package com.jamjamnow.batchservice.global.infrastructor.openapi.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jamjamnow.batchservice.global.infrastructor.openapi.NumericStringToInt;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true) // 예기치 않은 필드는 무시
public record OpenApiResponse(
    Header header,
    Body body
) {

    public record Header(
        String resultCode,
        String resultMsg
    ) {

    }

    public record Body(
        Items items,
        @JsonDeserialize(using = NumericStringToInt.class)
        int totalCount,
        int pageNo,
        int numOfRows,
        String dataType
    ) {

        public record Items(
            @JsonAlias("item") // 1개면 Object, 여러개면 List 문제 대비
            List<BusUsageItem> item
        ) {

        }
    }
}
