package com.jamjamnow.apiservice.domain.standard.service;

import com.jamjamnow.apiservice.domain.standard.dto.EmdResponse;
import com.jamjamnow.apiservice.domain.standard.dto.SggResponse;
import com.jamjamnow.apiservice.domain.standard.dto.SidoResponse;
import java.util.List;

public interface StandardService {

    List<SidoResponse> getSidoList();

    List<SggResponse> getSggList(String ctpvCd);

    List<EmdResponse> getEmdList(String sggCd);
}
