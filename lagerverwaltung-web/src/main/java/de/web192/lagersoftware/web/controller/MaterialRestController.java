package de.web192.lagersoftware.web.controller;

import de.web192.lagersoftware.lager.Material;
import de.web192.lagersoftware.lager.MaterialRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Wird vom Kamera-Scan auf der mobilen Seite per fetch() aufgerufen:
 * Code scannen -> hier nachschlagen -> passendes Material im Formular
 * automatisch auswaehlen. Liefert JSON statt einer HTML-Seite.
 */
@RestController
@RequestMapping("/api/material")
public class MaterialRestController {

    private final MaterialRepository materialRepository;

    public MaterialRestController(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @GetMapping("/barcode/{code}")
    public ResponseEntity<Material> perBarcode(@PathVariable String code) {
        return materialRepository.findByBarcode(code)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
