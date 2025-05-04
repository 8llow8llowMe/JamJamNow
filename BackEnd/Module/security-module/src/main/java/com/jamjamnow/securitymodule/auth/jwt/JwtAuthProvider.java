package com.jamjamnow.securitymodule.auth.jwt;

import com.jamjamnow.securitymodule.common.dto.MemberLoginActive;
import com.jamjamnow.securitymodule.common.enums.SecurityRole;
import com.jamjamnow.securitymodule.common.exception.SecurityErrorCode;
import com.jamjamnow.securitymodule.common.exception.SecurityJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthProvider {

    private static final String CLAIM_ROLE = "role";
    private final JwtAuthProperties jwtAuthProperties;

    public String issueAccessToken(long memberId, SecurityRole role) {
        Claims claims = Jwts.claims()
            .id(UUID.randomUUID().toString())
            .subject(String.valueOf(memberId))
            .add(CLAIM_ROLE, role)
            .build();

        return issueToken(claims, jwtAuthProperties.accessExpiration(),
            jwtAuthProperties.accessKey());
    }

    public String issueRefreshToken() {
        return issueToken(null, jwtAuthProperties.refreshExpiration(),
            jwtAuthProperties.refreshKey());
    }

    public MemberLoginActive parseAccessToken(String accessToken) {
        Claims payload = parseToken(accessToken, jwtAuthProperties.accessKey());

        return MemberLoginActive.builder()
            .id(Long.valueOf(payload.getSubject()))
            .role(SecurityRole.from(payload.get(CLAIM_ROLE, String.class)))
            .build();
    }

    private String issueToken(Claims claims, Duration expiration, String secretKey) {
        Date now = new Date();

        return Jwts.builder()
            .claims(claims)
            .issuedAt(now)
            .expiration(new Date(now.getTime() + expiration.toMillis()))
            .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
            .compact();
    }

    private Claims parseToken(String token, String secretKey) {
        Claims payload;

        try {
            payload = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            throw new SecurityJwtException(SecurityErrorCode.TOKEN_EXPIRED);
        } catch (MalformedJwtException | SecurityException | IllegalArgumentException e) {
            throw new SecurityJwtException(SecurityErrorCode.TOKEN_INVALID);
        } catch (SignatureException e) {
            throw new SecurityJwtException(SecurityErrorCode.TOKEN_SIGNATURE_INVALID);
        }

        return payload;
    }

}
