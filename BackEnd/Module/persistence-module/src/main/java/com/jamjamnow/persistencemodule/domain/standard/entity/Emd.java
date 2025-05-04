package com.jamjamnow.persistencemodule.domain.standard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("읍면동 코드")
    @Column(length = 10, nullable = false)
    private String emdCd;

    @Comment("읍면동명")
    @Column(length = 50, nullable = false)
    private String emdNm;

    @Comment("시군구 ID (외래키)")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sgg_id", nullable = false)
    private Sgg sgg;
}

