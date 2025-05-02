package com.jamjamnow.apiservice.domain.member.service;

import com.jamjamnow.apiservice.domain.member.dto.MemberLoginRequest;
import com.jamjamnow.apiservice.domain.member.dto.MemberLoginResponse;
import com.jamjamnow.apiservice.domain.member.dto.MemberMyInfoResponse;
import com.jamjamnow.apiservice.domain.member.dto.MemberSignupRequest;
import com.jamjamnow.apiservice.domain.member.entity.Member;
import com.jamjamnow.apiservice.domain.member.exception.MemberErrorCode;
import com.jamjamnow.apiservice.domain.member.exception.MemberException;
import com.jamjamnow.apiservice.domain.member.repository.MemberRepository;
import com.jamjamnow.apiservice.global.infrastructor.jwt.repository.RefreshTokenRepository;
import com.jamjamnow.apiservice.global.infrastructor.jwt.service.JwtAuthService;
import com.jamjamnow.persistencemodule.global.util.SnowflakeIdGenerator;
import com.jamjamnow.securitymodule.common.enums.SecurityRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthService jwtAuthService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void signupMember(MemberSignupRequest signupRequest) {
        boolean isExist = memberRepository.existsByEmail(signupRequest.email());

        if (isExist) {
            throw new MemberException(MemberErrorCode.EXIST_MEMBER_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.password());

        Member member = Member.builder()
            .id(snowflakeIdGenerator.generateId())
            .email(signupRequest.email())
            .password(encodedPassword)
            .name(signupRequest.name())
            .nickname(signupRequest.nickname())
            .role(SecurityRole.USER)
            .profileImage(signupRequest.profileImage())
            .build();

        memberRepository.save(member);
    }

    @Override
    public MemberLoginResponse loginMember(MemberLoginRequest loginRequest) {
        Member member = memberRepository.findByEmail(loginRequest.email())
            .orElseThrow(() -> new MemberException(MemberErrorCode.EXIST_MEMBER_EMAIL));

        String realPassword = member.getPassword();

        if (!passwordEncoder.matches(loginRequest.password(), realPassword)) {
            throw new MemberException(MemberErrorCode.NOT_MATCH_PASSWORD);
        }

        return jwtAuthService.issueAndSaveJwtToken(member);
    }

    @Override
    public void logoutMember(long memberId) {
        refreshTokenRepository.find(memberId)
            .ifPresent(token -> refreshTokenRepository.delete(memberId));
    }

    @Override
    public MemberMyInfoResponse getMemberMyInfo(long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND_MEMBER));

        return MemberMyInfoResponse.builder()
            .memberId(memberId)
            .name(member.getName())
            .nickname(member.getNickname())
            .build();
    }
}
