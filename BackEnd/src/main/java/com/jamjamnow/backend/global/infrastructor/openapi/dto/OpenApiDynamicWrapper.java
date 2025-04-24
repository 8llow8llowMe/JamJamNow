package com.jamjamnow.backend.global.infrastructor.openapi.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenApiDynamicWrapper {

    private final Map<String, OpenApiResponse> data = new HashMap<>();

    @JsonAnySetter
    public void set(String key, OpenApiResponse value) {
        data.put(key, value);
    }

    public OpenApiResponse getFirstResponse() {
        return data.values().stream().findFirst().orElse(null);
    }

    public Map<String, OpenApiResponse> getAll() {
        return data;
    }
}
