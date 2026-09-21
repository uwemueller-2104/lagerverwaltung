package de.web192.lagersoftware.web.controller;

import de.web192.lagersoftware.lager.Lager;
import de.web192.lagersoftware.lager.LagerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/lager")
public class LagerController {

    private final LagerRepository lagerRepository;

    public LagerController(LagerRepository lagerRepository) {
        this.lagerRepository = lagerRepository;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("lagerListe", lagerRepository.findAll());
        return "lager/liste";
    }

    @GetMapping("/neu")
    public String neuesFormular(Model model) {
        model.addAttribute("lager", new Lager());
        return "lager/formular";
    }

    @GetMapping("/{id}/bearbeiten")
    public String bearbeiten(@PathVariable Long id, Model model) {
        Lager lager = lagerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lager nicht gefunden"));
        model.addAttribute("lager", lager);
        return "lager/formular";
    }

    @PostMapping("/speichern")
    public String speichern(@ModelAttribute Lager lager) {
        lagerRepository.save(lager);
        return "redirect:/lager";
    }

    @PostMapping("/{id}/loeschen")
    public String loeschen(@PathVariable Long id) {
        lagerRepository.deleteById(id);
        return "redirect:/lager";
    }
}
