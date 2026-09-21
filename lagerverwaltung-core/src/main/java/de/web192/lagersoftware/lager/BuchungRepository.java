package de.web192.lagersoftware.lager;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuchungRepository extends JpaRepository<Buchung, Long> {

    List<Buchung> findByMaterialIdOrderByZeitpunktDesc(Long materialId);
}
