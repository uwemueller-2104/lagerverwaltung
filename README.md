# Lagersoftware – Version 0.1

Prototyp einer Lagersoftware fuer eine Handwerksfirma, ergaenzend zu Plancraft.
Version 0.1 bewusst ohne KI-Anbindung und ohne Plancraft-Import – nur das Grundgeruest.

## Module

Aufgeteilt nach der 3-Schichten-Architektur:

- **benutzerverwaltung** – wiederverwendbares Modul fuer Benutzer und Rollen
  (`Benutzer`, `Rolle`). Soll sich unveraendert auch in anderen Projekten
  einsetzen lassen.
- **lagerverwaltung-core** – Geschaeftslogik- und Datenzugriffsschicht:
  `Lager`, `Lagerplatz`, `Material`, `Bestand` (Menge je Material und
  Lagerplatz), `Buchung` (Ein-/Ausbuchung).
- **lagerverwaltung-web** – Praesentationsschicht, die lauffaehige
  Spring-Boot-Anwendung. Spaeter: Verwaltungs-GUI (volle Bearbeitung) und
  mobile Ansicht (nur Anzeigen + Buchen), je nach Rolle.

## Starten

Voraussetzung: Java 21 und Maven installiert.

```bash
mvn clean install
mvn spring-boot:run -pl lagerverwaltung-web
```

Danach im Browser:
- http://localhost:8080/ – Startseite
- http://localhost:8080/api/health – Testendpunkt, sollte "Lagersoftware v0.1 laeuft" zeigen
- http://localhost:8080/h2-console – Datenbank-Konsole (JDBC-URL: `jdbc:h2:file:./data/lagersoftware`)

Die Datenbank liegt als lokale Datei unter `lagerverwaltung-web/data/`.

## Stand

Projekt-Setup steht: Module, Entities, Repositories. Als naechstes gemeinsam:

- [x] Buchungslogik in `BestandService` (einbuchen/ausbuchen) implementieren
- [x] Verwaltungs-GUI (CRUD für Lager, Lagerplätze, Material, Bestand, Buchungen)
- [x] Testdaten beim Start (siehe `TestdatenInitializer`)
- [ ] REST-Endpunkte fuer Material, Lagerplaetze, Buchungen (falls fuer die mobile Ansicht als eigene API gebraucht)
- [ ] Mobile Ansicht (Anzeigen + Ein-/Ausbuchen, inkl. Barcode-Scan wie im JS-Prototyp)
- [ ] Login/Berechtigungen auf Basis von `Benutzer`/`Rolle`
