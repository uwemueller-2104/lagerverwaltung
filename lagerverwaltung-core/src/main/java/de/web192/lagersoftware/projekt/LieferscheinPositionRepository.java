package de.web192.lagersoftware.projekt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LieferscheinPositionRepository extends JpaRepository<LieferscheinPosition, Long> {

    List<LieferscheinPosition> findByLieferscheinId(Long lieferscheinId);
}
