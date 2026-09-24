package de.web192.lagersoftware.web.controller;

import de.web192.lagersoftware.benutzer.Benutzer;
import de.web192.lagersoftware.lager.*;
import de.web192.lagersoftware.web.security.BenutzerPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Schlanke, mobile-optimierte Ansicht: nur Anzeigen und Ein-/Ausbuchen,
 * kein Bearbeiten von Stammdaten (das bleibt der Verwaltungs-GUI vorbehalten).
 * Nutzt bewusst dieselben Services wie die Verwaltungs-GUI, nur mit
 * schlankeren Templates. Barcode-/QR-Scan per Handykamera kommt als
 * naechster Schritt oben drauf (siehe README).
 */
@Controller
@RequestMapping("/mobil")
public class MobileController {

    private final BestandService bestandService;
    private final MaterialRepository materialRepository;
    private final LagerplatzRepository lagerplatzRepository;

    public MobileController(BestandService bestandService, MaterialRepository materialRepository,
                             LagerplatzRepository lagerplatzRepository) {
        this.bestandService = bestandService;
        this.materialRepository = materialRepository;
        this.lagerplatzRepository = lagerplatzRepository;
    }

    @GetMapping
    public String start() {
        return "mobil/start";
    }

    @GetMapping("/material")
    public String material(Model model) {
        model.addAttribute("bestandListe", bestandService.alle());
        return "mobil/material";
    }

    @GetMapping("/buchen")
    public String buchenFormular(Model model) {
        befuelleOptionen(model);
        return "mobil/buchen";
    }

    // Buchender Benutzer kommt aus dem Login (siehe BuchungController fuer
    // dieselbe Begruendung), nicht mehr aus einer frei waehlbaren Liste.
    @PostMapping("/buchen")
    public String buchen(@RequestParam Long materialId, @RequestParam Long lagerplatzId,
                          @RequestParam Buchung.BuchungsTyp typ, @RequestParam double menge,
                          @AuthenticationPrincipal BenutzerPrincipal angemeldeter,
                          Model model, RedirectAttributes redirectAttributes) {
        Material material = materialRepository.findById(materialId).orElseThrow();
        Lagerplatz lagerplatz = lagerplatzRepository.findById(lagerplatzId).orElseThrow();
        Benutzer benutzer = angemeldeter.getBenutzer();

        try {
            if (typ == Buchung.BuchungsTyp.EINBUCHUNG) {
                bestandService.einbuchen(material, lagerplatz, benutzer, menge);
            } else {
                bestandService.ausbuchen(material, lagerplatz, benutzer, menge);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("fehler", e.getMessage());
            befuelleOptionen(model);
            return "mobil/buchen";
        }

        redirectAttributes.addFlashAttribute("erfolg",
                (typ == Buchung.BuchungsTyp.EINBUCHUNG ? "Eingebucht: " : "Ausgebucht: ")
                        + menge + " " + material.getEinheit() + " " + material.getName());
        return "redirect:/mobil/buchen";
    }

    private void befuelleOptionen(Model model) {
        model.addAttribute("materialOptionen", materialRepository.findAll());
        model.addAttribute("lagerplatzOptionen", lagerplatzRepository.findAll());
        model.addAttribute("typOptionen", Buchung.BuchungsTyp.values());
    }
}
