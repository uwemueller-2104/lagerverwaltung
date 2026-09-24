package de.web192.lagersoftware.web.controller;

import de.web192.lagersoftware.projekt.Angebot;
import de.web192.lagersoftware.projekt.AngebotService;
import de.web192.lagersoftware.projekt.Projekt;
import de.web192.lagersoftware.projekt.ProjektService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/projekt/{projektId}/angebot")
public class AngebotController {

    private final ProjektService projektService;
    private final AngebotService angebotService;

    public AngebotController(ProjektService projektService, AngebotService angebotService) {
        this.projektService = projektService;
        this.angebotService = angebotService;
    }

    @GetMapping
    public String ansehen(@PathVariable Long projektId, Model model) {
        befuellen(projektId, model);
        return "angebot/import";
    }

    // Ersetzt bei jedem Import die bisherigen Positionen (siehe AngebotService),
    // damit ein aktualisiertes Angebot aus Plancraft einfach erneut eingefuegt
    // werden kann. Zeigt danach direkt an, welche Zeilen einem Material
    // zugeordnet werden konnten und welche nicht.
    @PostMapping("/importieren")
    public String importieren(@PathVariable Long projektId, @RequestParam String text, Model model) {
        Projekt projekt = projektService.findeById(projektId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projekt nicht gefunden"));

        model.addAttribute("importErgebnis", angebotService.importieren(projekt, text));
        befuellen(projektId, model);
        return "angebot/import";
    }

    private void befuellen(Long projektId, Model model) {
        Projekt projekt = projektService.findeById(projektId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projekt nicht gefunden"));
        model.addAttribute("projekt", projekt);

        Angebot angebot = angebotService.findeFuerProjekt(projektId).orElse(null);
        if (angebot != null) {
            model.addAttribute("positionen", angebotService.positionen(angebot.getId()));
        }
    }
}
