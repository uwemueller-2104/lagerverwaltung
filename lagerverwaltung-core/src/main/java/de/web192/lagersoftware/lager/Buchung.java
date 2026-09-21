package de.web192.lagersoftware.lager;

import de.web192.lagersoftware.benutzer.Benutzer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Eine Ein- oder Ausbuchung von Material an einem Lagerplatz, ausgeloest
 * durch einen Benutzer (typischerweise am Handy per Scan).
 */
@Entity
@Table(name = "buchung")
@Getter
@Setter
@NoArgsConstructor
public class Buchung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lagerplatz_id", nullable = false)
    private Lagerplatz lagerplatz;

    @ManyToOne(optional = false)
    @JoinColumn(name = "benutzer_id", nullable = false)
    private Benutzer benutzer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BuchungsTyp typ;

    @Column(nullable = false)
    private double menge;

    @Column(nullable = false)
    private LocalDateTime zeitpunkt = LocalDateTime.now();

    public enum BuchungsTyp {
        EINBUCHUNG,
        AUSBUCHUNG
    }
}
