package com.jamjamnow.batchservice.domain.bus.repository;

import com.jamjamnow.batchservice.domain.bus.entity.RawBusUsage;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawBusUsageRepository extends JpaRepository<RawBusUsage, Long> {

    List<RawBusUsage> findAllByOprYmdAndSttnIdIn(LocalDate oprYmd, Collection<String> sttnIds);
}
