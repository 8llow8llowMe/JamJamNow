package com.jamjamnow.apiservice.global.exception;

import com.jamjamnow.apiservice.domain.member.exception.MemberException;
import com.jamjamnow.commonmodule.dto.Response;
import com.jamjamnow.securitymodule.common.exception.SecurityJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SecurityJwtException.class)
    public ResponseEntity<Response<Void>> jwtTokenException(SecurityJwtException e) {
        log.error("토큰 관련 오류: {}", e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
            .body(Response.fail(e.getErrorCode().getCode(), e.getErrorCode().getMessage()));
    }

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<Response<Void>> memberException(MemberException e) {
        log.error("회원 관련 오류: {}", e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
            .body(Response.fail(e.getErrorCode().getCode(), e.getErrorCode().getErrorMessage()));
    }

}
