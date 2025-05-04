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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
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

    private static final int CONCURRENCY_LIMIT = 5;

    private final DynamicOpenApiClient openApiClient;
    private final RawBusUsageService rawBusUsageService;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final SidoRepository sidoRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
        throws Exception {
        String oprYmd = (String) chunkContext.getStepContext().getJobParameters().get("oprYmd");
        String sidoParam = (String) chunkContext.getStepContext().getJobParameters().get("sido");

        List<String> targetSidoCodes = getTargetSidoCodes(sidoParam);
        log.info("[Batch] oprYmd={}, 시도코드 목록={}", oprYmd, targetSidoCodes);

        for (String ctpvCd : targetSidoCodes) {
            collectByRegionVirtual(ctpvCd, oprYmd);
        }

        return RepeatStatus.FINISHED;
    }

    private void collectByRegionVirtual(String ctpvCd, String oprYmd) throws InterruptedException {
        log.info("[Batch] 시도코드={} 날짜={} 수집 시작", ctpvCd, oprYmd);

        int totalPages = getTotalPages(ctpvCd, oprYmd);
        if (totalPages == 0) {
            log.warn("[Batch] 수집할 데이터 없음 - 시도코드={} 날짜={}", ctpvCd, oprYmd);
            return;
        }

        Semaphore semaphore = new Semaphore(CONCURRENCY_LIMIT);
        ExecutorService virtualThreadExecutor = Executors.newThreadPerTaskExecutor(
            Thread.ofVirtual().factory());
        List<CompletableFuture<Integer>> futures = new ArrayList<>();

        for (int pageNo = 1; pageNo <= totalPages; pageNo++) {
            final int finalPage = pageNo;
            semaphore.acquire();

            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return fetchAndSave(ctpvCd, oprYmd, finalPage);
                } finally {
                    semaphore.release();
                }
            }, virtualThreadExecutor);

            futures.add(future);
        }

        // 모든 작업 완료 대기 및 결과 집계
        int totalSaved = futures.stream()
            .mapToInt(f -> {
                try {
                    return f.join();
                } catch (Exception e) {
                    log.error("[Batch] 수집 중 오류", e);
                    return 0;
                }
            }).sum();

        virtualThreadExecutor.shutdown(); // ⭐ 가상 스레드 Executor 종료
        log.info("[Batch] 시도코드={} 날짜={} 최종 저장 건수={}", ctpvCd, oprYmd, totalSaved);
    }

    private int fetchAndSave(String ctpvCd, String oprYmd, int pageNo) {
        try {
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
                return 0;
            }

            List<BusUsageItem> items = Optional.ofNullable(res.body().items().item())
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

            rawBusUsageService.saveAllIgnoreDuplicates(entities);
            return entities.size();

        } catch (Exception e) {
            log.error("[Batch] 시도={} 날짜={} page={} 수집 중 에러", ctpvCd, oprYmd, pageNo, e);
            return 0;
        }
    }

    private int getTotalPages(String ctpvCd, String oprYmd) {
        try {
            Map<String, String> params = Map.of(
                "opr_ymd", oprYmd,
                "ctpv_cd", ctpvCd,
                "pageNo", "1"
            );

            OpenApiResponse res = Optional.ofNullable(
                    openApiClient.fetch("transport", params, OpenApiDynamicWrapper.class))
                .map(OpenApiDynamicWrapper::getFirstResponse)
                .orElse(null);

            if (res == null || res.body() == null || res.body().totalCount() <= 0) {
                return 0;
            }

            int totalCount = res.body().totalCount();
            int totalPages = (int) Math.ceil(totalCount / 100.0);
            log.info("[Batch] 시도={} 날짜={} 전체 건수={}, 총 페이지={}", ctpvCd, oprYmd, totalCount,
                totalPages);
            return totalPages;

        } catch (Exception e) {
            log.error("[Batch] 시도={} 날짜={} 전체 페이지 조회 실패", ctpvCd, oprYmd, e);
            return 0;
        }
    }

    private List<String> getTargetSidoCodes(String sidoParam) {
        if (sidoParam != null && !sidoParam.isBlank()) {
            return Arrays.stream(sidoParam.split(",")).map(String::trim).toList();
        } else {
            return sidoRepository.findAll().stream()
                .map(Sido::getCtpvCd)
                .toList();
        }
    }

    private LocalDate parseDate(String yyyymmdd) {
        return LocalDate.of(
            Integer.parseInt(yyyymmdd.substring(0, 4)),
            Integer.parseInt(yyyymmdd.substring(4, 6)),
            Integer.parseInt(yyyymmdd.substring(6, 8))
        );
    }
}
