package com.jamjamnow.securitymodule.auth.dto;

import lombok.Builder;

@Builder
public record JwtAuthTokenInfo(
    String accessToken
) {

}
