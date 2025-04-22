package com.jamjamnow.backend.domain.member.dto;

public record MemberLoginRequest(
    String email,
    String password
) {

}
