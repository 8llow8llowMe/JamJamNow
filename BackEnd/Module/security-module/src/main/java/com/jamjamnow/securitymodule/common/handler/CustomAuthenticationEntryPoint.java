package com.jamjamnow.securitymodule.common.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamjamnow.commonmodule.dto.Response;
import com.jamjamnow.securitymodule.common.exception.SecurityErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(jakarta.servlet.http.HttpServletRequest request,
        jakarta.servlet.http.HttpServletResponse response, AuthenticationException authException)
        throws IOException, ServletException {
        log.warn("[인증 실패] 인증되지 않은 요청 - {}", authException.getMessage());

        SecurityErrorCode errorCode = SecurityErrorCode.UNAUTHORIZED;

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Response<Void> errorResponse = Response.fail(errorCode.getCode(), errorCode.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
