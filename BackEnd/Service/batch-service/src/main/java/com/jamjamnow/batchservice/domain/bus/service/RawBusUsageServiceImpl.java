package com.jamjamnow.batchservice.domain.bus.service;

import com.jamjamnow.batchservice.domain.bus.entity.RawBusUsage;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RawBusUsageServiceImpl implements RawBusUsageService {

    private final JdbcTemplate jdbcTemplate;

    // 유효성 검사 -> 2. 중복은 DB 유니크 제약 조건(INSERT IGNORE)으로 처리
    @Override
    public void saveAllIgnoreDuplicates(List<RawBusUsage> usages) {
        if (usages == null || usages.isEmpty()) {
            return;
        }

        // 1. 유효하지 않은 데이터 필터링
        List<RawBusUsage> validList = usages.stream()
            .filter(this::isValid)
            .toList();

        int skipped = usages.size() - validList.size();
        if (skipped > 0) {
            log.warn("[Batch] 유효하지 않은 데이터 {}건은 저장 대상에서 제외됨", skipped);
        }

        if (validList.isEmpty()) {
            return;
        }

        // 2. JdbcTemplate batch insert (INSERT IGNORE 사용)
        String sql = """
                INSERT IGNORE INTO raw_bus_usage (
                    id, opr_ymd, dow_nm, ctpv_cd, ctpv_nm, sgg_cd, sgg_nm,
                    emd_cd, emd_nm, rte_id, sttn_id, users_type_nm, utztn_nope
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws java.sql.SQLException {
                RawBusUsage r = validList.get(i);
                ps.setLong(1, r.getId());
                ps.setDate(2, Date.valueOf(r.getOprYmd()));
                ps.setString(3, r.getDowNm());
                ps.setString(4, r.getCtpvCd());
                ps.setString(5, r.getCtpvNm());
                ps.setString(6, r.getSggCd());
                ps.setString(7, r.getSggNm());
                ps.setString(8, r.getEmdCd());
                ps.setString(9, r.getEmdNm());
                ps.setString(10, r.getRteId());
                ps.setString(11, r.getSttnId());
                ps.setString(12, r.getUsersTypeNm());
                ps.setInt(13, r.getUtztnNope());
            }

            @Override
            public int getBatchSize() {
                return validList.size();
            }
        });

        log.info("[Batch] 최종 저장 요청: {}건 (유효 데이터 중복 제외는 DB가 처리)", validList.size());
    }

    private boolean isValid(RawBusUsage rawBusUsage) {
        return rawBusUsage.getOprYmd() != null
            && isNotBlank(rawBusUsage.getDowNm())
            && isNotBlank(rawBusUsage.getCtpvCd())
            && isNotBlank(rawBusUsage.getCtpvNm())
            && isNotBlank(rawBusUsage.getSggCd())
            && isNotBlank(rawBusUsage.getSggNm())
            && isNotBlank(rawBusUsage.getEmdCd())
            && isNotBlank(rawBusUsage.getEmdNm())
            && isNotBlank(rawBusUsage.getRteId())
            && isNotBlank(rawBusUsage.getSttnId())
            && isNotBlank(rawBusUsage.getUsersTypeNm())
            && rawBusUsage.getUtztnNope() != null;
    }

    private boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty() && !s.trim().equals("~");
    }
}
