package de.web192.lagersoftware.projekt;

import de.web192.lagersoftware.lager.Lagerplatz;
import de.web192.lagersoftware.lager.Material;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lieferschein_position")
@Getter
@Setter
@NoArgsConstructor
public class LieferscheinPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lieferschein_id", nullable = false)
    private Lieferschein lieferschein;

    @ManyToOne(optional = false)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    // Wohin im Lager die Ware bei der Einbuchung gelegt wird.
    @ManyToOne(optional = false)
    @JoinColumn(name = "lagerplatz_id", nullable = false)
    private Lagerplatz lagerplatz;

    @Column(nullable = false)
    private double menge;
}
