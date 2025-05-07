package com.jamjamnow.batchservice.domain.bus.batch.tasklet;

import com.jamjamnow.batchservice.domain.bus.entity.RawBusCongestion;
import com.jamjamnow.batchservice.domain.bus.service.RawBusCongestionService;
import com.jamjamnow.batchservice.global.infrastructor.openapi.DynamicOpenApiClient;
import com.jamjamnow.batchservice.global.infrastructor.openapi.dto.OpenApiGenericResponse;
import com.jamjamnow.batchservice.global.infrastructor.openapi.dto.OpenApiGenericResponse.Body;
import com.jamjamnow.batchservice.global.infrastructor.openapi.dto.OpenApiGenericResponse.Body.Items;
import com.jamjamnow.batchservice.global.infrastructor.openapi.dto.OpenApiRoot;
import com.jamjamnow.batchservice.global.infrastructor.openapi.dto.bus.BusCongestionItem;
import com.jamjamnow.persistencemodule.domain.standard.entity.Sgg;
import com.jamjamnow.persistencemodule.domain.standard.entity.Sido;
import com.jamjamnow.persistencemodule.domain.standard.repository.SggRepository;
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
public class RawBusCongestionTasklet implements Tasklet {

    private static final String CATEGORY = "bus";
    private static final String NAME = "congestion";

    private static final ParameterizedTypeReference<OpenApiRoot<BusCongestionItem>> BUS_CONGESTION_TYPE =
        new ParameterizedTypeReference<>() {
        };

    private final DynamicOpenApiClient openApiClient;
    private final RawBusCongestionService rawBusCongestionService;
    private final SidoRepository sidoRepository;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final SggRepository sggRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
        throws Exception {
        String oprYmd = (String) chunkContext.getStepContext().getJobParameters().get("oprYmd");
        String sidoParam = (String) chunkContext.getStepContext().getJobParameters().get("sido");

        List<String> targetSidoCodes = getTargetSidoCodes(sidoParam);
        log.info("[Batch] [BusCongestion] oprYmd={}, 시도코드 목록={}", oprYmd, targetSidoCodes);

        try (ExecutorService executor = Executors.newThreadPerTaskExecutor(
            Thread.ofVirtual().name("bus-congestion-", 0).factory())) {

            // 전체 시군구 리스트 수집
            List<Sgg> sggList = targetSidoCodes.stream()
                .flatMap(code -> sggRepository.findBySido_CtpvCd(code).stream())
                .toList();

            List<CompletableFuture<Void>> tasks = sggList.stream()
                .map(sgg -> CompletableFuture.runAsync(() -> {
                    try {
                        collectByRegion(sgg.getSido().getCtpvCd(), sgg.getSggCd(), oprYmd);
                    } catch (Exception e) {
                        log.error("[Batch] [BusCongestion] 수집 실패 - {}, {}", sgg.getSggCd(),
                            e.getMessage(), e);
                    }
                }, executor))
                .toList();

            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        }

        return RepeatStatus.FINISHED;
    }

    private void collectByRegion(String ctpvCd, String sggCd, String oprYmd) {
        log.info("[Batch] [BusCongestion] 시작 - 시도={}, 시군구={}, 날짜={}", ctpvCd, sggCd, oprYmd);
        int pageNo = 1;
        int totalSaved = 0;

        while (true) {
            int saved = fetchAndSave(ctpvCd, sggCd, oprYmd, pageNo++);
            if (saved == 0) {
                break;
            }
            totalSaved += saved;
        }

        log.info("[Batch] [BusCongestion] 완료 - 시도={}, 시군구={}, 날짜={}, 저장 건수={}", ctpvCd, sggCd,
            oprYmd, totalSaved);
    }

    private int fetchAndSave(String ctpvCd, String sggCd, String oprYmd, int pageNo) {
        try {
            Map<String, String> params = Map.of(
                "opr_ymd", oprYmd,
                "ctpv_cd", ctpvCd,
                "sgg_cd", sggCd,
                "pageNo", String.valueOf(pageNo)
            );

            OpenApiGenericResponse<BusCongestionItem> response = openApiClient
                .fetch(CATEGORY, NAME, params, BUS_CONGESTION_TYPE)
                .response();

            List<BusCongestionItem> items = Optional.ofNullable(response)
                .map(OpenApiGenericResponse::body)
                .map(Body::items)
                .map(Items::item)
                .orElse(List.of());

            if (items.isEmpty()) {
                return 0;
            }

            List<RawBusCongestion> entities = items.stream()
                .map(item -> RawBusCongestion.builder()
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
                    .sttnSeq(item.sttn_seq())
                    .sttnId(item.sttn_id())
                    .tzon(item.tzon())
                    .cgst(item.cgst())
                    .build())
                .toList();

            rawBusCongestionService.saveAll(entities);
            return entities.size();

        } catch (Exception e) {
            log.error("[Batch] 수집 오류 - 시도={}, 시군구={}, 날짜={}, 페이지={}", ctpvCd, sggCd, oprYmd, pageNo,
                e);
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
