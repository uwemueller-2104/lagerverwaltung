package de.web192.lagersoftware.web.controller;

import de.web192.lagersoftware.benutzer.Benutzer;
import de.web192.lagersoftware.benutzer.BenutzerRepository;
import de.web192.lagersoftware.benutzer.Rolle;
import de.web192.lagersoftware.web.security.BenutzerPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Verwaltung von Benutzern und ihrer Rolle. Nur fuer ADMIN (siehe
 * SecurityConfig).
 *
 * Bewusst kein "Loeschen": Benutzer stehen als Fremdschluessel in Buchung
 * und Lieferschein (wer hat das gebucht/angenommen?), ein Loeschen wuerde
 * entweder an der DB-Fremdschluesselbeziehung scheitern oder Historie
 * kaputt machen. Stattdessen gibt es De-/Aktivieren (Feld "aktiv") - ein
 * deaktivierter Benutzer kann sich nicht mehr einloggen (siehe
 * BenutzerPrincipal.isEnabled()), bleibt aber in der Historie sichtbar.
 */
@Controller
@RequestMapping("/benutzer")
public class BenutzerController {

    private final BenutzerRepository benutzerRepository;
    private final PasswordEncoder passwordEncoder;

    public BenutzerController(BenutzerRepository benutzerRepository, PasswordEncoder passwordEncoder) {
        this.benutzerRepository = benutzerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("benutzerListe", benutzerRepository.findAll());
        return "benutzer/liste";
    }

    @GetMapping("/neu")
    public String neuesFormular(Model model) {
        model.addAttribute("benutzer", new Benutzer());
        model.addAttribute("rolleOptionen", Rolle.values());
        return "benutzer/formular";
    }

    @GetMapping("/{id}/bearbeiten")
    public String bearbeiten(@PathVariable Long id, Model model) {
        Benutzer benutzer = benutzerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Benutzer nicht gefunden"));
        model.addAttribute("benutzer", benutzer);
        model.addAttribute("rolleOptionen", Rolle.values());
        return "benutzer/formular";
    }

    @PostMapping("/speichern")
    public String speichern(@RequestParam(required = false) Long id,
                             @RequestParam String benutzername,
                             @RequestParam String anzeigename,
                             @RequestParam Rolle rolle,
                             @RequestParam(required = false) String neuesPasswort,
                             Model model) {
        // Formular-Eingabe fuer eine erneute Anzeige bei Fehlern vorbereiten,
        // BEVOR irgendwas gespeichert wird - so gehen bei einem Fehler keine
        // eingegebenen Werte verloren.
        Benutzer eingabe = new Benutzer();
        eingabe.setId(id);
        eingabe.setBenutzername(benutzername);
        eingabe.setAnzeigename(anzeigename);
        eingabe.setRolle(rolle);
        eingabe.setAktiv(true);

        var vorhandenerMitName = benutzerRepository.findByBenutzername(benutzername);
        if (vorhandenerMitName.isPresent() && !vorhandenerMitName.get().getId().equals(id)) {
            model.addAttribute("fehler", "Benutzername \"" + benutzername + "\" ist bereits vergeben.");
            model.addAttribute("benutzer", eingabe);
            model.addAttribute("rolleOptionen", Rolle.values());
            return "benutzer/formular";
        }

        Benutzer benutzer;
        if (id != null) {
            benutzer = benutzerRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Benutzer nicht gefunden"));
        } else {
            if (neuesPasswort == null || neuesPasswort.isBlank()) {
                model.addAttribute("fehler", "Für einen neuen Benutzer wird ein Passwort benötigt.");
                model.addAttribute("benutzer", eingabe);
                model.addAttribute("rolleOptionen", Rolle.values());
                return "benutzer/formular";
            }
            benutzer = new Benutzer();
            benutzer.setAktiv(true);
        }

        benutzer.setBenutzername(benutzername);
        benutzer.setAnzeigename(anzeigename);
        benutzer.setRolle(rolle);
        if (neuesPasswort != null && !neuesPasswort.isBlank()) {
            benutzer.setPasswortHash(passwordEncoder.encode(neuesPasswort));
        }
        benutzerRepository.save(benutzer);
        return "redirect:/benutzer";
    }

    @PostMapping("/{id}/deaktivieren")
    public String deaktivieren(@PathVariable Long id, @AuthenticationPrincipal BenutzerPrincipal angemeldeter,
                                RedirectAttributes redirectAttributes) {
        Benutzer benutzer = benutzerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Benutzer nicht gefunden"));
        if (benutzer.getBenutzername().equals(angemeldeter.getUsername())) {
            redirectAttributes.addFlashAttribute("fehler", "Du kannst dich nicht selbst deaktivieren.");
            return "redirect:/benutzer";
        }
        benutzer.setAktiv(false);
        benutzerRepository.save(benutzer);
        return "redirect:/benutzer";
    }

    @PostMapping("/{id}/aktivieren")
    public String aktivieren(@PathVariable Long id) {
        Benutzer benutzer = benutzerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Benutzer nicht gefunden"));
        benutzer.setAktiv(true);
        benutzerRepository.save(benutzer);
        return "redirect:/benutzer";
    }
}
