# Lagersoftware – Version 0.1

Prototyp einer Lagersoftware fuer eine Handwerksfirma, ergaenzend zu Plancraft.
Version 0.1 bewusst ohne KI-Anbindung – dafuer mit Projekt-, Angebots- und
Lieferschein-Verwaltung als Kern.

## Module

Aufgeteilt nach der 3-Schichten-Architektur:

- **benutzerverwaltung** – wiederverwendbares Modul fuer Benutzer und Rollen
  (`Benutzer`, `Rolle`). Soll sich unveraendert auch in anderen Projekten
  einsetzen lassen.
- **lagerverwaltung-core** – Geschaeftslogik- und Datenzugriffsschicht:
  - Paket `lager` (Bounded Context "Lagerverwaltung"): `Lager`, `Lagerplatz`,
    `Material`, `Bestand` (Menge je Material und Lagerplatz), `Buchung`
    (Ein-/Ausbuchung, optional einem `Projekt` zugeordnet).
  - Paket `projekt` (Bounded Context "Projektabwicklung"): `Projekt`,
    `Angebot`/`AngebotPosition` (Soll-Materialbedarf, per Copy&Paste-Import
    aus Plancraft befuellt) und `Lieferschein`/`LieferscheinPosition` (Ware,
    die fuer ein Projekt bestellt wurde und im eigenen Lager ankommt – jede
    Position loest beim Anlegen automatisch eine echte Einbuchung ueber
    `BestandService` aus).
  - `ProjektService.sollVsIst(projektId)` vergleicht Angebots-Soll-Mengen
    mit der Summe der projektbezogenen Ausbuchungen.
- **lagerverwaltung-web** – Praesentationsschicht, die lauffaehige
  Spring-Boot-Anwendung.
  - Verwaltungs-GUI (volle Bearbeitung) fuer Lager, Lagerplaetze, Material,
    Bestand, Buchungen.
  - Verwaltungs-GUI fuer Projekte: Projektliste/-formular, Detailseite mit
    Soll-vs-Ist-Auswertung, Angebot-Import (Copy&Paste aus Plancraft) und
    Lieferschein-Erfassung (mehrere Positionen je Lieferschein, jede loest
    eine Einbuchung aus).
  - Mobile Ansicht unter `/mobil` (nur Anzeigen + Ein-/Ausbuchen, kein
    Bearbeiten von Stammdaten): Bestandsuebersicht als Karten, Buchungs-
    formular mit Kamera-Scan (Barcode/QR ueber `html5-qrcode`), das
    gescannte Material automatisch per REST-Endpunkt
    `/api/material/barcode/{code}` im Formular auswaehlt.

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
- http://localhost:8080/mobil – mobile Ansicht (Bestand ansehen, buchen, scannen)
- http://localhost:8080/projekt – Projekte, Angebote, Lieferscheine

Die Datenbank liegt als lokale Datei unter `lagerverwaltung-web/data/`.

**Einmalig nach dem Update auf Flyway:** Falls bei dir schon eine
`lagerverwaltung-web/data/`-Datenbank aus der Zeit vor Flyway existiert,
bitte einmal löschen (einfach den Ordner `data/` entfernen), bevor du
`mvn spring-boot:run` startest. Flyway erwartet beim ersten Lauf ein leeres
Schema und legt es dann selbst per `V1__initial_schema.sql` neu an;
`TestdatenInitializer` füllt die Beispieldaten danach automatisch wieder.

## HTTPS für den Kamera-Scan vom Handy

Browser verweigern Kamerazugriff (`getUserMedia`) außerhalb eines sicheren
Kontexts (`https` oder `localhost`). Da das Handy den Rechner über die
lokale Netzwerk-IP anspricht, reicht normales `http` nicht.

1. Eigene lokale IP herausfinden: `ipconfig` (Windows) bzw. `ifconfig`/`ip a`
   (Mac/Linux).
2. Selbstsigniertes Zertifikat erzeugen, im Ordner
   `lagerverwaltung-web/src/main/resources/`:
   ```
   keytool -genkeypair -alias lagersoftware -keyalg RSA -keysize 2048 -storetype PKCS12 \
     -keystore keystore.p12 -validity 3650 -dname "CN=lagersoftware" \
     -ext "SAN=ip:DEINE-IP,ip:127.0.0.1"
   ```
3. In `application.properties` die auskommentierten `server.ssl.*`-Zeilen
   aktivieren (und die obere `server.port=8080`-Zeile auskommentieren).
4. Starten, am Rechner `https://localhost:8443/` aufrufen, Zertifikatswarnung
   einmalig bestätigen ("Erweitert" → "Trotzdem fortfahren").
5. Vom Handy (selbes WLAN) `https://DEINE-IP:8443/mobil/buchen` aufrufen,
   Warnung ebenfalls einmalig bestätigen. Danach funktioniert der
   Kamera-Scan.

Das Zertifikat ist bewusst nicht Teil des Repos (`.gitignore`), weil es an
die eigene lokale IP gebunden ist – jede Person, die mitentwickelt, erzeugt
sich ihr eigenes.

## Datenbank-Migrationen (Flyway)

Das Schema wird nicht mehr von Hibernate automatisch erzeugt (`ddl-auto=validate`),
sondern von Flyway aus versionierten SQL-Skripten unter
`lagerverwaltung-web/src/main/resources/db/migration/`. Sie laufen beim
Start der Anwendung automatisch, in Reihenfolge ihrer Versionsnummer.

Für eine künftige Schemaänderung: neue Datei nach dem Muster
`V<nächste Nummer>__kurze_beschreibung.sql` anlegen (z.B.
`V2__benutzer_microsoft_login.sql`), niemals eine bereits ausgelieferte
Migration nachträglich ändern – Flyway erkennt das an der Prüfsumme und
bricht sonst beim nächsten Start ab.

## Tests

`lagerverwaltung-core` hat `spring-boot-starter-test` (JUnit 5, AssertJ,
Mockito) als Test-Dependency. Tests laufen ohne Datenbank/Spring-Kontext,
indem `BestandService` & Co. direkt mit gemockten Repositories konstruiert
werden (schnell, kein Setup nötig).

```bash
mvn test -pl lagerverwaltung-core
```

Stand: `BestandServiceTest` deckt bisher den Fall "einbuchen legt neuen
Bestand an" ab (Arrange/Act/Assert mit `@Mock` + `ArgumentCaptor`). Als
Nächstes: Fehlerfall `ausbuchen()` bei zu wenig Bestand (`assertThrows`).

## Stand

Projekt-Setup steht: Module, Entities, Repositories. Als naechstes gemeinsam:

- [x] Buchungslogik in `BestandService` (einbuchen/ausbuchen, inkl. optionaler Projekt-Zuordnung) implementieren
- [x] Verwaltungs-GUI (CRUD für Lager, Lagerplätze, Material, Bestand, Buchungen)
- [x] Testdaten beim Start (siehe `TestdatenInitializer`)
- [x] Mobile Ansicht: Grundgerüst + Barcode-/QR-Scan (html5-qrcode) + REST-Endpunkt `/api/material/barcode/{code}` + HTTPS-Anleitung stehen
- [x] Datenmodell + Services für Projekt, Angebot (Copy&Paste-Import), Lieferschein (löst Einbuchung aus)
- [x] Erster Unit-Test für `BestandService` (einbuchen, Happy Path)
- [x] CRUD-Seiten für Projekt, Angebot-Import, Lieferschein-Erfassung
- [x] Projekt-Detailseite mit Soll-vs-Ist-Auswertung (`ProjektService.sollVsIst`)
- [x] Flyway für DB-Migrationen einführen (`V1__initial_schema.sql`, Hibernate läuft nur noch mit `ddl-auto=validate`)
- [ ] Unit-Test für `ausbuchen()`: Fehlerfall bei zu wenig Bestand (`assertThrows`)
- [ ] Unit-Test für `ausbuchen()`: Happy Path
- [ ] Unit-Tests für `AngebotService.importieren(...)` (Copy&Paste-Parsing, inkl. "Material nicht gefunden"-Fall)
- [ ] Unit-Tests für `LieferscheinService.anlegen(...)` (löst Einbuchung mit Projektbezug aus)
- [ ] Mobile Ausbuchung um optionale Projekt-Zuordnung erweitern (Baustelle wählen)
- [ ] Login/Berechtigungen auf Basis von `Benutzer`/`Rolle` (geplant: Microsoft Entra ID + lokaler Fallback, siehe Chat-Verlauf)
