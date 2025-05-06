package com.jamjamnow.batchservice.global.infrastructor.openapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jamjamnow.batchservice.global.infrastructor.openapi.NumericStringToInt;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenApiGenericResponse<T>(
    Header header,
    Body<T> body
) {

    public record Header(
        String resultCode,
        String resultMsg
    ) {

    }

    public record Body<T>(
        Items<T> items,
        @JsonDeserialize(using = NumericStringToInt.class)
        int totalCount,
        int pageNo,
        int numOfRows,
        String dataType
    ) {

        public record Items<T>(
            List<T> item
        ) {

        }
    }
}
