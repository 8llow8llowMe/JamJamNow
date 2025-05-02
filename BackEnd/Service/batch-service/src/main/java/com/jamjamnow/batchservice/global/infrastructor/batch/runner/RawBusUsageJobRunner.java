package com.jamjamnow.batchservice.global.infrastructor.batch.runner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RawBusUsageJobRunner implements CommandLineRunner {

    private final JobLauncher jobLauncher;
    private final Job rawBusUsageJob;

    @Override
    public void run(String... args) throws Exception {
        log.info("RawBusUsageBatchJob 실행 시작!");

        // 기본 oprYmd는 오늘-1개월
        String oprYmd = LocalDate.now().minusMonths(1)
            .format(DateTimeFormatter.BASIC_ISO_DATE);

        // CLI 인자에서 --oprYmd=20250328 같은거 파싱
        for (String arg : args) {
            if (arg.startsWith("--oprYmd=")) {
                oprYmd = arg.substring("--oprYmd=".length());
                break;
            }
        }

        log.info("[Batch] oprYmd 인자 적용: {}", oprYmd);

        JobParameters jobParameters = new JobParametersBuilder()
            .addString("oprYmd", oprYmd)
            .addLong("timestamp", System.currentTimeMillis()) // JobInstance 구분용
            .toJobParameters();

        jobLauncher.run(rawBusUsageJob, jobParameters);

        log.info("RawBusUsageBatchJob 실행 완료!");
    }
}
