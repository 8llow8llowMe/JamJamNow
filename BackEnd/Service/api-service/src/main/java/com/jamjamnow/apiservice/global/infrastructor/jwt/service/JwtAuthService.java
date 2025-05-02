package com.jamjamnow.apiservice.global.infrastructor.jwt.service;

import com.jamjamnow.apiservice.domain.member.dto.MemberLoginResponse;
import com.jamjamnow.apiservice.domain.member.entity.Member;

public interface JwtAuthService {

    MemberLoginResponse issueAndSaveJwtToken(Member member);

    String reissueAccessToken(long memberId);
}
