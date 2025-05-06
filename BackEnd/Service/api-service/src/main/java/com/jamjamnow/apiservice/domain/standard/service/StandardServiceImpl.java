package com.jamjamnow.apiservice.domain.standard.service;

import com.jamjamnow.apiservice.domain.standard.dto.EmdResponse;
import com.jamjamnow.apiservice.domain.standard.dto.SggResponse;
import com.jamjamnow.apiservice.domain.standard.dto.SidoResponse;
import com.jamjamnow.persistencemodule.domain.standard.entity.Emd;
import com.jamjamnow.persistencemodule.domain.standard.entity.Sgg;
import com.jamjamnow.persistencemodule.domain.standard.entity.Sido;
import com.jamjamnow.persistencemodule.domain.standard.repository.EmdRepository;
import com.jamjamnow.persistencemodule.domain.standard.repository.SggRepository;
import com.jamjamnow.persistencemodule.domain.standard.repository.SidoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StandardServiceImpl implements StandardService {

    private final SidoRepository sidoRepository;
    private final SggRepository sggRepository;
    private final EmdRepository emdRepository;

    @Override
    public List<SidoResponse> getSidoList() {
        List<Sido> sidos = sidoRepository.findAll();
        return sidos.stream()
            .map(sido -> SidoResponse.builder()
                .ctpvCd(sido.getCtpvCd())
                .ctpvNm(sido.getCtpvNm())
                .build()
            )
            .toList();
    }

    @Override
    public List<SggResponse> getSggList(String ctpvCd) {
        List<Sgg> sggs = sggRepository.findBySido_CtpvCd(ctpvCd);
        return sggs.stream()
            .map(sgg -> SggResponse.builder()
                .sggCd(sgg.getSggCd())
                .sggNm(sgg.getSggNm())
                .build()
            )
            .toList();
    }

    @Override
    public List<EmdResponse> getEmdList(String sggCd) {
        List<Emd> emds = emdRepository.findBySgg_SggCd(sggCd);
        return emds.stream()
            .map(emd -> EmdResponse.builder()
                .emdCd(emd.getEmdCd())
                .emdNm(emd.getEmdNm())
                .build()
            )
            .toList();
    }
}
