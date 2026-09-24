package de.web192.lagersoftware.projekt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AngebotRepository extends JpaRepository<Angebot, Long> {

    Optional<Angebot> findByProjektId(Long projektId);
}
