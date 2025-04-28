package com.jamjamnow.backend.domain.rawdata.service;

import com.jamjamnow.backend.domain.rawdata.entity.RawBusUsage;
import java.util.List;

public interface RawBusUsageService {

    void saveAllIfNotExists(List<RawBusUsage> usages);

    void saveAllForce(List<RawBusUsage> usages);
}
