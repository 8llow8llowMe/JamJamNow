package com.jamjamnow.batchservice.global.infrastructor.openapi;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicOpenApiClient {

    private final WebClient webClient;
    private final OpenApiRegistryProperties registry;

    public <T> T fetch(String category, String name, Map<String, String> params,
        ParameterizedTypeReference<T> responseType) {

        // 1. 카테고리 + 이름으로 OpenAPI 정의 가져오기
        OpenApiRegistryProperties.OpenApiSpec spec = Optional.ofNullable(
                registry.categories().get(category).apis().get(name))
            .orElseThrow(
                () -> new IllegalArgumentException("등록되지 않은 OpenAPI: " + category + "." + name));

        // 2. 기본 파라미터 + 추가 파라미터 병합
        Map<String, String> mergedParams = new HashMap<>(spec.defaultParams());
        if (params != null) {
            mergedParams.putAll(params);
        }

        // 3. URI 구성
        String encodedServiceKey = URLEncoder.encode(spec.serviceKey(), StandardCharsets.UTF_8);
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromUriString(spec.url())
            .queryParam("serviceKey", encodedServiceKey);

        mergedParams.remove("serviceKey"); // 중복 방지
        mergedParams.forEach(builder::queryParam);

        URI uri = URI.create(builder.build(false).toUriString());
        log.info("OpenAPI 요청 URI [{}]: {}", name, uri);

        // 4. 요청 전송 및 JSON 응답 처리
        return webClient.get()
            .uri(uri)
            .accept(MediaType.APPLICATION_JSON)
            .exchangeToMono((ClientResponse response) -> {
                MediaType contentType = response.headers().contentType()
                    .orElse(MediaType.APPLICATION_OCTET_STREAM);

                if (contentType.includes(MediaType.APPLICATION_XML)) {
                    return response.bodyToMono(String.class)
                        .doOnNext(body -> log.warn("[OpenAPI] XML 응답 무시됨 - body: \n{}", body))
                        .then(Mono.empty());
                }

                return response.bodyToMono(responseType);
            })
            .block();
    }
}
