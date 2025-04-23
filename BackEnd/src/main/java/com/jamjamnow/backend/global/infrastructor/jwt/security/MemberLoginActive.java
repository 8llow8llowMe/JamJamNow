package com.jamjamnow.backend.global.infrastructor.jwt.security;

import com.jamjamnow.backend.domain.member.entity.enums.MemberRole;

public record MemberLoginActive(
    long id,
    MemberRole role
) {

}
