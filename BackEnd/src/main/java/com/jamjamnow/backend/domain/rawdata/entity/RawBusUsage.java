package com.jamjamnow.backend.domain.rawdata.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
    uniqueConstraints = @UniqueConstraint(
        name = "uk_oprYmd_sttnId_usersTypeNm",
        columnNames = {"oprYmd", "sttnId", "usersTypeNm"}
    ),
    indexes = {
        @Index(name = "idx_oprYmd", columnList = "oprYmd"),
        @Index(name = "idx_ctpvCd", columnList = "ctpvCd"),
        @Index(name = "idx_sggCd", columnList = "sggCd")
    }
)
public class RawBusUsage {

    @Id
    @Comment("국토교통부_대중교통 이용인원수 ID")
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

    @Comment("정류장 ID")
    private String sttnId;

    @Comment("이용자 유형명 (예: 일반인)")
    @Column(length = 20)
    private String usersTypeNm;

    @Comment("이용 인원 수")
    @Column(columnDefinition = "INT UNSIGNED")
    private int utztnNope;
}
