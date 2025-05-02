package com.jamjamnow.apiservice.domain.member.dto;

import lombok.Builder;

@Builder
public record MemberMyInfoResponse(
    long memberId,
    String name,
    String nickname
) {

}
