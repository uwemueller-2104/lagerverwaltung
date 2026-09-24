package de.web192.lagersoftware.lager;

import de.web192.lagersoftware.benutzer.Benutzer;
import de.web192.lagersoftware.projekt.Projekt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Buchungslogik: Bestand pro Lagerplatz erhoehen/verringern und dabei
 * jede Buchung protokollieren. Beides passiert in derselben Transaktion,
 * damit Bestand und Buchungshistorie nie auseinanderlaufen.
 */
@Service
public class BestandService {

    private final BestandRepository bestandRepository;
    private final BuchungRepository buchungRepository;

    public BestandService(BestandRepository bestandRepository, BuchungRepository buchungRepository) {
        this.bestandRepository = bestandRepository;
        this.buchungRepository = buchungRepository;
    }

    public List<Bestand> alle() {
        return bestandRepository.findAll();
    }

    public Optional<Bestand> findeById(Long id) {
        return bestandRepository.findById(id);
    }

    // Direkte Korrektur eines Bestand-Datensatzes ueber die CRUD-Seite,
    // OHNE Buchung anzulegen. Fuer den normalen Ablauf (Ein-/Ausbuchen)
    // bitte einbuchen()/ausbuchen() verwenden, damit die Historie stimmt.
    public Bestand speichernDirekt(Bestand bestand) {
        return bestandRepository.save(bestand);
    }

    public void loeschen(Long id) {
        bestandRepository.deleteById(id);
    }

    @Transactional
    public void einbuchen(Material material, Lagerplatz lagerplatz, Benutzer benutzer, double menge) {
        einbuchen(material, lagerplatz, benutzer, menge, null);
    }

    @Transactional
    public void einbuchen(Material material, Lagerplatz lagerplatz, Benutzer benutzer, double menge, Projekt projekt) {
        pruefeMenge(menge);

        Bestand bestand = findeBestand(material, lagerplatz).orElseGet(() -> {
            Bestand neu = new Bestand();
            neu.setMaterial(material);
            neu.setLagerplatz(lagerplatz);
            neu.setMenge(0);
            return neu;
        });
        bestand.setMenge(bestand.getMenge() + menge);
        bestandRepository.save(bestand);

        protokolliere(material, lagerplatz, benutzer, projekt, menge, Buchung.BuchungsTyp.EINBUCHUNG);
    }

    @Transactional
    public void ausbuchen(Material material, Lagerplatz lagerplatz, Benutzer benutzer, double menge) {
        ausbuchen(material, lagerplatz, benutzer, menge, null);
    }

    @Transactional
    public void ausbuchen(Material material, Lagerplatz lagerplatz, Benutzer benutzer, double menge, Projekt projekt) {
        pruefeMenge(menge);

        Bestand bestand = findeBestand(material, lagerplatz)
                .orElseThrow(() -> new IllegalStateException(
                        "Kein Bestand von \"" + material.getName() + "\" an diesem Lagerplatz vorhanden."));

        if (bestand.getMenge() < menge) {
            throw new IllegalStateException(
                    "Nicht genug Bestand: vorhanden " + bestand.getMenge() + " " + material.getEinheit()
                            + ", angefordert " + menge + " " + material.getEinheit());
        }

        bestand.setMenge(bestand.getMenge() - menge);
        bestandRepository.save(bestand);

        protokolliere(material, lagerplatz, benutzer, projekt, menge, Buchung.BuchungsTyp.AUSBUCHUNG);
    }

    private java.util.Optional<Bestand> findeBestand(Material material, Lagerplatz lagerplatz) {
        return bestandRepository.findByMaterialIdAndLagerplatzId(material.getId(), lagerplatz.getId());
    }

    private void protokolliere(Material material, Lagerplatz lagerplatz, Benutzer benutzer, Projekt projekt,
                                double menge, Buchung.BuchungsTyp typ) {
        Buchung buchung = new Buchung();
        buchung.setMaterial(material);
        buchung.setLagerplatz(lagerplatz);
        buchung.setBenutzer(benutzer);
        buchung.setProjekt(projekt);
        buchung.setMenge(menge);
        buchung.setTyp(typ);
        buchungRepository.save(buchung);
    }

    private void pruefeMenge(double menge) {
        if (menge <= 0) {
            throw new IllegalArgumentException("Menge muss groesser als 0 sein.");
        }
    }
}
