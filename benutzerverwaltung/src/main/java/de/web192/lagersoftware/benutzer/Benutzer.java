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

    // TODO (naechster Schritt, gemeinsam): richtiges Passwort-Hashing einbauen
    // (z.B. ueber Spring Security PasswordEncoder), sobald der Login drankommt.
    @Column(nullable = false)
    private String passwortHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rolle rolle;

    private boolean aktiv = true;
}
