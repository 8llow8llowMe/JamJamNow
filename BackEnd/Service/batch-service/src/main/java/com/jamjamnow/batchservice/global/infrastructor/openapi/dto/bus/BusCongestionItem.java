package com.jamjamnow.batchservice.global.infrastructor.openapi.dto.bus;

public record BusCongestionItem(
    String opr_ymd,
    String dow_nm,
    String ctpv_cd,
    String ctpv_nm,
    String sgg_cd,
    String sgg_nm,
    String emd_cd,
    String emd_nm,
    String rte_id,
    int sttn_seq,
    String sttn_id,
    String tzon,
    int cgst
) {

}
