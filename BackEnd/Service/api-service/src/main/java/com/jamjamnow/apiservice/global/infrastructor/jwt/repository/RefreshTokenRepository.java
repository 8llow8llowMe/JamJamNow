package com.jamjamnow.apiservice.global.infrastructor.jwt.repository;

import com.jamjamnow.securitymodule.auth.jwt.JwtAuthProperties;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private static final String KEY_PREFIX = "refreshToken::";
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtAuthProperties jwtAuthProperties;

    public void save(long memberId, String token) {
        String key = KEY_PREFIX + memberId;
        redisTemplate.opsForValue().set(key, token, jwtAuthProperties.refreshExpiration());
    }

    public Optional<String> find(long memberId) {
        String token = redisTemplate.opsForValue().get(KEY_PREFIX + memberId);
        return Optional.ofNullable(token);
    }

    public void delete(long memberId) {
        redisTemplate.delete(KEY_PREFIX + memberId);
    }
}
