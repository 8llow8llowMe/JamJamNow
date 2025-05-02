package com.jamjamnow.batchservice.global.infrastructor.openapi;

import java.util.Map;
import jdk.jfr.Name;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "openapi")
public record OpenApiRegistryProperties(
    @Name("apis") Map<String, OpenApiSpec> apis
) {

    public record OpenApiSpec(
        String url,
        String serviceKey,
        @DefaultValue("{}") Map<String, String> defaultParams
    ) {

    }
}
