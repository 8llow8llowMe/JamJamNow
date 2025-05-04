package com.jamjamnow.batchservice.domain.bus.service;

import com.jamjamnow.batchservice.domain.bus.entity.RawBusUsage;
import java.util.List;

public interface RawBusUsageService {

    void saveAllIgnoreDuplicates(List<RawBusUsage> usages);
}
