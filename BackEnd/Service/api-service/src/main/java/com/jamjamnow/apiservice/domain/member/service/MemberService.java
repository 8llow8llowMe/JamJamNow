package com.jamjamnow.apiservice.domain.member.service;

import com.jamjamnow.apiservice.domain.member.dto.MemberLoginRequest;
import com.jamjamnow.apiservice.domain.member.dto.MemberLoginResponse;
import com.jamjamnow.apiservice.domain.member.dto.MemberMyInfoResponse;
import com.jamjamnow.apiservice.domain.member.dto.MemberSignupRequest;

public interface MemberService {

    void signupMember(MemberSignupRequest signupRequest);

    MemberLoginResponse loginMember(MemberLoginRequest loginRequest);

    void logoutMember(long memberId);

    MemberMyInfoResponse getMemberMyInfo(long memberId);
}
