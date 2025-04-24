package com.jamjamnow.backend.global.infrastructor.openapi;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicOpenApiClient {

    private final WebClient webClient;
    private final OpenApiRegistryProperties registry;

    public <T> T fetch(
        String apiName,
        Map<String, String> additionalParams,
        Class<T> responseType) {

        // 1. API 사양 조회
        OpenApiRegistryProperties.OpenApiSpec spec =
            Optional.ofNullable(registry.apis().get(apiName))
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 OpenAPI: " + apiName));

        // 2. 파라미터 병합
        Map<String, String> mergedParams = new HashMap<>(spec.defaultParams());
        if (additionalParams != null) {
            mergedParams.putAll(additionalParams);
        }

        // 3. serviceKey는 직접 인코딩
        String encodedServiceKey = URLEncoder.encode(spec.serviceKey(), StandardCharsets.UTF_8);
        log.info("Encoded serviceKey: {}", encodedServiceKey);

        // 4. 나머지 파라미터에서 serviceKey는 제외
        Map<String, String> otherParams = new HashMap<>(mergedParams);
        otherParams.remove("serviceKey");

        // 5. URI 조립 (serviceKey는 수동 인코딩된 상태로 삽입)
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromUriString(spec.url())
            .queryParam("serviceKey", encodedServiceKey);

        for (Map.Entry<String, String> entry : otherParams.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }

        String fullUrl = builder.build(false).toUriString(); // false = 인코딩 생략
        URI uri = URI.create(fullUrl);  // 문자열을 URI로 변환
        log.info("API 요청 URL: {}", uri);

        // 6. WebClient 요청
        return webClient.get()
            .uri(uri) // 문자열이 아니라 URI 객체로 전달
            .retrieve()
            .bodyToMono(responseType)
            .block();
    }
}
