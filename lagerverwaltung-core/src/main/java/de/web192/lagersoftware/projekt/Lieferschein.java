package de.web192.lagersoftware.projekt;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Ware, die fuer ein Projekt bestellt wurde und im Lager ankommt (nicht
 * direkt auf der Baustelle). Beim Anlegen loest jede Position eine echte
 * Einbuchung ueber BestandService aus, siehe LieferscheinService.
 */
@Entity
@Table(name = "lieferschein")
@Getter
@Setter
@NoArgsConstructor
public class Lieferschein {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "projekt_id", nullable = false)
    private Projekt projekt;

    private String nummer;

    @Column(nullable = false)
    private LocalDate datum = LocalDate.now();
}
