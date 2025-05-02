package com.jamjamnow.batchservice.global.infrastructor.openapi;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public <T> T fetch(String apiName, Map<String, String> additionalParams,
        Class<T> responseType) {

        OpenApiRegistryProperties.OpenApiSpec spec =
            Optional.ofNullable(registry.apis().get(apiName))
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 OpenAPI: " + apiName));

        Map<String, String> mergedParams = new HashMap<>(spec.defaultParams());
        if (additionalParams != null) {
            mergedParams.putAll(additionalParams);
        }

        String encodedServiceKey = URLEncoder.encode(spec.serviceKey(), StandardCharsets.UTF_8);
        log.info("Encoded serviceKey: {}", encodedServiceKey);

        Map<String, String> otherParams = new HashMap<>(mergedParams);
        otherParams.remove("serviceKey");

        UriComponentsBuilder builder = UriComponentsBuilder
            .fromUriString(spec.url())
            .queryParam("serviceKey", encodedServiceKey);

        for (Map.Entry<String, String> entry : otherParams.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }

        String fullUrl = builder.build(false).toUriString();
        URI uri = URI.create(fullUrl);
        log.info("API 요청 URL: {}", uri);

        // 6. Content-Type 검사 후 XML 응답 무시 (null 반환)
        return webClient.get()
            .uri(uri)
            .accept(MediaType.APPLICATION_JSON)
            .exchangeToMono((ClientResponse response) -> {
                MediaType contentType = response.headers().contentType()
                    .orElse(MediaType.APPLICATION_OCTET_STREAM);

                if (contentType.includes(MediaType.APPLICATION_XML) || contentType.includes(
                    MediaType.TEXT_XML)) {
                    return response.bodyToMono(String.class)
                        .doOnNext(body -> log.warn("[OpenAPI] XML 응답 감지 - 무시됨. body=\n{}", body))
                        .then(Mono.empty());  // XML 응답은 무시
                }

                return response.bodyToMono(responseType);
            })
            .block();
    }
}
