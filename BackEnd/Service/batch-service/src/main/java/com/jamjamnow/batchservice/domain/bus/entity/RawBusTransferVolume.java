package com.jamjamnow.batchservice.domain.bus.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
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
@Table(
    indexes = {
        @Index(name = "idx_opr_ctpv_sgg", columnList = "oprYmd, ctpvCd, sggCd")
    }
)
public class RawBusTransferVolume {

    @Id
    @Comment("국토교통부 환승통행량 ID")
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

    @Comment("이용자 유형명")
    @Column(length = 20, nullable = false)
    private String usersTypeNm;

    @Comment("시간대")
    @Column(length = 2, nullable = false)
    private String tzon;

    @Comment("발생 통행 건수")
    @Column(columnDefinition = "INT UNSIGNED", nullable = false)
    private Integer ocrnPasgCnt;

    @Comment("도착 통행 건수")
    @Column(columnDefinition = "INT UNSIGNED", nullable = false)
    private Integer arvlPasgCnt;
}
