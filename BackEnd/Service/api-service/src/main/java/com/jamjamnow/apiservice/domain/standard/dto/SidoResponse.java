package com.jamjamnow.apiservice.domain.standard.dto;

import lombok.Builder;

@Builder
public record SidoResponse(
    long sidoId,
    String ctpvCd,
    String ctpvNm
) {

}
