package com.jamjamnow.backend.global.infrastructor.openapi;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

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

        OpenApiRegistryProperties.OpenApiSpec spec =
            Optional.ofNullable(registry.apis().get(apiName))
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 OpenAPI: " + apiName));

        // 파라미터 머지
        Map<String, String> merged = new HashMap<>(spec.defaultParams());
        if (additionalParams != null) {
            merged.putAll(additionalParams);
        }
        merged.put("serviceKey", spec.serviceKey());

        return webClient.get()
            // 문자열 대신 uriBuilder 사용
            .uri(builder -> {
                builder = builder.path("");          // base URL만 있을 때는 빈 path
                merged.forEach(builder::queryParam); // 자동 인코딩
                return builder.build();
            })
            .retrieve()
            .bodyToMono(responseType)
            .block();   // 배치 스레드므로 block() 허용
    }
}
