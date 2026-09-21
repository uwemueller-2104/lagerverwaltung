package de.web192.lagersoftware.web.controller;

import de.web192.lagersoftware.lager.Lagerplatz;
import de.web192.lagersoftware.lager.LagerplatzService;
import de.web192.lagersoftware.lager.LagerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/lagerplatz")
public class LagerplatzController {

    private final LagerplatzService lagerplatzService;
    private final LagerRepository lagerRepository;

    public LagerplatzController(LagerplatzService lagerplatzService, LagerRepository lagerRepository) {
        this.lagerplatzService = lagerplatzService;
        this.lagerRepository = lagerRepository;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("lagerplatzListe", lagerplatzService.alle());
        return "lagerplatz/liste";
    }

    @GetMapping("/neu")
    public String neuesFormular(Model model) {
        model.addAttribute("lagerplatz", new Lagerplatz());
        model.addAttribute("lagerOptionen", lagerRepository.findAll());
        return "lagerplatz/formular";
    }

    @GetMapping("/{id}/bearbeiten")
    public String bearbeiten(@PathVariable Long id, Model model) {
        Lagerplatz lagerplatz = lagerplatzService.findeById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lagerplatz nicht gefunden"));
        model.addAttribute("lagerplatz", lagerplatz);
        model.addAttribute("lagerOptionen", lagerRepository.findAll());
        return "lagerplatz/formular";
    }

    @PostMapping("/speichern")
    public String speichern(@ModelAttribute Lagerplatz lagerplatz, @RequestParam Long lagerId) {
        lagerRepository.findById(lagerId).ifPresent(lagerplatz::setLager);
        lagerplatzService.speichern(lagerplatz);
        return "redirect:/lagerplatz";
    }

    @PostMapping("/{id}/loeschen")
    public String loeschen(@PathVariable Long id) {
        lagerplatzService.loeschen(id);
        return "redirect:/lagerplatz";
    }
}
