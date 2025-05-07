package com.jamjamnow.batchservice.domain.bus.service;

import com.jamjamnow.batchservice.domain.bus.entity.RawBusCongestion;
import java.util.List;

public interface RawBusCongestionService {

    void saveAll(List<RawBusCongestion> congestions);
}
