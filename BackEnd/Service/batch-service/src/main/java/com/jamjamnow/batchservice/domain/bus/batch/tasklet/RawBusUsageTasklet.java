package com.jamjamnow.batchservice.domain.bus.batch.tasklet;

import com.jamjamnow.batchservice.domain.bus.entity.RawBusUsage;
import com.jamjamnow.batchservice.domain.bus.service.RawBusUsageService;
import com.jamjamnow.batchservice.global.infrastructor.openapi.DynamicOpenApiClient;
import com.jamjamnow.batchservice.global.infrastructor.openapi.dto.OpenApiGenericResponse;
import com.jamjamnow.batchservice.global.infrastructor.openapi.dto.OpenApiGenericResponse.Body;
import com.jamjamnow.batchservice.global.infrastructor.openapi.dto.OpenApiGenericResponse.Body.Items;
import com.jamjamnow.batchservice.global.infrastructor.openapi.dto.OpenApiRoot;
import com.jamjamnow.batchservice.global.infrastructor.openapi.dto.bus.BusUsageItem;
import com.jamjamnow.persistencemodule.domain.standard.entity.Sido;
import com.jamjamnow.persistencemodule.domain.standard.repository.SidoRepository;
import com.jamjamnow.persistencemodule.global.util.SnowflakeIdGenerator;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RawBusUsageTasklet implements Tasklet {

    public static final ParameterizedTypeReference<OpenApiRoot<BusUsageItem>> BUS_USAGE_TYPE =
        new ParameterizedTypeReference<>() {
        };

    private static final String CATEGORY = "bus";
    private static final String NAME = "usage";
    // 동시 실행을 제한할 가상 스레드 세마포어 임계값 (한 번에 5개 페이지 처리)
    private static final int CONCURRENCY_LIMIT = 5;

    private final DynamicOpenApiClient openApiClient;
    private final RawBusUsageService rawBusUsageService;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final SidoRepository sidoRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
        throws Exception {
        // 1. 배치 Job 파라미터에서 날짜 및 시도코드 파라미터 추출
        String oprYmd = (String) chunkContext.getStepContext().getJobParameters().get("oprYmd");
        String sidoParam = (String) chunkContext.getStepContext().getJobParameters().get("sido");

        // 2. 수집 대상 시도코드 목록 결정 (입력 없으면 전체 시도)
        List<String> targetSidoCodes = getTargetSidoCodes(sidoParam);
        log.info("[Batch] [BusUsage] 수집 시작 - oprYmd={}, 시도코드={}", oprYmd, targetSidoCodes);

        // 3. 가상 스레드 기반 Executor 생성
        try (ExecutorService executor = Executors.newThreadPerTaskExecutor(
            Thread.ofVirtual().factory())) {
            List<CompletableFuture<Void>> tasks = targetSidoCodes.stream()
                .map(ctpvCd -> CompletableFuture.runAsync(() -> collectBySido(ctpvCd, oprYmd),
                    executor))
                .toList();

            // 4. 모든 시도코드 수집 완료 대기
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        }

        return RepeatStatus.FINISHED;
    }

    private void collectBySido(String ctpvCd, String oprYmd) {
        log.info("[Batch] [BusUsage] 시작 - 시도코드={} 날짜={}", ctpvCd, oprYmd);

        int page = 1;
        int totalSaved = 0;

        while (true) {
            int saved = fetchAndSave(ctpvCd, oprYmd, page++);
            if (saved == 0) {
                break;
            }
            totalSaved += saved;
        }

        log.info("[Batch] [BusUsage] 완료 - 시도코드={} 날짜={} 저장 건수={}", ctpvCd, oprYmd, totalSaved);
    }

    private int fetchAndSave(String ctpvCd, String oprYmd, int pageNo) {
        try {
            Map<String, String> params = Map.of(
                "opr_ymd", oprYmd,
                "ctpv_cd", ctpvCd,
                "pageNo", String.valueOf(pageNo)
            );

            OpenApiGenericResponse<BusUsageItem> response = openApiClient
                .fetch(CATEGORY, NAME, params, BUS_USAGE_TYPE)
                .response();

            List<BusUsageItem> items = Optional.ofNullable(response)
                .map(OpenApiGenericResponse::body)
                .map(Body::items)
                .map(Items::item)
                .orElse(List.of());

            if (items.isEmpty()) {
                return 0;
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

            rawBusUsageService.saveAll(entities);
            return entities.size();

        } catch (Exception e) {
            log.error("[Batch] [BusUsage] 오류 - 시도={} 날짜={} page={}", ctpvCd, oprYmd, pageNo, e);
            return 0;
        }
    }

    private List<String> getTargetSidoCodes(String sidoParam) {
        return (sidoParam != null && !sidoParam.isBlank())
            ? Arrays.stream(sidoParam.split(",")).map(String::trim).toList()
            : sidoRepository.findAll().stream().map(Sido::getCtpvCd).toList();
    }

    private LocalDate parseDate(String yyyymmdd) {
        return LocalDate.of(
            Integer.parseInt(yyyymmdd.substring(0, 4)),
            Integer.parseInt(yyyymmdd.substring(4, 6)),
            Integer.parseInt(yyyymmdd.substring(6, 8))
        );
    }
}
