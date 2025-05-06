package com.jamjamnow.batchservice.global.infrastructor.openapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenApiRoot<T>(
    @JsonProperty("Response")
    OpenApiGenericResponse<T> response
) {

}
