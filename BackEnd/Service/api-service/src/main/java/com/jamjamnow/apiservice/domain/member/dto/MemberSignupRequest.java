package com.jamjamnow.apiservice.domain.member.dto;

public record MemberSignupRequest(
    String email,
    String password,
    String name,
    String nickname,
    String profileImage
) {

}
