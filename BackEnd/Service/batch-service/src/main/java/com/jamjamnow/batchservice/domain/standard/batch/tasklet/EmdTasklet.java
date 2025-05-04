package com.jamjamnow.batchservice.domain.standard.batch.tasklet;

import com.jamjamnow.persistencemodule.domain.standard.entity.Emd;
import com.jamjamnow.persistencemodule.domain.standard.entity.Sgg;
import com.jamjamnow.persistencemodule.domain.standard.repository.EmdRepository;
import com.jamjamnow.persistencemodule.domain.standard.repository.SggRepository;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmdTasklet implements Tasklet {

    private final SggRepository sggRepository;
    private final EmdRepository emdRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
        throws Exception {

        log.info("[EmdTasklet] 읍면동 데이터 삽입 시작");
        ClassPathResource resource = new ClassPathResource(
            "data/standard/국토교통부_전국_법정동_20250415.csv");

        List<Emd> toSaves = new ArrayList<>();

        try (
            Reader reader = new InputStreamReader(resource.getInputStream(),
                StandardCharsets.UTF_8);
            CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT)
        ) {
            for (CSVRecord record : parser) {
                // 헤더 라인은 건너뜀
                if (record.getRecordNumber() == 1) {
                    continue;
                }

                // 안전한 접근을 위해 최소 필드 수 체크
                if (record.size() < 4) {
                    log.warn("필드 누락 - 라인: {}", record);
                    continue;
                }

                String emdCd = record.get(0).trim();
                String ctpvNm = record.get(1).trim();
                String sggCdPrefix = emdCd.substring(0, 5); // 시군구코드
                String emdNm = record.get(3).trim();

                if (emdNm.isBlank()) {
                    log.warn("빈 읍면동명 - 시도: {}, 시군구코드: {}, 법정동코드: {}", ctpvNm, sggCdPrefix, emdCd);
                    continue;
                }

                Optional<Sgg> sggOpt = sggRepository.findBySggCdAndSido_CtpvNm(sggCdPrefix, ctpvNm);
                if (sggOpt.isEmpty()) {
                    log.warn("시군구 없음 - 시도: {}, 시군구코드: {}, 읍면동: {}", ctpvNm, sggCdPrefix, emdNm);
                    continue;
                }

                Emd emd = Emd.builder()
                    .emdCd(emdCd)
                    .emdNm(emdNm)
                    .sgg(sggOpt.get())
                    .build();

                toSaves.add(emd);
            }
        }

        emdRepository.saveAll(toSaves);
        log.info("[EmdTasklet] 총 {}건 저장 완료", toSaves.size());
        return RepeatStatus.FINISHED;
    }
}
