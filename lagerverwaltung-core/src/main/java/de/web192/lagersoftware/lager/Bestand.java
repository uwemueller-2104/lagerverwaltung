package de.web192.lagersoftware.lager;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Wie viel von einem Material an einem bestimmten Lagerplatz liegt.
 * Ein Material kann an mehreren Lagerplaetzen liegen, deshalb eine eigene Tabelle
 * statt eines einfachen "bestand"-Feldes direkt an Material.
 */
@Entity
@Table(name = "bestand", uniqueConstraints = @UniqueConstraint(columnNames = {"material_id", "lagerplatz_id"}))
@Getter
@Setter
@NoArgsConstructor
public class Bestand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lagerplatz_id", nullable = false)
    private Lagerplatz lagerplatz;

    @Column(nullable = false)
    private double menge;
}
