package de.web192.lagersoftware.projekt;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Ein Angebot pro Projekt (einfachste Variante fuer v0.1). Enthaelt den
 * Soll-Materialbedarf als AngebotPosition-Liste, befuellt per Copy&Paste-
 * Import aus Plancraft.
 */
@Entity
@Table(name = "angebot")
@Getter
@Setter
@NoArgsConstructor
public class Angebot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "projekt_id", nullable = false, unique = true)
    private Projekt projekt;

    @Column(nullable = false)
    private LocalDate erstelltAm = LocalDate.now();
}
