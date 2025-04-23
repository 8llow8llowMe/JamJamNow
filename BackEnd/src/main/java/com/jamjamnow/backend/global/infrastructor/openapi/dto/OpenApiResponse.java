package com.jamjamnow.backend.global.infrastructor.openapi.dto;

import java.util.List;

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
        String totalCount,
        String pageNo,
        String numOfRows,
        String dataType
    ) {

        public record Items(
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
