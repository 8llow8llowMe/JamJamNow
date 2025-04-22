package com.jamjamnow.backend.domain.member.dto;

public record MemberInfo(
    Long id,
    String email,
    String name,
    String nickname,
    String profileImage
) {

}
