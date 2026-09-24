package de.web192.lagersoftware.projekt;

import de.web192.lagersoftware.lager.Material;
import de.web192.lagersoftware.lager.MaterialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Legt pro Projekt ein Angebot mit Positionen an. Die Positionen kommen per
 * Copy&Paste aus Plancraft (eine Position pro Zeile, Spalten per Tab
 * getrennt: Bezeichnung, Menge, [Einheit]) - dieselbe Idee wie im
 * urspruenglichen Prototyp, nur jetzt dauerhaft gespeichert.
 */
@Service
public class AngebotService {

    private final AngebotRepository angebotRepository;
    private final AngebotPositionRepository angebotPositionRepository;
    private final MaterialRepository materialRepository;

    public AngebotService(AngebotRepository angebotRepository, AngebotPositionRepository angebotPositionRepository,
                           MaterialRepository materialRepository) {
        this.angebotRepository = angebotRepository;
        this.angebotPositionRepository = angebotPositionRepository;
        this.materialRepository = materialRepository;
    }

    public Optional<Angebot> findeFuerProjekt(Long projektId) {
        return angebotRepository.findByProjektId(projektId);
    }

    public List<AngebotPosition> positionen(Long angebotId) {
        return angebotPositionRepository.findByAngebotId(angebotId);
    }

    public static class ImportZeile {
        public String materialName;
        public double mengeSoll;
        public boolean gefunden;
    }

    /**
     * Parst den eingefuegten Text und legt (falls noch nicht vorhanden) das
     * Angebot fuer das Projekt an, ersetzt dabei die bisherigen Positionen.
     * Zeilen ohne passendes Material werden trotzdem angelegt, aber als
     * "nicht gefunden" zurueckgemeldet, damit die Seite das anzeigen kann.
     */
    @Transactional
    public List<ImportZeile> importieren(Projekt projekt, String eingefuegterText) {
        Angebot angebot = angebotRepository.findByProjektId(projekt.getId()).orElseGet(() -> {
            Angebot neu = new Angebot();
            neu.setProjekt(projekt);
            return angebotRepository.save(neu);
        });

        // Bisherige Positionen ersetzen, damit ein erneuter Import nicht dupliziert.
        angebotPositionRepository.deleteAll(angebotPositionRepository.findByAngebotId(angebot.getId()));

        List<ImportZeile> ergebnis = new ArrayList<>();
        String[] zeilen = eingefuegterText.split("\n");

        for (String zeile : zeilen) {
            zeile = zeile.trim();
            if (zeile.isEmpty()) {
                continue;
            }

            String[] spalten = zeile.split("\t");
            if (spalten.length < 2) {
                spalten = zeile.split("\\s{2,}");
            }
            if (spalten.length < 2) {
                continue;
            }

            String name = spalten[0].trim();
            double menge;
            try {
                menge = Double.parseDouble(spalten[1].trim().replace(",", "."));
            } catch (NumberFormatException e) {
                continue;
            }

            ImportZeile importZeile = new ImportZeile();
            importZeile.materialName = name;
            importZeile.mengeSoll = menge;

            Optional<Material> material = materialRepository.findAll().stream()
                    .filter(m -> m.getName().equalsIgnoreCase(name))
                    .findFirst();

            if (material.isPresent()) {
                AngebotPosition position = new AngebotPosition();
                position.setAngebot(angebot);
                position.setMaterial(material.get());
                position.setMengeSoll(menge);
                angebotPositionRepository.save(position);
                importZeile.gefunden = true;
            } else {
                importZeile.gefunden = false;
            }

            ergebnis.add(importZeile);
        }

        return ergebnis;
    }
}
