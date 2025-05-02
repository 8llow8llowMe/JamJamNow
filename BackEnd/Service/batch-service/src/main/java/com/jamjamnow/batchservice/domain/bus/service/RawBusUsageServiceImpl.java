package com.jamjamnow.batchservice.domain.bus.service;

import com.jamjamnow.batchservice.domain.bus.entity.RawBusUsage;
import com.jamjamnow.batchservice.domain.bus.repository.RawBusUsageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RawBusUsageServiceImpl implements RawBusUsageService {

    private static final int CHUNK_SIZE = 1000;
    private final RawBusUsageRepository rawBusUsageRepository;

    @Override
    public void saveAllForce(List<RawBusUsage> usages) {
        if (usages.isEmpty()) {
            return;
        }

        List<RawBusUsage> filtered = usages.stream()
            .filter(this::isValid)
            .toList();

        int skipped = usages.size() - filtered.size();
        if (skipped > 0) {
            log.warn("[Batch] 유효하지 않은 데이터 {}건은 저장 대상에서 제외됨", skipped);
        }

        if (filtered.isEmpty()) {
            return;
        }

        try {
            rawBusUsageRepository.saveAll(filtered);
            rawBusUsageRepository.flush();  // Hibernate batch insert
        } catch (Exception e) {
            log.warn("[Batch] 중복 insert 무시 - 일부 데이터는 이미 존재합니다.", e);
        }
    }

    private boolean isValid(RawBusUsage rawBusUsage) {
        return rawBusUsage.getOprYmd() != null
            && isNotBlank(rawBusUsage.getDowNm())
            && isNotBlank(rawBusUsage.getCtpvCd())
            && isNotBlank(rawBusUsage.getCtpvNm())
            && isNotBlank(rawBusUsage.getSggCd())
            && isNotBlank(rawBusUsage.getSggNm())
            && isNotBlank(rawBusUsage.getEmdCd())
            && isNotBlank(rawBusUsage.getEmdNm())
            && isNotBlank(rawBusUsage.getRteId())
            && isNotBlank(rawBusUsage.getSttnId())
            && isNotBlank(rawBusUsage.getUsersTypeNm())
            && rawBusUsage.getUtztnNope() != null;
    }

    private boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty() && !s.trim().equals("~");
    }
}
