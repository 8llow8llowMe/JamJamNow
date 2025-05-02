package com.jamjamnow.batchservice.global.infrastructor.batch.tasklet;

import com.jamjamnow.batchservice.domain.bus.entity.RawBusUsage;
import com.jamjamnow.batchservice.domain.bus.service.RawBusUsageService;
import com.jamjamnow.batchservice.global.infrastructor.openapi.DynamicOpenApiClient;
import com.jamjamnow.batchservice.global.infrastructor.openapi.dto.BusUsageItem;
import com.jamjamnow.batchservice.global.infrastructor.openapi.dto.OpenApiDynamicWrapper;
import com.jamjamnow.batchservice.global.infrastructor.openapi.dto.OpenApiResponse;
import com.jamjamnow.persistencemodule.domain.standard.entity.Sido;
import com.jamjamnow.persistencemodule.domain.standard.repository.SidoRepository;
import com.jamjamnow.persistencemodule.global.util.SnowflakeIdGenerator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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

    private static final int THREAD_POOL_SIZE = 1;
    private static final List<String> TARGET_SIDO_CODES = List.of("11", "31", "23"); // 서울, 경기, 인천

    private final DynamicOpenApiClient openApiClient;
    private final RawBusUsageService rawBusUsageService;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final SidoRepository sidoRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
        throws Exception {
        LocalDate from = getStartDate(chunkContext);
        LocalDate to = getEndDate(chunkContext, from);

        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE)) {

            for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
                String oprYmd = date.format(DateTimeFormatter.BASIC_ISO_DATE);
                log.info("[Batch] 날짜={} 수집 시작", oprYmd);

                List<Sido> sidoList = sidoRepository.findAll().stream()
                    .filter(sido -> TARGET_SIDO_CODES.contains(sido.getCtpvCd()))
                    .toList();

                List<Callable<Integer>> tasks = new ArrayList<>();

                for (Sido sido : sidoList) {
                    tasks.add(() -> collectByRegion(sido.getCtpvCd(), oprYmd));
                }

                List<Future<Integer>> results = executor.invokeAll(tasks);

                int total = 0;
                for (Future<Integer> result : results) {
                    total += result.get();
                }

                log.info("[Batch] 날짜={} 총 저장 건수={}", oprYmd, total);
            }

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.HOURS);
        }

        return RepeatStatus.FINISHED;
    }

    private int collectByRegion(String ctpvCd, String oprYmd) {
        int totalSaved = 0;
        int pageNo = 1;
        int totalPages = 1; // 기본값

        while (pageNo <= totalPages) {
            log.info("[Batch] 수집 중: 날짜={}, 시도코드={}, pageNo={}", oprYmd, ctpvCd, pageNo);

            try {
                Thread.sleep(300); // 호출 간 지연
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("딜레이 중단됨", e);
            }

            Map<String, String> params = Map.of(
                "opr_ymd", oprYmd,
                "ctpv_cd", ctpvCd,
                "pageNo", String.valueOf(pageNo)
            );

            OpenApiResponse res = Optional.ofNullable(
                    openApiClient.fetch("transport", params, OpenApiDynamicWrapper.class))
                .map(OpenApiDynamicWrapper::getFirstResponse)
                .orElse(null);

            if (res == null || res.body() == null || res.body().items() == null) {
                break;
            }

            // 첫 페이지에서 totalCount 기반 페이지 수 계산
            int totalCount = res.body().totalCount();  // null 불가, int 타입일 경우 항상 값이 있음
            if (pageNo == 1 && totalCount > 0) {
                totalPages = (int) Math.ceil(totalCount / 100.0);
                log.info("전체 건수={}, 총 페이지={}", totalCount, totalPages);
            } else if (pageNo == 1 && totalCount <= 0) {
                log.warn("totalCount가 0이거나 잘못된 응답입니다. 시도코드={}, 날짜={}", ctpvCd, oprYmd);
                break;
            }

            List<BusUsageItem> items = Optional.ofNullable(res.body().items().item())
                .orElse(List.of());
            if (items.isEmpty()) {
                break;
            }

            List<RawBusUsage> entities = items.stream()
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
                .toList();

            rawBusUsageService.saveAllForce(entities);
            totalSaved += entities.size();
            pageNo++;
        }

        return totalSaved;
    }

    private LocalDate getStartDate(ChunkContext context) {
        String from = (String) context.getStepContext().getJobParameters().get("from");
        if (from != null) {
            return LocalDate.parse(from, DateTimeFormatter.BASIC_ISO_DATE);
        }
        return LocalDate.now().minusDays(30);
    }

    private LocalDate getEndDate(ChunkContext context, LocalDate defaultFrom) {
        String to = (String) context.getStepContext().getJobParameters().get("to");
        if (to != null) {
            return LocalDate.parse(to, DateTimeFormatter.BASIC_ISO_DATE);
        }
        return defaultFrom.plusDays(2);  // 기본 자동 수집 3일치
    }

    private LocalDate parseDate(String yyyymmdd) {
        return LocalDate.of(
            Integer.parseInt(yyyymmdd.substring(0, 4)),
            Integer.parseInt(yyyymmdd.substring(4, 6)),
            Integer.parseInt(yyyymmdd.substring(6, 8))
        );
    }
}
