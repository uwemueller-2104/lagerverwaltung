package de.web192.lagersoftware.lager;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Bewusst OHNE eigene "erstellen"-Methode: Eine neue Buchung entsteht immer
 * ueber BestandService.einbuchen()/ausbuchen(), damit Bestand und Historie
 * konsistent bleiben. Dieser Service ist nur fuer Lesen und (mit Vorsicht,
 * siehe README) Loeschen der Historie in der Verwaltungs-GUI.
 */
@Service
public class BuchungService {

    private final BuchungRepository buchungRepository;

    public BuchungService(BuchungRepository buchungRepository) {
        this.buchungRepository = buchungRepository;
    }

    public List<Buchung> alle() {
        return buchungRepository.findAll();
    }

    public Optional<Buchung> findeById(Long id) {
        return buchungRepository.findById(id);
    }

    // Achtung: passt bei Aenderung von Menge/Typ NICHT automatisch den
    // Bestand an. Nur fuer das Korrigieren von Tippfehlern (z.B. falscher
    // Zeitpunkt oder falsch ausgewaehlter Benutzer) gedacht.
    public Buchung speichernDirekt(Buchung buchung) {
        return buchungRepository.save(buchung);
    }

    // Achtung: loescht nur den Historien-Eintrag, passt NICHT automatisch
    // den zugehoerigen Bestand an. Nur fuer Korrekturen von Fehleingaben
    // gedacht, nicht als regulaerer "Buchung rueckgaengig machen"-Weg.
    public void loeschen(Long id) {
        buchungRepository.deleteById(id);
    }
}
