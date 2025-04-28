package com.jamjamnow.backend.global.infrastructor.batch.job;

import com.jamjamnow.backend.global.infrastructor.batch.tasklet.RawBusUsageTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class RawBusUsageBatchJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RawBusUsageTasklet rawBusUsageTasklet;

    @Bean
    public Job rawBusUsageJob() {
        return new JobBuilder("rawBusUsageJob", jobRepository)
            .start(rawBusUsageStep())
            .build();
    }

    @Bean
    public Step rawBusUsageStep() {
        return new StepBuilder("rawBusUsageStep", jobRepository)
            .tasklet(rawBusUsageTasklet, transactionManager)
            .build();
    }
}
