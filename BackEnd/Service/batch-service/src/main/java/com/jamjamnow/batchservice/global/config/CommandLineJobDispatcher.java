package com.jamjamnow.batchservice.global.config;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CommandLineJobDispatcher {

    private final JobLauncher jobLauncher;
    private final Map<String, Job> jobs;

    @Bean
    public CommandLineRunner jobRunner() {
        return args -> {
            String oprYmd = null;
            String sido = null;
            String jobName = null;

            for (String arg : args) {
                if (arg.startsWith("--oprYmd=")) {
                    oprYmd = arg.substring("--oprYmd=".length());
                } else if (arg.startsWith("--sido=")) {
                    sido = arg.substring("--sido=".length());
                } else if (arg.startsWith("--job=")) {
                    jobName = arg.substring("--job=".length());
                }
            }

            if (jobName == null || oprYmd == null) {
                log.warn("[CLI] --job 또는 --oprYmd 인자가 없어 배치 작업 실행 안 함");
                return;
            }

            Job job = jobs.get(jobName);
            if (job == null) {
                log.error("[CLI] 등록되지 않은 job: {}", jobName);
                return;
            }

            var builder = new JobParametersBuilder()
                .addString("oprYmd", oprYmd)
                .addLong("timestamp", System.currentTimeMillis());

            if (sido != null) {
                builder.addString("sido", sido);
            }

            log.info("[CLI] 실행할 Job: {}, 날짜: {}, 시도: {}", jobName, oprYmd, sido);
            jobLauncher.run(job, builder.toJobParameters());
        };
    }
}
