package de.web192.lagersoftware.lager;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Ein Lagerplatz liegt innerhalb eines Lagers, z.B. "Regal A3".
 */
@Entity
@Table(name = "lagerplatz")
@Getter
@Setter
@NoArgsConstructor
public class Lagerplatz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lager_id", nullable = false)
    private Lager lager;

    @Column(nullable = false)
    private String bezeichnung;
}
