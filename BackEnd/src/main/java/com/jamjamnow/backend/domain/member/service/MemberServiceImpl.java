package com.jamjamnow.backend.domain.member.service;

import com.jamjamnow.backend.domain.member.dto.MemberSignupRequest;
import com.jamjamnow.backend.domain.member.entity.Member;
import com.jamjamnow.backend.domain.member.repository.MemberRepository;
import com.jamjamnow.backend.global.util.SnowflakeIdGenerator;
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

    @Override
    public void signupMember(MemberSignupRequest signupRequest) {
        if (memberRepository.existsByEmail(signupRequest.email())) {
            throw new RuntimeException("해당 이메일을 가진 회원이 존재합니다.");
        }

        Member member = Member.builder()
            .id(snowflakeIdGenerator.generateId())
            .email(signupRequest.email())
            .password(passwordEncoder.encode(signupRequest.password()))
            .name(signupRequest.name())
            .nickname(signupRequest.nickname())
            .profileImage(signupRequest.profileImage())
            .build();

        memberRepository.save(member);
    }
}
