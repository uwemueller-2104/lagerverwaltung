package de.web192.lagersoftware.projekt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AngebotPositionRepository extends JpaRepository<AngebotPosition, Long> {

    List<AngebotPosition> findByAngebotId(Long angebotId);
}
