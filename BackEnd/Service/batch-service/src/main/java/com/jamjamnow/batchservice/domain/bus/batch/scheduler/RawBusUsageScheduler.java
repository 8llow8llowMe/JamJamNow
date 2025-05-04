package com.jamjamnow.batchservice.domain.bus.batch.scheduler;

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

<<<<<<< HEAD:BackEnd/Service/batch-service/src/main/java/com/jamjamnow/batchservice/domain/bus/batch/scheduler/RawBusUsageScheduler.java
    @Scheduled(cron = "0 0 2 * * *") // 매일 새벽 2시 실행
    public void runDailyJob() {
        String oprYmd = LocalDate.now().minusMonths(1)
            .format(DateTimeFormatter.BASIC_ISO_DATE);

        log.info("[Scheduler] 하루치 수집 시작: oprYmd={}", oprYmd);
=======
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
>>>>>>> 2c4fccf ([BE] fix: CLI를 통한 배치 작업 인자 운행일자 인자 넘겨야 돌아가게끔 설정):BackEnd/Service/batch-service/src/main/java/com/jamjamnow/batchservice/global/infrastructor/batch/runner/RawBusUsageJobRunner.java

        JobParameters jobParameters = new JobParametersBuilder()
            .addString("oprYmd", oprYmd)
            .addString("sido", sidoArg)
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters();

        try {
            jobLauncher.run(rawBusUsageJob, jobParameters);
        } catch (Exception e) {
            log.error("[Scheduler] 배치 실행 실패: {}", e.getMessage(), e);
        }
    }
}
