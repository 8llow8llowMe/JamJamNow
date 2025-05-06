//package com.jamjamnow.batchservice.domain.bus.batch.runner;
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
//public class RawBusCongestionJobRunner implements CommandLineRunner {
//
//    private final JobLauncher jobLauncher;
//    private final Job rawBusCongestionJob;
//
//    @Override
//    public void run(String... args) throws Exception {
//        String oprYmd = null;
//        String sidoArg = null;
//
//        for (String arg : args) {
//            if (arg.startsWith("--oprYmd=")) {
//                oprYmd = arg.substring("--oprYmd=".length());
//            } else if (arg.startsWith("--sido=")) {
//                sidoArg = arg.substring("--sido=".length());
//            }
//        }
//
//        if (oprYmd == null || oprYmd.isBlank()) {
//            log.info("[CLI] --oprYmd 인자가 없으므로 배치 작업 실행 안 함");
//            return;
//        }
//
//        JobParametersBuilder builder = new JobParametersBuilder()
//            .addString("oprYmd", oprYmd)
//            .addLong("timestamp", System.currentTimeMillis());
//
//        if (sidoArg != null && !sidoArg.isBlank()) {
//            builder.addString("sido", sidoArg);
//        }
//
//        jobLauncher.run(rawBusCongestionJob, builder.toJobParameters());
//    }
//}
