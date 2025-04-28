//package com.jamjamnow.backend.domain.rawdata.buscongestion.entity;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Index;
//import jakarta.persistence.Table;
//import lombok.AccessLevel;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.Comment;
//
//@Entity
//@Getter
//@Builder
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor(access = AccessLevel.PROTECTED)
//@Table(
//    indexes = {
//        @Index(name = "idx_oprYmd_congestion", columnList = "oprYmd"),
//        @Index(name = "idx_ctpvCd_congestion", columnList = "ctpvCd"),
//        @Index(name = "idx_sggCd_congestion", columnList = "sggCd")
//    }
//)
//public class RawBusCongestion {
//
//    @Id
//    @Comment("국토교통부 노선별 혼잡도 ID")
//    @Column(columnDefinition = "BIGINT UNSIGNED")
//    private Long id;
//}
