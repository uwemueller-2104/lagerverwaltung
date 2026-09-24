package de.web192.lagersoftware.web.security;

import de.web192.lagersoftware.benutzer.BenutzerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Bindeglied zwischen Spring Security und unserer eigenen Benutzerverwaltung.
 * Spring Security (DaoAuthenticationProvider) findet diesen Bean automatisch,
 * sobald genau ein UserDetailsService- und ein PasswordEncoder-Bean im
 * Kontext vorhanden sind (siehe SecurityConfig).
 */
@Service
public class BenutzerDetailsService implements UserDetailsService {

    private final BenutzerRepository benutzerRepository;

    public BenutzerDetailsService(BenutzerRepository benutzerRepository) {
        this.benutzerRepository = benutzerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String benutzername) throws UsernameNotFoundException {
        return benutzerRepository.findByBenutzername(benutzername)
                .map(BenutzerPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("Unbekannter Benutzername: " + benutzername));
    }
}
