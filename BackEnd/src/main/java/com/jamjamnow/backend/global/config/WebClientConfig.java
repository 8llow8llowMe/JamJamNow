package com.jamjamnow.backend.global.config;

import io.netty.channel.ChannelOption;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        // Netty 기반 HttpClient 설정
        HttpClient http = HttpClient.create()
            // 응답 대기 제한 시간 (응답이 10초 내에 오지 않으면 TimeoutException 발생)
            .responseTimeout(Duration.ofSeconds(10))
            // 서버와 TCP 커넥션을 맺을 때 최대 5초까지 대기 (커넥션 지연 방지)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000);

        // WebClient가 처리할 수 있는 최대 응답 크기 설정 (기본값: 256KB -> 여기선 4MB로 확대)
        ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(config -> config.defaultCodecs().maxInMemorySize(4 * 1024 * 1024))
            .build();

        return WebClient.builder()
            // 위에서 설정한 Netty HttpClient를 WebClient에 연결
            .clientConnector(new ReactorClientHttpConnector(http))
            // 응답 크기 제한 전략 적용
            .exchangeStrategies(strategies)
            // 요청 로깅 필터 추가
            .filter(logRequest())
            // 응답 로깅 필터 추가
            .filter(logResponse())
            .build();
    }

    // 요청 로그 필터
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("➡️ Request: [{}] {}", clientRequest.method(), clientRequest.url());

            // 모든 헤더 출력 (디버깅에 유용)
            clientRequest.headers().forEach((name, values) ->
                values.forEach(value -> log.debug("   ↪ {}: {}", name, value)));

            return Mono.just(clientRequest);
        });
    }

    // 응답 로그 필터
    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("⬅️ Response [{}]", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }
}
