package com.jamjamnow.apiservice.domain.standard.dto;

import lombok.Builder;

@Builder
public record SidoResponse(
    String ctpvCd,
    String ctpvNm
) {

}
