package com.jamjamnow.backend.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    //    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CORS(Cross-Origin Resource Sharing) 설정을 적용합니다.
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // CSRF 설정 비활성화
            .csrf(AbstractHttpConfigurer::disable)

            // HTTP Basic 인증 방식을 비활성화합니다. (ID/PW 기반 인증 사용하지 않음)
            .httpBasic(AbstractHttpConfigurer::disable)

            // X-Frame-Options 비활성화 (H2 Console 접근 등 필요시 사용)
            .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))

            // 모든 HTTP 요청에 대해 접근을 허용합니다.
            // 인증이 필요한 요청은 JwtTokenSecurityFilter에서 직접 토큰 검증을 수행하며,
            // @PreAuthorize 등 메서드 수준의 인가 처리는 EnableMethodSecurity에 의해 적용됩니다.
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )

            // Spring Security 기본 로그인/로그아웃 기능 비활성화
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable);

        // UsernamePasswordAuthenticationFilter 실행 전에 커스텀 JWT 필터를 삽입
//            .addFilterBefore(jwtSecurityFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

//    @Bean
//    public JwtTokenSecurityFilter jwtSecurityFilter() {
//        return new JwtTokenSecurityFilter(jwtTokenProvider, objectMapper);
//    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = getCorsConfiguration(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean() {
        CorsConfiguration config = getCorsConfiguration(6000L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> filterBean = new FilterRegistrationBean<>(
            new CorsFilter(source));
        filterBean.setOrder(0);
        return filterBean;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private CorsConfiguration getCorsConfiguration(long maxAge) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of(
            "http://localhost:5173",
            "https://www.jamjamnow.com"
        ));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setMaxAge(maxAge);
        return config;
    }


}
