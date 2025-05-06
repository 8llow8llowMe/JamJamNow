package com.jamjamnow.batchservice.global.infrastructor.openapi.dto.bus;

public record BusTransferVolumeItem(
    String opr_ymd,
    String dow_nm,
    String ctpv_cd,
    String ctpv_nm,
    String sgg_cd,
    String sgg_nm,
    String users_type_nm,
    String tzon,
    int ocrn_pasg_cnt,
    int arvl_pasg_cnt
) {

}
