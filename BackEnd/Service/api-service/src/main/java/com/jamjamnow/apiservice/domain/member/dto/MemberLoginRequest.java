package com.jamjamnow.apiservice.domain.member.dto;

public record MemberLoginRequest(
    String email,
    String password
) {

}
