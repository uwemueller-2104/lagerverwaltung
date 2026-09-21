package de.web192.lagersoftware.web.init;

import de.web192.lagersoftware.benutzer.Benutzer;
import de.web192.lagersoftware.benutzer.BenutzerRepository;
import de.web192.lagersoftware.benutzer.Rolle;
import de.web192.lagersoftware.lager.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Fuellt beim ersten Start Beispieldaten ein, damit man die Anwendung
 * sofort ausprobieren kann, ohne alles per Hand anzulegen.
 *
 * Laeuft nur, wenn noch kein Lager existiert, damit bei jedem weiteren
 * Start keine Duplikate entstehen.
 *
 * TODO (gemeinsam, sobald Login drankommt): passwortHash ist hier nur ein
 * Platzhaltertext, kein echter Hash. Vor einem echten Login muss das durch
 * einen PasswordEncoder ersetzt werden.
 */
@Component
public class TestdatenInitializer implements CommandLineRunner {

    private final LagerRepository lagerRepository;
    private final LagerplatzRepository lagerplatzRepository;
    private final MaterialRepository materialRepository;
    private final BenutzerRepository benutzerRepository;
    private final BestandService bestandService;

    public TestdatenInitializer(LagerRepository lagerRepository, LagerplatzRepository lagerplatzRepository,
                                 MaterialRepository materialRepository, BenutzerRepository benutzerRepository,
                                 BestandService bestandService) {
        this.lagerRepository = lagerRepository;
        this.lagerplatzRepository = lagerplatzRepository;
        this.materialRepository = materialRepository;
        this.benutzerRepository = benutzerRepository;
        this.bestandService = bestandService;
    }

    @Override
    public void run(String... args) {
        if (lagerRepository.count() > 0) {
            return; // schon befuellt
        }

        Benutzer admin = neuerBenutzer("admin", "Admin", Rolle.ADMIN);
        Benutzer monteur = neuerBenutzer("monteur1", "Monteur 1", Rolle.MITARBEITER);

        Lager hauptlager = neuesLager("Hauptlager", "Penzlin");
        Lager fahrzeug = neuesLager("Fahrzeug 1", "mobil");

        Lagerplatz regalA3 = neuerLagerplatz(hauptlager, "Regal A3");
        Lagerplatz regalB1 = neuerLagerplatz(hauptlager, "Regal B1");
        Lagerplatz kofferraum = neuerLagerplatz(fahrzeug, "Kofferraum");

        Material schrauben = neuesMaterial("Schrauben 4x40mm", "Stk", 200, "4001234567890");
        Material kabel = neuesMaterial("Kabel NYM-J 3x1,5", "m", 100, "4001234567906");
        Material silikon = neuesMaterial("Silikon transparent", "Kartusche", 10, "4001234567913");
        Material daemmplatte = neuesMaterial("Dämmplatte 5cm", "m²", 15, "4001234567920");
        Material rohr = neuesMaterial("Rohr PVC DN50", "m", 20, "4001234567937");

        // Anfangsbestaende ueber echte Einbuchungen anlegen, damit gleich
        // eine passende Buchungshistorie mit dabei ist.
        bestandService.einbuchen(schrauben, regalA3, admin, 600);
        bestandService.einbuchen(schrauben, kofferraum, monteur, 250);
        bestandService.einbuchen(kabel, regalA3, admin, 120);
        bestandService.einbuchen(silikon, regalB1, admin, 6);
        bestandService.einbuchen(daemmplatte, regalB1, admin, 40);
        bestandService.einbuchen(rohr, kofferraum, monteur, 28);

        // Eine Beispiel-Ausbuchung, damit die Historie beide Buchungstypen zeigt.
        bestandService.ausbuchen(schrauben, kofferraum, monteur, 30);
    }

    private Benutzer neuerBenutzer(String benutzername, String anzeigename, Rolle rolle) {
        Benutzer b = new Benutzer();
        b.setBenutzername(benutzername);
        b.setAnzeigename(anzeigename);
        b.setPasswortHash("kein-login-in-v0.1");
        b.setRolle(rolle);
        return benutzerRepository.save(b);
    }

    private Lager neuesLager(String name, String ort) {
        Lager l = new Lager();
        l.setName(name);
        l.setOrt(ort);
        return lagerRepository.save(l);
    }

    private Lagerplatz neuerLagerplatz(Lager lager, String bezeichnung) {
        Lagerplatz lp = new Lagerplatz();
        lp.setLager(lager);
        lp.setBezeichnung(bezeichnung);
        return lagerplatzRepository.save(lp);
    }

    private Material neuesMaterial(String name, String einheit, double mindestbestand, String barcode) {
        Material m = new Material();
        m.setName(name);
        m.setEinheit(einheit);
        m.setMindestbestand(mindestbestand);
        m.setBarcode(barcode);
        return materialRepository.save(m);
    }
}
