package com.jamjamnow.backend.global.infrastructor.jwt;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtTokenPropsInfo(
    String accessKey,
    Duration accessExpiration,
    String refreshKey,
    Duration refreshExpiration
) {

}
