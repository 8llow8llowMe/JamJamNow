package com.jamjamnow.batchservice.domain.bus.service;

import com.jamjamnow.batchservice.domain.bus.entity.RawBusCongestion;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
public class RawBusCongestionServiceImpl implements RawBusCongestionService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveAll(List<RawBusCongestion> congestions) {
        if (congestions == null || congestions.isEmpty()) {
            return;
        }

        List<RawBusCongestion> validList = congestions.stream()
            .filter(this::isValid)
            .toList();

        int skipped = congestions.size() - validList.size();
        if (skipped > 0) {
            log.warn("[Batch] 유효하지 않은 혼잡도 데이터 {}건은 저장 대상에서 제외됨", skipped);
        }

        if (validList.isEmpty()) {
            return;
        }

        String sql = """
                INSERT INTO raw_bus_congestion (
                    id, opr_ymd, dow_nm, ctpv_cd, ctpv_nm,
                    sgg_cd, sgg_nm, emd_cd, emd_nm,
                    rte_id, sttn_seq, sttn_id, tzon, cgst
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                RawBusCongestion r = validList.get(i);
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
                ps.setInt(11, r.getSttnSeq());
                ps.setString(12, r.getSttnId());
                ps.setString(13, r.getTzon());
                ps.setInt(14, r.getCgst());
            }

            @Override
            public int getBatchSize() {
                return validList.size();
            }
        });

        log.info("[Batch] 혼잡도 최종 저장 요청: {}건", validList.size());
    }

    private boolean isValid(RawBusCongestion c) {
        return c.getOprYmd() != null
            && isNotBlank(c.getDowNm())
            && isNotBlank(c.getCtpvCd())
            && isNotBlank(c.getCtpvNm())
            && isNotBlank(c.getSggCd())
            && isNotBlank(c.getSggNm())
            && isNotBlank(c.getEmdCd())
            && isNotBlank(c.getEmdNm())
            && isNotBlank(c.getRteId())
            && c.getSttnSeq() != null
            && isNotBlank(c.getSttnId())
            && isNotBlank(c.getTzon())
            && c.getCgst() != null;
    }

    private boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty() && !s.trim().equals("~");
    }
}
