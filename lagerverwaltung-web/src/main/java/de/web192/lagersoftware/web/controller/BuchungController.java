package de.web192.lagersoftware.web.controller;

import de.web192.lagersoftware.benutzer.Benutzer;
import de.web192.lagersoftware.benutzer.BenutzerRepository;
import de.web192.lagersoftware.lager.*;
import de.web192.lagersoftware.web.security.BenutzerPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/buchung")
public class BuchungController {

    private final BuchungService buchungService;
    private final BestandService bestandService;
    private final MaterialRepository materialRepository;
    private final LagerplatzRepository lagerplatzRepository;
    private final BenutzerRepository benutzerRepository;

    public BuchungController(BuchungService buchungService, BestandService bestandService,
                              MaterialRepository materialRepository, LagerplatzRepository lagerplatzRepository,
                              BenutzerRepository benutzerRepository) {
        this.buchungService = buchungService;
        this.bestandService = bestandService;
        this.materialRepository = materialRepository;
        this.lagerplatzRepository = lagerplatzRepository;
        this.benutzerRepository = benutzerRepository;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("buchungListe", buchungService.alle());
        return "buchung/liste";
    }

    @GetMapping("/neu")
    public String neuesFormular(Model model) {
        befuelleOptionen(model);
        return "buchung/formular";
    }

    // Bewusst getrennt von "/speichern": das hier fuehrt eine ECHTE Buchung
    // aus (ueber BestandService), passt also auch den Bestand an. Der
    // buchende Benutzer kommt aus dem Login, nicht mehr aus einer frei
    // waehlbaren Dropdown-Liste - sonst koennte jeder Buchungen unter
    // fremdem Namen anlegen.
    @PostMapping("/buchen")
    public String buchen(@RequestParam Long materialId, @RequestParam Long lagerplatzId,
                          @RequestParam Buchung.BuchungsTyp typ, @RequestParam double menge,
                          @AuthenticationPrincipal BenutzerPrincipal angemeldeter, Model model) {
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
            return "buchung/formular";
        }
        return "redirect:/buchung";
    }

    // Direkte Korrektur eines bestehenden Historien-Eintrags, siehe Caveat
    // in BuchungService. Passt den Bestand NICHT automatisch an.
    @GetMapping("/{id}/bearbeiten")
    public String bearbeiten(@PathVariable Long id, Model model) {
        Buchung buchung = buchungService.findeById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Buchung nicht gefunden"));
        model.addAttribute("buchung", buchung);
        befuelleOptionen(model);
        return "buchung/bearbeiten";
    }

    @PostMapping("/{id}/speichern")
    public String speichern(@PathVariable Long id, @ModelAttribute Buchung buchung,
                             @RequestParam Long materialId, @RequestParam Long lagerplatzId,
                             @RequestParam Long benutzerId) {
        buchung.setId(id);
        materialRepository.findById(materialId).ifPresent(buchung::setMaterial);
        lagerplatzRepository.findById(lagerplatzId).ifPresent(buchung::setLagerplatz);
        benutzerRepository.findById(benutzerId).ifPresent(buchung::setBenutzer);
        buchungService.speichernDirekt(buchung);
        return "redirect:/buchung";
    }

    @PostMapping("/{id}/loeschen")
    public String loeschen(@PathVariable Long id) {
        buchungService.loeschen(id);
        return "redirect:/buchung";
    }

    private void befuelleOptionen(Model model) {
        model.addAttribute("materialOptionen", materialRepository.findAll());
        model.addAttribute("lagerplatzOptionen", lagerplatzRepository.findAll());
        model.addAttribute("benutzerOptionen", benutzerRepository.findAll());
        model.addAttribute("typOptionen", Buchung.BuchungsTyp.values());
    }
}
