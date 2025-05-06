package com.jamjamnow.apiservice.domain.standard.dto;

import lombok.Builder;

@Builder
public record EmdResponse(
    String emdCd,
    String emdNm
) {

}
