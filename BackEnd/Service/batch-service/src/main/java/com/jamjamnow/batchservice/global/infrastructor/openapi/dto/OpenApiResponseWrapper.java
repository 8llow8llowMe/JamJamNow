package com.jamjamnow.batchservice.global.infrastructor.openapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenApiResponseWrapper(
    OpenApiResponse openApiResponse
) {

}
