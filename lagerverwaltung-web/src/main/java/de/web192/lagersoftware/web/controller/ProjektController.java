package de.web192.lagersoftware.web.controller;

import de.web192.lagersoftware.projekt.Angebot;
import de.web192.lagersoftware.projekt.AngebotService;
import de.web192.lagersoftware.projekt.Lieferschein;
import de.web192.lagersoftware.projekt.LieferscheinService;
import de.web192.lagersoftware.projekt.Projekt;
import de.web192.lagersoftware.projekt.ProjektService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/projekt")
public class ProjektController {

    private final ProjektService projektService;
    private final AngebotService angebotService;
    private final LieferscheinService lieferscheinService;

    public ProjektController(ProjektService projektService, AngebotService angebotService,
                              LieferscheinService lieferscheinService) {
        this.projektService = projektService;
        this.angebotService = angebotService;
        this.lieferscheinService = lieferscheinService;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("projektListe", projektService.alle());
        return "projekt/liste";
    }

    @GetMapping("/neu")
    public String neuesFormular(Model model) {
        model.addAttribute("projekt", new Projekt());
        model.addAttribute("statusOptionen", Projekt.Status.values());
        return "projekt/formular";
    }

    @GetMapping("/{id}/bearbeiten")
    public String bearbeiten(@PathVariable Long id, Model model) {
        Projekt projekt = projektService.findeById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projekt nicht gefunden"));
        model.addAttribute("projekt", projekt);
        model.addAttribute("statusOptionen", Projekt.Status.values());
        return "projekt/formular";
    }

    @PostMapping("/speichern")
    public String speichern(@ModelAttribute Projekt projekt) {
        projektService.speichern(projekt);
        return "redirect:/projekt";
    }

    @PostMapping("/{id}/loeschen")
    public String loeschen(@PathVariable Long id) {
        projektService.loeschen(id);
        return "redirect:/projekt";
    }

    // Detailseite: fasst Angebot-Status, Soll-vs-Ist-Auswertung und die
    // Lieferscheine des Projekts an einer Stelle zusammen - der zentrale
    // Ausgangspunkt fuer die Arbeit an einem Projekt.
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Projekt projekt = projektService.findeById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projekt nicht gefunden"));

        model.addAttribute("projekt", projekt);

        Angebot angebot = angebotService.findeFuerProjekt(id).orElse(null);
        model.addAttribute("angebotVorhanden", angebot != null);

        model.addAttribute("sollIstListe", projektService.sollVsIst(id).values());

        java.util.List<Lieferschein> lieferscheinListe = lieferscheinService.fuerProjekt(id);
        model.addAttribute("lieferscheinListe", lieferscheinListe);

        return "projekt/detail";
    }
}
