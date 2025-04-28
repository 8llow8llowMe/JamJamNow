package com.jamjamnow.backend.global.infrastructor.batch.tasklet;

import com.jamjamnow.backend.domain.rawdata.entity.RawBusUsage;
import com.jamjamnow.backend.domain.rawdata.service.RawBusUsageService;
import com.jamjamnow.backend.domain.standard.entity.Sido;
import com.jamjamnow.backend.domain.standard.repository.SidoRepository;
import com.jamjamnow.backend.global.infrastructor.openapi.DynamicOpenApiClient;
import com.jamjamnow.backend.global.infrastructor.openapi.dto.BusUsageItem;
import com.jamjamnow.backend.global.infrastructor.openapi.dto.OpenApiDynamicWrapper;
import com.jamjamnow.backend.global.infrastructor.openapi.dto.OpenApiResponse;
import com.jamjamnow.backend.global.util.SnowflakeIdGenerator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RawBusUsageTasklet implements Tasklet {

    private static final int THREAD_POOL_SIZE = 5; // 스레드 5개
    private static final int PAGE_SIZE = 100;       // 한 페이지당 100건

    private final DynamicOpenApiClient openApiClient;
    private final RawBusUsageService rawBusUsageService;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final SidoRepository sidoRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
        throws Exception {
        String oprYmd = getOperationDate(chunkContext);

        log.info("[Batch] oprYmd={}", oprYmd);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<Sido> sidos = sidoRepository.findAll();

        for (Sido sido : sidos) {
            int totalPages = estimateTotalPages(sido.getCtpvCd(), oprYmd);
            log.info("[Batch] 시도코드={} 예상 페이지 수={}", sido.getCtpvCd(), totalPages);

            List<Callable<List<RawBusUsage>>> tasks = new ArrayList<>();

            for (int pageNo = 1; pageNo <= totalPages; pageNo++) {
                int finalPageNo = pageNo;
                tasks.add(() -> fetchData(oprYmd, sido.getCtpvCd(), finalPageNo));
            }

            List<Future<List<RawBusUsage>>> futures = executor.invokeAll(tasks);

            int successCount = 0;
            int failCount = 0;

            for (Future<List<RawBusUsage>> future : futures) {
                try {
                    List<RawBusUsage> usages = future.get();
                    if (!usages.isEmpty()) {
                        rawBusUsageService.saveAllForce(usages);
                        successCount += usages.size();
                    }
                } catch (Exception e) {
                    log.error("[Batch] 데이터 저장 실패", e);
                    failCount++;
                }
            }

            log.info("[Batch] 시도코드={} 저장 완료 - 성공: {}, 실패: {}", sido.getCtpvCd(), successCount,
                failCount);
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        return RepeatStatus.FINISHED;
    }

    private String getOperationDate(ChunkContext chunkContext) {
        String oprYmd = (String) chunkContext.getStepContext()
            .getJobParameters()
            .get("oprYmd");

        if (oprYmd == null) {
            LocalDate defaultDate = LocalDate.now().minusMonths(1);
            oprYmd = defaultDate.format(DateTimeFormatter.BASIC_ISO_DATE);
        }
        return oprYmd;
    }

    private List<RawBusUsage> fetchData(String oprYmd, String ctpvCd, int pageNo) {
        Map<String, String> params = new HashMap<>();
        params.put("opr_ymd", oprYmd);
        params.put("ctpv_cd", ctpvCd);
        params.put("pageNo", String.valueOf(pageNo));

        OpenApiDynamicWrapper wrapper = openApiClient.fetch("transport", params,
            OpenApiDynamicWrapper.class);

        OpenApiResponse openApiResponse = Optional.ofNullable(wrapper.getFirstResponse())
            .orElseThrow(() -> new IllegalStateException("OpenAPI 응답이 비어있습니다."));

        List<BusUsageItem> items = Optional.ofNullable(openApiResponse.body())
            .map(OpenApiResponse.Body::items)
            .map(OpenApiResponse.Body.Items::item)
            .orElse(List.of());

        return items.stream()
            .map(item -> RawBusUsage.builder()
                .id(snowflakeIdGenerator.generateId())
                .oprYmd(parseDate(item.opr_ymd()))
                .dowNm(item.dow_nm())
                .ctpvCd(item.ctpv_cd())
                .ctpvNm(item.ctpv_nm())
                .sggCd(item.sgg_cd())
                .sggNm(item.sgg_nm())
                .emdCd(item.emd_cd())
                .emdNm(item.emd_nm())
                .rteId(item.rte_id())
                .sttnId(item.sttn_id())
                .usersTypeNm(item.users_type_nm())
                .utztnNope(item.utztn_nope())
                .build())
            .collect(Collectors.toList());
    }

    private int estimateTotalPages(String ctpvCd, String oprYmd) {
        // 총 몇 페이지인지 알아야 멀티스레드로 돌릴 수 있다.
        Map<String, String> params = new HashMap<>();
        params.put("opr_ymd", oprYmd);
        params.put("ctpv_cd", ctpvCd);
        params.put("pageNo", "1");

        OpenApiDynamicWrapper wrapper = openApiClient.fetch("transport", params,
            OpenApiDynamicWrapper.class);

        OpenApiResponse openApiResponse = Optional.ofNullable(wrapper.getFirstResponse())
            .orElseThrow(() -> new IllegalStateException("OpenAPI 응답이 비어있습니다."));

        int totalCount = openApiResponse.body().totalCount();
        return (int) Math.ceil(totalCount / (double) PAGE_SIZE);
    }

    private LocalDate parseDate(String yyyymmdd) {
        if (yyyymmdd == null || yyyymmdd.length() != 8) {
            throw new IllegalArgumentException("잘못된 날짜 형식: " + yyyymmdd);
        }
        return LocalDate.of(
            Integer.parseInt(yyyymmdd.substring(0, 4)),
            Integer.parseInt(yyyymmdd.substring(4, 6)),
            Integer.parseInt(yyyymmdd.substring(6, 8))
        );
    }
}
