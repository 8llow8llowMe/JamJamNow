package com.jamjamnow.batchservice.domain.bus.batch.scheduler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RawBusCongestionScheduler {

    private final JobLauncher jobLauncher;
    private final Job rawBusCongestionJob;

    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시
    public void runDailyJob() {
        String oprYmd = LocalDate.now().minusMonths(1)
            .format(DateTimeFormatter.BASIC_ISO_DATE);

        JobParameters params = new JobParametersBuilder()
            .addString("oprYmd", oprYmd)
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters();

        try {
            jobLauncher.run(rawBusCongestionJob, params);
        } catch (Exception e) {
            log.error("[Scheduler] RawBusCongestion 배치 실패", e);
        }
    }
}
