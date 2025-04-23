package com.jamjamnow.backend.global.infrastructor.openapi.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jamjamnow.backend.global.infrastructor.openapi.NumericStringToInt;
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
            List<Item> item
        ) {

        }

        public record Item(
            String opr_ymd,
            String dow_nm,
            String ctpv_cd,
            String ctpv_nm,
            String sgg_cd,
            String sgg_nm,
            String emd_cd,
            String emd_nm,
            String rte_id,
            String sttn_id,
            String users_type_nm,
            int utztn_nope
        ) {

        }
    }
}
