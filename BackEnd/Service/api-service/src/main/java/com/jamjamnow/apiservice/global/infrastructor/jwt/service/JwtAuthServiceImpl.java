package com.jamjamnow.apiservice.global.infrastructor.jwt.service;

import com.jamjamnow.apiservice.domain.member.dto.MemberLoginResponse;
import com.jamjamnow.apiservice.domain.member.dto.MemberMyInfoResponse;
import com.jamjamnow.apiservice.domain.member.entity.Member;
import com.jamjamnow.apiservice.domain.member.exception.MemberErrorCode;
import com.jamjamnow.apiservice.domain.member.exception.MemberException;
import com.jamjamnow.apiservice.domain.member.repository.MemberRepository;
import com.jamjamnow.apiservice.global.infrastructor.jwt.repository.RefreshTokenRepository;
import com.jamjamnow.securitymodule.auth.dto.JwtAuthTokenInfo;
import com.jamjamnow.securitymodule.auth.jwt.JwtAuthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtAuthServiceImpl implements JwtAuthService {

    private final JwtAuthProvider jwtAuthProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    @Override
    public MemberLoginResponse issueAndSaveJwtToken(Member member) {
        String accessToken = jwtAuthProvider.issueAccessToken(member.getId(),
            member.getRole());
        String refreshToken = jwtAuthProvider.issueRefreshToken();

        JwtAuthTokenInfo tokenInfo = JwtAuthTokenInfo.builder()
            .accessToken(accessToken)
            .build();

        try {
            refreshTokenRepository.save(member.getId(), refreshToken);
        } catch (RedisConnectionFailureException e) {
            log.warn("Redis 연결 실패 - RefreshToken 저장 안됨", e);
        }

        MemberMyInfoResponse memberMyInfo = MemberMyInfoResponse
            .builder()
            .memberId(member.getId())
            .name(member.getName())
            .build();

        return MemberLoginResponse.builder()
            .tokenInfo(tokenInfo)
            .memberMyInfo(memberMyInfo)
            .build();
    }

    @Override
    public String reissueAccessToken(long memberId) {
        refreshTokenRepository.find(memberId)
            .orElseThrow(() -> new RuntimeException("레디스에 저장된 refreshToken이 없습니다."));

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        return jwtAuthProvider.issueAccessToken(member.getId(), member.getRole());
    }
}
