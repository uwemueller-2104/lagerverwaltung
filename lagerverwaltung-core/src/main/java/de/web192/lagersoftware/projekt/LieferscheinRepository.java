package de.web192.lagersoftware.projekt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LieferscheinRepository extends JpaRepository<Lieferschein, Long> {

    List<Lieferschein> findByProjektId(Long projektId);
}
