package com.jamjamnow.apiservice.domain.standard.dto;

import lombok.Builder;

@Builder
public record SggResponse(
    String sggCd,
    String sggNm
) {

}
