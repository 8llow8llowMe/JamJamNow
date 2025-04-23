package com.jamjamnow.backend.global.infrastructor.jwt;

import com.jamjamnow.backend.domain.member.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String CLAIM_ROLE = "role";
    private final JwtTokenPropsInfo tokenPropsInfo;

    public String issueAccessToken(Member member) {
        Claims claims = Jwts.claims()
            .id(UUID.randomUUID().toString())
            .subject(String.valueOf(member.getId()))
            .add(CLAIM_ROLE, member.getRole())
            .build();

        return issueToken(claims, tokenPropsInfo.accessExpiration(), tokenPropsInfo.accessKey());
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


}
