package com.jamjamnow.backend.global.infrastructor.openapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenApiResponseWrapper(
    OpenApiResponse openApiResponse
) {

}
