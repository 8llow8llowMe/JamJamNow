package com.jamjamnow.batchservice.domain.standard.batch.tasklet;

import com.jamjamnow.persistencemodule.domain.standard.repository.SidoBoundaryRepository;
import com.jamjamnow.persistencemodule.domain.standard.repository.SidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SidoBoundaryTasklet implements Tasklet {

    private final SidoBoundaryRepository sidoBoundaryRepository;
    private final SidoRepository sidoRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
        throws Exception {

        log.info("[SidoBoundaryTasklet] 시도 경계 데이터 삽입 시작");

        return null;
    }
}
