package com.jamjamnow.batchservice.global.infrastructor.batch.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;

@Slf4j
@RequiredArgsConstructor
public class RawBusUsageJobRunner implements CommandLineRunner {

    private final JobLauncher jobLauncher;
    private final Job rawBusUsageJob;

    @Override
    public void run(String... args) throws Exception {
        String oprYmd = null;
        String sidoArg = null;

        // 1. 인자 파싱
        for (String arg : args) {
            if (arg.startsWith("--oprYmd=")) {
                oprYmd = arg.substring("--oprYmd=".length());
            } else if (arg.startsWith("--sido=")) {
                sidoArg = arg.substring("--sido=".length());
            }
        }

        // 2. 인자 없으면 실행하지 않음
        if (oprYmd == null || oprYmd.isBlank()) {
            log.info("[CLI] --oprYmd 인자가 없으므로 배치 작업을 실행하지 않습니다.");
            return;
        }

        log.info("[CLI] 수집 날짜: {}, 시도코드: {}", oprYmd, sidoArg);

        JobParameters jobParameters = new JobParametersBuilder()
            .addString("oprYmd", oprYmd)
            .addString("sido", sidoArg)
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters();

        jobLauncher.run(rawBusUsageJob, jobParameters);
    }
}
