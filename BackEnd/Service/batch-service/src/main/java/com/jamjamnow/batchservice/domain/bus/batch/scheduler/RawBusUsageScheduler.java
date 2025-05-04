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
public class RawBusUsageScheduler {

    private final JobLauncher jobLauncher;
    private final Job rawBusUsageJob;

    @Scheduled(cron = "0 0 2 * * *") // 매일 새벽 2시 실행
    public void runDailyJob() {
        String oprYmd = LocalDate.now().minusMonths(1)
            .format(DateTimeFormatter.BASIC_ISO_DATE);

        log.info("[Scheduler] 하루치 수집 시작: oprYmd={}", oprYmd);


        JobParameters jobParameters = new JobParametersBuilder()
            .addString("oprYmd", oprYmd)
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters();

        try {
            jobLauncher.run(rawBusUsageJob, jobParameters);
        } catch (Exception e) {
            log.error("[Scheduler] 배치 실행 실패: {}", e.getMessage(), e);
        }
    }
}
