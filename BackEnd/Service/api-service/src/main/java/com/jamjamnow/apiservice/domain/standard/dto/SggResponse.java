package com.jamjamnow.apiservice.domain.standard.dto;

import lombok.Builder;

@Builder
public record SggResponse(
    long sggId,
    String sggCd,
    String sggNm
) {

}
