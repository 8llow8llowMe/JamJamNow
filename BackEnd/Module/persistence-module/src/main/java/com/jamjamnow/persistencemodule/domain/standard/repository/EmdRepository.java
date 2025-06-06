package com.jamjamnow.persistencemodule.domain.standard.repository;

import com.jamjamnow.persistencemodule.domain.standard.entity.Emd;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmdRepository extends JpaRepository<Emd, Long> {

    List<Emd> findBySgg_SggCd(String sggCd);
}
