package de.web192.lagersoftware.web.controller;

import de.web192.lagersoftware.lager.Material;
import de.web192.lagersoftware.lager.MaterialService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/material")
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("materialListe", materialService.alle());
        return "material/liste";
    }

    @GetMapping("/neu")
    public String neuesFormular(Model model) {
        model.addAttribute("material", new Material());
        return "material/formular";
    }

    @GetMapping("/{id}/bearbeiten")
    public String bearbeiten(@PathVariable Long id, Model model) {
        Material material = materialService.findeById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material nicht gefunden"));
        model.addAttribute("material", material);
        return "material/formular";
    }

    @PostMapping("/speichern")
    public String speichern(@ModelAttribute Material material) {
        materialService.speichern(material);
        return "redirect:/material";
    }

    @PostMapping("/{id}/loeschen")
    public String loeschen(@PathVariable Long id) {
        materialService.loeschen(id);
        return "redirect:/material";
    }
}
