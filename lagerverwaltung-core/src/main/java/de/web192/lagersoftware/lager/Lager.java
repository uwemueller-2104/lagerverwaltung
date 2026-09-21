package de.web192.lagersoftware.lager;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Ein Lager ist der uebergeordnete Standort, z.B. "Hauptlager" oder "Fahrzeug 1".
 * Ein Lager enthaelt mehrere Lagerplaetze.
 */
@Entity
@Table(name = "lager")
@Getter
@Setter
@NoArgsConstructor
public class Lager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String ort;
}
