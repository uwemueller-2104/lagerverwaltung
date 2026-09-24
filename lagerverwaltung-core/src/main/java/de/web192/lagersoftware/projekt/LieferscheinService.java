package de.web192.lagersoftware.projekt;

import de.web192.lagersoftware.benutzer.Benutzer;
import de.web192.lagersoftware.lager.BestandService;
import de.web192.lagersoftware.lager.Lagerplatz;
import de.web192.lagersoftware.lager.Material;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Ein Lieferschein dokumentiert Ware, die fuer ein Projekt bestellt wurde
 * und im eigenen Lager ankommt (nicht direkt auf der Baustelle). Jede
 * Position loest deshalb beim Anlegen eine echte Einbuchung ueber
 * BestandService aus - Bestand und Buchungshistorie bleiben so konsistent,
 * genau wie bei jeder anderen Einbuchung auch.
 */
@Service
public class LieferscheinService {

    private final LieferscheinRepository lieferscheinRepository;
    private final LieferscheinPositionRepository lieferscheinPositionRepository;
    private final BestandService bestandService;

    public LieferscheinService(LieferscheinRepository lieferscheinRepository,
                                LieferscheinPositionRepository lieferscheinPositionRepository,
                                BestandService bestandService) {
        this.lieferscheinRepository = lieferscheinRepository;
        this.lieferscheinPositionRepository = lieferscheinPositionRepository;
        this.bestandService = bestandService;
    }

    public List<Lieferschein> alle() {
        return lieferscheinRepository.findAll();
    }

    public List<Lieferschein> fuerProjekt(Long projektId) {
        return lieferscheinRepository.findByProjektId(projektId);
    }

    public Optional<Lieferschein> findeById(Long id) {
        return lieferscheinRepository.findById(id);
    }

    public List<LieferscheinPosition> positionen(Long lieferscheinId) {
        return lieferscheinPositionRepository.findByLieferscheinId(lieferscheinId);
    }

    public record NeuePosition(Material material, Lagerplatz lagerplatz, double menge) {
    }

    @Transactional
    public Lieferschein anlegen(Projekt projekt, String nummer, Benutzer benutzer, List<NeuePosition> positionen) {
        Lieferschein lieferschein = new Lieferschein();
        lieferschein.setProjekt(projekt);
        lieferschein.setNummer(nummer);
        lieferschein = lieferscheinRepository.save(lieferschein);

        for (NeuePosition p : positionen) {
            LieferscheinPosition position = new LieferscheinPosition();
            position.setLieferschein(lieferschein);
            position.setMaterial(p.material());
            position.setLagerplatz(p.lagerplatz());
            position.setMenge(p.menge());
            lieferscheinPositionRepository.save(position);

            // Ware kommt im Lager an -> echte Einbuchung, mit Projektbezug
            // fuer die spaetere Soll/Ist-Auswertung.
            bestandService.einbuchen(p.material(), p.lagerplatz(), benutzer, p.menge(), projekt);
        }

        return lieferschein;
    }

    public void loeschen(Long id) {
        // Achtung: loescht nur den Lieferschein-Datensatz, macht die bereits
        // erfolgte Einbuchung NICHT rueckgaengig. Fuer eine echte Korrektur
        // eine entsprechende Ausbuchung ueber /buchung vornehmen.
        lieferscheinRepository.deleteById(id);
    }
}
