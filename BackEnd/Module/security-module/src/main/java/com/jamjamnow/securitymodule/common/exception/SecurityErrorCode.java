package com.jamjamnow.securitymodule.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SecurityErrorCode {

    UNAUTHORIZED("SEC001", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("SEC002", "토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("SEC003", "토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_SIGNATURE_INVALID("SEC004", "서명 검증에 실패하였습니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_MALFORMED("SEC005", "토큰 형식이 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_MISSING("SEC006", "토큰이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("SEC007", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
