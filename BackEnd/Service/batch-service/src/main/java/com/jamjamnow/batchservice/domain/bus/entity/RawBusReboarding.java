package com.jamjamnow.batchservice.domain.bus.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class RawBusReboarding {

    @Id
    @Comment("국토교통부 노선별 차내 재차인원")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @Comment("운행일자")
    @Column(nullable = false)
    private LocalDate oprYmd;

    @Comment("요일명")
    @Column(length = 10, nullable = false)
    private String dowNm;

    @Comment("시도코드")
    @Column(length = 2, nullable = false)
    private String ctpvCd;

    @Comment("시도명")
    @Column(length = 50, nullable = false)
    private String ctpvNm;

    @Comment("시군구코드")
    @Column(length = 5, nullable = false)
    private String sggCd;

    @Comment("시군구명")
    @Column(length = 50, nullable = false)
    private String sggNm;

    @Comment("노선 ID")
    @Column(length = 20, nullable = false)
    private String rteId;

    @Comment("노선명")
    @Column(length = 100, nullable = false)
    private String rteNm;

    @Comment("출발정류장명")
    @Column(length = 100, nullable = false)
    private String dptreSttnNm;

    @Comment("도착정류장명")
    @Column(length = 100, nullable = false)
    private String arvlSttnNm;

    @Comment("정류장 순서")
    @Column(columnDefinition = "INT UNSIGNED", nullable = false)
    private Integer sttnSeq;

    @Comment("정류장명")
    @Column(length = 100, nullable = false)
    private String sttnNm;

    @Comment("시간대")
    @Column(length = 2, nullable = false)
    private String tzon;

    @Comment("평균 재차인원수")
    @Column(columnDefinition = "INT UNSIGNED", nullable = false)
    private Integer avgScdtmNope;
}
