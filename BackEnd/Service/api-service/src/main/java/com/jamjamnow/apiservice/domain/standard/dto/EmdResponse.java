package com.jamjamnow.apiservice.domain.standard.dto;

import lombok.Builder;

@Builder
public record EmdResponse(
    long emdId,
    String emdCd,
    String emdNm
) {

}
