package de.web192.lagersoftware.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Login (lokal, Benutzername/Passwort) + rollenbasierte Zugriffssteuerung.
 *
 * Rollen (siehe {@link de.web192.lagersoftware.benutzer.Rolle}):
 * - ADMIN: darf Stammdaten bearbeiten (Lager, Lagerplaetze, Material,
 *   Projekte, Angebote, Lieferscheine) und Benutzer verwalten.
 * - MITARBEITER: darf alles ansehen sowie Bestand ein-/ausbuchen
 *   (Verwaltungs-GUI und mobile Ansicht), aber keine Stammdaten anlegen
 *   oder aendern.
 *
 * TODO (Backlog): Microsoft Entra ID als zweite Login-Moeglichkeit neben
 * dem lokalen Login ergaenzen (spring-boot-starter-oauth2-client), siehe
 * Skizze im Chat-Verlauf: lokaler Login bleibt als Fallback bestehen,
 * Rollenzuweisung fuer neue Microsoft-Benutzer erfolgt manuell in der App,
 * Single-Tenant.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/h2-console/**", "/api/health").permitAll()
                        .requestMatchers("/lager/**", "/lagerplatz/**", "/material/**",
                                "/projekt/**", "/angebot/**", "/lieferschein/**", "/benutzer/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", false)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                // H2-Konsole nutzt Frames und ist selbst nicht CSRF-faehig -
                // beides nur fuer diesen Entwickler-Endpunkt lockern.
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }
}
