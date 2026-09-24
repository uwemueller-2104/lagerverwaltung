package de.web192.lagersoftware.web.security;

import de.web192.lagersoftware.benutzer.Benutzer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Adapter zwischen unserem eigenen {@link Benutzer} (aus dem Modul
 * benutzerverwaltung, das bewusst nichts von Spring Security wissen soll)
 * und der Spring-Security-Sicht {@link UserDetails}.
 *
 * Die Rolle wird 1:1 auf eine Spring-Security-Authority "ROLE_<Rolle>"
 * gemappt, z.B. ADMIN -> ROLE_ADMIN.
 */
public class BenutzerPrincipal implements UserDetails {

    private final Benutzer benutzer;

    public BenutzerPrincipal(Benutzer benutzer) {
        this.benutzer = benutzer;
    }

    public Benutzer getBenutzer() {
        return benutzer;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + benutzer.getRolle().name()));
    }

    @Override
    public String getPassword() {
        return benutzer.getPasswortHash();
    }

    @Override
    public String getUsername() {
        return benutzer.getBenutzername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return benutzer.isAktiv();
    }
}
