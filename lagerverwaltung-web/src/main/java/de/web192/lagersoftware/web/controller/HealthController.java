package de.web192.lagersoftware.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dient nur dazu, nach dem Projekt-Setup zu pruefen, dass alle Module
 * korrekt verdrahtet sind und die Anwendung startet.
 */
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public String health() {
        return "Lagersoftware v0.1 laeuft";
    }
}
