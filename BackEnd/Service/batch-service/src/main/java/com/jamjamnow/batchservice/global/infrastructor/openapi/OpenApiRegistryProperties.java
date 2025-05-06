package com.jamjamnow.batchservice.global.infrastructor.openapi;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "openapi")
public record OpenApiRegistryProperties(
    Map<String, OpenApiCategory> categories // bus, subway 등
) {

    public record OpenApiCategory(
        Map<String, OpenApiSpec> apis // usage, congestion 등
    ) {

    }

    public record OpenApiSpec(
        String url,
        String serviceKey,
        @DefaultValue("{}") Map<String, String> defaultParams
    ) {

    }
}
