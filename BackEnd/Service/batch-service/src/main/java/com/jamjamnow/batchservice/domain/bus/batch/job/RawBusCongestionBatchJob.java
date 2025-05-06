package com.jamjamnow.batchservice.domain.bus.batch.job;

import com.jamjamnow.batchservice.domain.bus.batch.tasklet.RawBusCongestionTasklet;
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
public class RawBusCongestionBatchJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RawBusCongestionTasklet rawBusCongestionTasklet;

    @Bean
    public Job rawBusCongestionJob() {
        return new JobBuilder("rawBusCongestionJob", jobRepository)
            .start(rawBusCongestionStep())
            .build();
    }

    @Bean
    public Step rawBusCongestionStep() {
        return new StepBuilder("rawBusCongestionStep", jobRepository)
            .tasklet(rawBusCongestionTasklet, transactionManager)
            .build();
    }

//    @Bean
//    public CommandLineRunner rawBusCongestionJobRunner(JobLauncher jobLauncher,
//        Job rawBusCongestionJob) {
//        return new RawBusCongestionJobRunner(jobLauncher, rawBusCongestionJob);
//    }
}
