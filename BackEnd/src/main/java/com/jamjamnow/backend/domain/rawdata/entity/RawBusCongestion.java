package com.jamjamnow.backend.domain.rawdata.entity;

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
public class RawBusCongestion {

    @Id
    @Comment("국토교통부 노선별 혼잡도 ID")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @Comment("운행일자")
    @Column(nullable = false)
    private LocalDate oprYmd;

    @Comment("요일명")
    @Column(length = 10)
    private String dowNm;

    @Comment("시도코드")
    @Column(length = 2)
    private String ctpvCd;

    @Comment("시도명")
    @Column(length = 50)
    private String ctpvNm;

    @Comment("시군구코드")
    @Column(length = 5)
    private String sggCd;

    @Comment("시군구명")
    @Column(length = 50)
    private String sggNm;

    @Comment("읍면동코드")
    @Column(length = 20)
    private String emdCd;

    @Comment("읍면동명")
    @Column(length = 50)
    private String emdNm;

    @Comment("노선 ID")
    @Column(length = 20)
    private String rteId;

    @Comment("정류장 순서")
    @Column(columnDefinition = "INT UNSIGNED")
    private Integer sttnSeq;

    @Comment("정류장 ID")
    @Column(length = 20)
    private String sttnId;

    @Comment("시간대")
    @Column(length = 2)
    private String tzon;

    @Comment("혼잡도")
    @Column(columnDefinition = "INT UNSIGNED")
    private Integer cgst;
}
