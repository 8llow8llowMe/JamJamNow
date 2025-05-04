package com.jamjamnow.batchservice.domain.standard.batch.job;

import com.jamjamnow.batchservice.domain.standard.batch.runner.EmdJobRunner;
import com.jamjamnow.batchservice.domain.standard.batch.tasklet.EmdTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class EmdBatchJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EmdTasklet emdTasklet;

    @Bean
    public Job emdJob() {
        return new JobBuilder("emdJob", jobRepository)
            .start(emdStep())
            .build();
    }

    @Bean
    public Step emdStep() {
        return new StepBuilder("emdStep", jobRepository)
            .tasklet(emdTasklet, transactionManager)
            .build();
    }

    @Bean
    public CommandLineRunner emdJobRunner(JobLauncher jobLauncher, Job emdJob) {
        return new EmdJobRunner(jobLauncher, emdJob);
    }
}
