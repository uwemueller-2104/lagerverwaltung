package de.web192.lagersoftware.web.controller;

import de.web192.lagersoftware.benutzer.Benutzer;
import de.web192.lagersoftware.benutzer.BenutzerRepository;
import de.web192.lagersoftware.lager.LagerplatzRepository;
import de.web192.lagersoftware.lager.Material;
import de.web192.lagersoftware.lager.MaterialRepository;
import de.web192.lagersoftware.projekt.Lieferschein;
import de.web192.lagersoftware.projekt.LieferscheinService;
import de.web192.lagersoftware.projekt.Projekt;
import de.web192.lagersoftware.projekt.ProjektService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/lieferschein")
public class LieferscheinController {

    // Feste Anzahl an Positionszeilen im Formular (kein JS im Projekt bisher -
    // reicht fuer den Prototyp; leere Zeilen werden beim Speichern ignoriert).
    private static final int POSITIONS_ZEILEN = 8;

    private final LieferscheinService lieferscheinService;
    private final ProjektService projektService;
    private final MaterialRepository materialRepository;
    private final LagerplatzRepository lagerplatzRepository;
    private final BenutzerRepository benutzerRepository;

    public LieferscheinController(LieferscheinService lieferscheinService, ProjektService projektService,
                                   MaterialRepository materialRepository, LagerplatzRepository lagerplatzRepository,
                                   BenutzerRepository benutzerRepository) {
        this.lieferscheinService = lieferscheinService;
        this.projektService = projektService;
        this.materialRepository = materialRepository;
        this.lagerplatzRepository = lagerplatzRepository;
        this.benutzerRepository = benutzerRepository;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("lieferscheinListe", lieferscheinService.alle());
        return "lieferschein/liste";
    }

    @GetMapping("/neu")
    public String neuesFormular(@RequestParam Long projektId, Model model) {
        Projekt projekt = projektService.findeById(projektId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projekt nicht gefunden"));
        model.addAttribute("projekt", projekt);
        befuelleOptionen(model);
        return "lieferschein/formular";
    }

    // Legt Lieferschein + Positionen an; jede Position loest ueber
    // LieferscheinService eine echte Einbuchung aus. Zeilen mit Menge <= 0
    // gelten als leer und werden ignoriert.
    @PostMapping("/anlegen")
    public String anlegen(@RequestParam Long projektId,
                           @RequestParam String nummer,
                           @RequestParam Long benutzerId,
                           @RequestParam(name = "materialId") List<String> materialIds,
                           @RequestParam(name = "lagerplatzId") List<String> lagerplatzIds,
                           @RequestParam(name = "menge") List<String> mengen,
                           Model model) {
        Projekt projekt = projektService.findeById(projektId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projekt nicht gefunden"));
        Benutzer benutzer = benutzerRepository.findById(benutzerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Benutzer nicht gefunden"));

        List<LieferscheinService.NeuePosition> positionen = new ArrayList<>();
        for (int i = 0; i < mengen.size(); i++) {
            double menge = parseMengeOderNull(mengen.get(i));
            if (menge <= 0) {
                continue; // leere Zeile
            }
            Material material = materialRepository.findById(Long.valueOf(materialIds.get(i))).orElseThrow();
            var lagerplatz = lagerplatzRepository.findById(Long.valueOf(lagerplatzIds.get(i))).orElseThrow();
            positionen.add(new LieferscheinService.NeuePosition(material, lagerplatz, menge));
        }

        if (positionen.isEmpty()) {
            model.addAttribute("fehler", "Mindestens eine Position mit Menge > 0 wird benötigt.");
            model.addAttribute("projekt", projekt);
            befuelleOptionen(model);
            return "lieferschein/formular";
        }

        lieferscheinService.anlegen(projekt, nummer, benutzer, positionen);
        return "redirect:/projekt/" + projektId;
    }

    @GetMapping("/{id}")
    public String ansehen(@PathVariable Long id, Model model) {
        Lieferschein lieferschein = lieferscheinService.findeById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lieferschein nicht gefunden"));
        model.addAttribute("lieferschein", lieferschein);
        model.addAttribute("positionen", lieferscheinService.positionen(id));
        return "lieferschein/detail";
    }

    @PostMapping("/{id}/loeschen")
    public String loeschen(@PathVariable Long id) {
        Lieferschein lieferschein = lieferscheinService.findeById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lieferschein nicht gefunden"));
        Long projektId = lieferschein.getProjekt().getId();
        lieferscheinService.loeschen(id);
        return "redirect:/projekt/" + projektId;
    }

    private void befuelleOptionen(Model model) {
        model.addAttribute("materialOptionen", materialRepository.findAll());
        model.addAttribute("lagerplatzOptionen", lagerplatzRepository.findAll());
        model.addAttribute("benutzerOptionen", benutzerRepository.findAll());
        model.addAttribute("positionsZeilen", POSITIONS_ZEILEN);
    }

    private double parseMengeOderNull(String wert) {
        if (wert == null || wert.isBlank()) {
            return 0;
        }
        try {
            return Double.parseDouble(wert.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
