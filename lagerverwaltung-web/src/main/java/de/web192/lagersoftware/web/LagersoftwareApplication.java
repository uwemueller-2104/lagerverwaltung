package de.web192.lagersoftware.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Einstiegspunkt. scanBasePackages/EntityScan/EnableJpaRepositories zeigen
 * bewusst auf das gemeinsame Wurzelpaket "de.web192.lagersoftware", damit
 * die Entities und Repositories aus den anderen Modulen (benutzerverwaltung,
 * lagerverwaltung-core) mit gefunden werden.
 */
@SpringBootApplication(scanBasePackages = "de.web192.lagersoftware")
@EntityScan(basePackages = "de.web192.lagersoftware")
@EnableJpaRepositories(basePackages = "de.web192.lagersoftware")
public class LagersoftwareApplication {

    public static void main(String[] args) {
        SpringApplication.run(LagersoftwareApplication.class, args);
    }
}
