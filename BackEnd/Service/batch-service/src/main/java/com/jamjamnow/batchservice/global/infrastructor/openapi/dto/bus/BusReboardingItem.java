package com.jamjamnow.batchservice.global.infrastructor.openapi.dto.bus;

public record BusReboardingItem(
    String opr_ymd,
    String dow_nm,
    String ctpv_cd,
    String ctpv_nm,
    String sgg_cd,
    String sgg_nm,
    String rte_id,
    String rte_nm,
    String dptre_sttn_nm,
    String arvl_sttn_nm,
    int sttn_seq,
    String sttn_nm,
    String tzon,
    int avg_scdtm_nope
) {

}
