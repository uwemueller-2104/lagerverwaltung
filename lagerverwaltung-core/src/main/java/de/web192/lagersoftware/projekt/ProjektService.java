package de.web192.lagersoftware.projekt;

import de.web192.lagersoftware.lager.Buchung;
import de.web192.lagersoftware.lager.BuchungRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProjektService {

    private final ProjektRepository projektRepository;
    private final AngebotRepository angebotRepository;
    private final AngebotPositionRepository angebotPositionRepository;
    private final BuchungRepository buchungRepository;

    public ProjektService(ProjektRepository projektRepository, AngebotRepository angebotRepository,
                           AngebotPositionRepository angebotPositionRepository, BuchungRepository buchungRepository) {
        this.projektRepository = projektRepository;
        this.angebotRepository = angebotRepository;
        this.angebotPositionRepository = angebotPositionRepository;
        this.buchungRepository = buchungRepository;
    }

    public List<Projekt> alle() {
        return projektRepository.findAll();
    }

    public Optional<Projekt> findeById(Long id) {
        return projektRepository.findById(id);
    }

    public Projekt speichern(Projekt projekt) {
        return projektRepository.save(projekt);
    }

    public void loeschen(Long id) {
        projektRepository.deleteById(id);
    }

    /**
     * Soll (aus dem Angebot) vs. Ist (Summe aller Ausbuchungen, die diesem
     * Projekt zugeordnet sind) je Material-Id, fuer die Projekt-Detailseite.
     */
    public Map<Long, SollIst> sollVsIst(Long projektId) {
        Map<Long, SollIst> ergebnis = new HashMap<>();

        angebotRepository.findByProjektId(projektId).ifPresent(angebot -> {
            for (AngebotPosition pos : angebotPositionRepository.findByAngebotId(angebot.getId())) {
                ergebnis.computeIfAbsent(pos.getMaterial().getId(), k -> new SollIst(pos.getMaterial().getName(), pos.getMaterial().getEinheit()))
                        .soll += pos.getMengeSoll();
            }
        });

        for (Buchung b : buchungRepository.findByProjektId(projektId)) {
            if (b.getTyp() == Buchung.BuchungsTyp.AUSBUCHUNG) {
                ergebnis.computeIfAbsent(b.getMaterial().getId(), k -> new SollIst(b.getMaterial().getName(), b.getMaterial().getEinheit()))
                        .ist += b.getMenge();
            }
        }

        return ergebnis;
    }

    public static class SollIst {
        public final String materialName;
        public final String einheit;
        public double soll;
        public double ist;

        public SollIst(String materialName, String einheit) {
            this.materialName = materialName;
            this.einheit = einheit;
        }
    }
}
