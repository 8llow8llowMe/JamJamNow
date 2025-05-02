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

@Slf4j
@RequiredArgsConstructor
public class RawBusUsageJobRunner implements CommandLineRunner {

    private final JobLauncher jobLauncher;
    private final Job rawBusUsageJob;

    @Override
    public void run(String... args) throws Exception {
        String oprYmd = LocalDate.now().minusMonths(1)
            .format(DateTimeFormatter.BASIC_ISO_DATE);

        for (String arg : args) {
            if (arg.startsWith("--oprYmd=")) {
                oprYmd = arg.substring("--oprYmd=".length());
            }
        }

        log.info("[CLI] 수집 날짜: {}", oprYmd);

        JobParameters jobParameters = new JobParametersBuilder()
            .addString("oprYmd", oprYmd)
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters();

        jobLauncher.run(rawBusUsageJob, jobParameters);
    }
}
