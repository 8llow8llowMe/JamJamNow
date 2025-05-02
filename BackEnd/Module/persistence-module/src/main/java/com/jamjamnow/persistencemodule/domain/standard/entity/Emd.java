package com.jamjamnow.persistencemodule.domain.standard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_emd_cd",
            columnNames = {"emdCd"}
        )
    }
)
public class Emd {

    @Id
    @Comment("읍면동 ID")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @Comment("읍면동 코드")
    @Column(length = 10, nullable = false)
    private String emdCd;

    @Comment("시도명")
    @Column(length = 50, nullable = false)
    private String ctpvNm;

    @Comment("시군구명")
    @Column(length = 50, nullable = false)
    private String sggNm;

    @Comment("읍면동명")
    @Column(length = 50, nullable = false)
    private String emdNm;
}

