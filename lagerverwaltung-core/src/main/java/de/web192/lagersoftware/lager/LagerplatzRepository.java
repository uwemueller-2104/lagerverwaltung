package de.web192.lagersoftware.lager;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LagerplatzRepository extends JpaRepository<Lagerplatz, Long> {

    List<Lagerplatz> findByLagerId(Long lagerId);
}
