package de.web192.lagersoftware.lager;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BestandRepository extends JpaRepository<Bestand, Long> {

    List<Bestand> findByMaterialId(Long materialId);

    Optional<Bestand> findByMaterialIdAndLagerplatzId(Long materialId, Long lagerplatzId);
}
