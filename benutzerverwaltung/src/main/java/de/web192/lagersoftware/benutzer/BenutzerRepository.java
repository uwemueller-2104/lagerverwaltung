package de.web192.lagersoftware.benutzer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BenutzerRepository extends JpaRepository<Benutzer, Long> {

    Optional<Benutzer> findByBenutzername(String benutzername);
}
