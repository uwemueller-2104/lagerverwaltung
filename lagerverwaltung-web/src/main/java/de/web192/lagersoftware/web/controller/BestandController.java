package de.web192.lagersoftware.web.controller;

import de.web192.lagersoftware.lager.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/bestand")
public class BestandController {

    private final BestandService bestandService;
    private final MaterialRepository materialRepository;
    private final LagerplatzRepository lagerplatzRepository;

    public BestandController(BestandService bestandService, MaterialRepository materialRepository,
                              LagerplatzRepository lagerplatzRepository) {
        this.bestandService = bestandService;
        this.materialRepository = materialRepository;
        this.lagerplatzRepository = lagerplatzRepository;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("bestandListe", bestandService.alle());
        return "bestand/liste";
    }

    @GetMapping("/neu")
    public String neuesFormular(Model model) {
        model.addAttribute("bestand", new Bestand());
        befuelleOptionen(model);
        return "bestand/formular";
    }

    @GetMapping("/{id}/bearbeiten")
    public String bearbeiten(@PathVariable Long id, Model model) {
        Bestand bestand = bestandService.findeById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bestand nicht gefunden"));
        model.addAttribute("bestand", bestand);
        befuelleOptionen(model);
        return "bestand/formular";
    }

    // Direkte Korrektur (z.B. Inventurkorrektur). Fuer normale Ein-/Ausbuchungen
    // bitte /buchung verwenden, damit die Historie mitgeschrieben wird.
    @PostMapping("/speichern")
    public String speichern(@ModelAttribute Bestand bestand, @RequestParam Long materialId,
                             @RequestParam Long lagerplatzId) {
        materialRepository.findById(materialId).ifPresent(bestand::setMaterial);
        lagerplatzRepository.findById(lagerplatzId).ifPresent(bestand::setLagerplatz);
        bestandService.speichernDirekt(bestand);
        return "redirect:/bestand";
    }

    @PostMapping("/{id}/loeschen")
    public String loeschen(@PathVariable Long id) {
        bestandService.loeschen(id);
        return "redirect:/bestand";
    }

    private void befuelleOptionen(Model model) {
        model.addAttribute("materialOptionen", materialRepository.findAll());
        model.addAttribute("lagerplatzOptionen", lagerplatzRepository.findAll());
    }
}
