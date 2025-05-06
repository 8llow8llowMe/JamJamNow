//package com.jamjamnow.batchservice.domain.standard.batch.runner;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobParametersBuilder;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.boot.CommandLineRunner;
//
//@Slf4j
//@RequiredArgsConstructor
//public class EmdJobRunner implements CommandLineRunner {
//
//    private final JobLauncher jobLauncher;
//    private final Job emdJob;
//
//    @Override
//    public void run(String... args) throws Exception {
//        String jobName = null;
//
//        for (String arg : args) {
//            if (arg.startsWith("--job=")) {
//                jobName = arg.substring("--job=".length());
//            }
//        }
//
//        if (!"insert-emd".equals(jobName)) {
//            log.info("[EmdJobRunner] --job=insert-emd 가 아니므로 실행 안 함");
//            return;
//        }
//
//        // 실행
//        jobLauncher.run(emdJob, new JobParametersBuilder()
//            .addLong("timestamp", System.currentTimeMillis()) // JobInstance 중복 방지
//            .toJobParameters());
//    }
//}
