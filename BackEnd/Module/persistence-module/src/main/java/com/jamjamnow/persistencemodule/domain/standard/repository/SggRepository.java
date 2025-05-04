package com.jamjamnow.persistencemodule.domain.standard.repository;

import com.jamjamnow.persistencemodule.domain.standard.entity.Sgg;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SggRepository extends JpaRepository<Sgg, Long> {

    Optional<Sgg> findBySggCdAndSido_CtpvNm(String sggCd, String ctpvNm);

    List<Sgg> findBySido_CtpvCd(String ctpvCd);
}