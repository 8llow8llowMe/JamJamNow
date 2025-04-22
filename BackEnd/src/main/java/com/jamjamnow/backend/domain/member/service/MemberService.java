package com.jamjamnow.backend.domain.member.service;

import com.jamjamnow.backend.domain.member.dto.MemberSignupRequest;

public interface MemberService {

    void signupMember(MemberSignupRequest signupRequest);
}
