package de.web192.lagersoftware.benutzer;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "benutzer")
@Getter
@Setter
@NoArgsConstructor
public class Benutzer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String benutzername;

    @Column(nullable = false)
    private String anzeigename;

    // BCrypt-Hash (siehe PasswordEncoder in lagerverwaltung-web), niemals
    // Klartext. Nullable, weil ein rein per Microsoft angemeldeter Benutzer
    // (geplant, siehe Backlog) kein lokales Passwort braucht.
    @Column
    private String passwortHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rolle rolle;

    private boolean aktiv = true;
}
