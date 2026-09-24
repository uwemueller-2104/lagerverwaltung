package de.web192.lagersoftware.projekt;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "projekt")
@Getter
@Setter
@NoArgsConstructor
public class Projekt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String kunde;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.OFFEN;

    public enum Status {
        OFFEN,
        IN_ARBEIT,
        ABGESCHLOSSEN
    }
}
