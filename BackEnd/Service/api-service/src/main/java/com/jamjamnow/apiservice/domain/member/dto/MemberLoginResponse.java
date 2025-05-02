package com.jamjamnow.apiservice.domain.member.dto;

import com.jamjamnow.securitymodule.auth.dto.JwtAuthTokenInfo;
import lombok.Builder;

@Builder
public record MemberLoginResponse(
    JwtAuthTokenInfo tokenInfo,
    MemberMyInfoResponse memberMyInfo
) {

}
