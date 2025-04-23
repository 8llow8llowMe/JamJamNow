package com.jamjamnow.backend.domain.member.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode {

    EXIST_MEMBER_EMAIL("M001", "이미 가입된 이메일입니다.", HttpStatus.CONFLICT),
    NOT_FOUND_MEMBER("M002", "존재하지 않는 회원입니다", HttpStatus.NOT_FOUND),
    NOT_MATCH_PASSWORD("M003", "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String errorMessage;
    private final HttpStatus httpStatus;
}
