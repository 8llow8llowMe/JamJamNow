package com.jamjamnow.backend.domain.rawdata.service;

import com.jamjamnow.backend.domain.rawdata.entity.RawBusUsage;
import com.jamjamnow.backend.domain.rawdata.repository.RawBusUsageRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RawBusUsageServiceImpl implements RawBusUsageService {

    private final RawBusUsageRepository repository;

    @Override
    public void saveAllIfNotExists(List<RawBusUsage> usages) {
        if (usages.isEmpty()) {
            return;
        }

        // 모두 같은 날짜로 들어온다고 가정
        LocalDate date = usages.getFirst().getOprYmd();

        // 1) 이 배치에 등장하는 정류장ID 목록 추출
        Set<String> stationIds = usages.stream()
            .map(RawBusUsage::getSttnId)
            .collect(Collectors.toSet());

        // 2) DB 에서 한 번에 조회
        List<RawBusUsage> existing = repository.findAllByOprYmdAndSttnIdIn(date, stationIds);

        // 3) 이미 저장된 (sttnId, usersTypeNm) 쌍을 Set 으로 준비
        Set<Pair<String, String>> existingKeys = existing.stream()
            .map(u -> Pair.of(u.getSttnId(), u.getUsersTypeNm()))
            .collect(Collectors.toSet());

        // 4) 남은 것만 필터 후 bulk save
        List<RawBusUsage> toSave = usages.stream()
            .filter(u -> !existingKeys.contains(Pair.of(u.getSttnId(), u.getUsersTypeNm())))
            .toList();

        if (!toSave.isEmpty()) {
            repository.saveAll(toSave);
        }
    }

    @Override
    public void saveAllForce(List<RawBusUsage> usages) {
        if (usages.isEmpty()) {
            return;
        }

        try {
            repository.saveAll(usages);
            repository.flush();  // Hibernate batch insert
        } catch (Exception e) {
            log.warn("[Batch] 중복 insert 무시 - 일부 데이터는 이미 존재합니다.", e);
        }
    }
}

