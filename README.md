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
  - Login (lokal, Benutzername/Passwort) mit rollenbasierter Zugriffs-
    steuerung ueber Spring Security, siehe Abschnitt "Login" unten.

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

## Login

Die Anwendung verlangt eine Anmeldung (Spring Security, Formular-Login unter
`/login`). Benutzer legt man unter `/benutzer` an (nur für ADMIN sichtbar).
Zwei Beispiel-Logins stehen nach dem ersten Start zur Verfügung:

| Benutzername | Passwort      | Rolle       |
|--------------|---------------|-------------|
| `admin`      | `admin123`    | ADMIN       |
| `monteur1`   | `monteur123`  | MITARBEITER |

**Nur zum Ausprobieren auf dem eigenen Rechner** – vor einem echten Einsatz
unbedingt eigene Zugangsdaten setzen (unter `/benutzer` ein neues Passwort
für `admin` vergeben, oder gleich einen eigenen Admin-Benutzer anlegen und
die Beispiel-Logins deaktivieren).

Rollen und Rechte (siehe `Rolle.java`):
- **ADMIN**: darf zusätzlich Stammdaten bearbeiten – Lager, Lagerplätze,
  Material, Projekte, Angebote, Lieferscheine – und Benutzer verwalten
  (`/benutzer`): anlegen, Rolle/Passwort ändern, de-/aktivieren.
- **MITARBEITER**: darf alles ansehen sowie Bestand ein-/ausbuchen
  (Verwaltungs-GUI und mobile Ansicht), aber keine Stammdaten anlegen oder
  ändern. Ruft er/sie trotzdem eine Stammdaten-Seite auf, kommt eine
  403-Fehlerseite; die entsprechenden Menüpunkte sind für MITARBEITER
  in der Navigation deshalb von vornherein ausgeblendet.

Benutzer werden nie gelöscht, nur deaktiviert (De-/Aktivieren-Button in der
Liste) – sie stecken als Fremdschlüssel in Buchungen und Lieferscheinen
("wer hat das gebucht/angenommen?"), ein Löschen würde entweder an der
Datenbank scheitern oder Historie zerstören. Ein deaktivierter Benutzer kann
sich nicht mehr einloggen. Man kann sich nicht selbst deaktivieren.

**Einmalig nach dem Update auf den Login:** Falls bei dir schon eine
`lagerverwaltung-web/data/`-Datenbank von vor dieser Änderung existiert,
hat `admin`/`monteur1` noch das alte Platzhalter-Passwort ohne echten
Hash – der Login schlägt dann mit "Benutzername oder Passwort ist falsch"
fehl, obwohl die Zugangsdaten stimmen. Abhilfe: Ordner `data/` einmal löschen
und neu starten (siehe oben, gleiches Prinzip wie beim Flyway-Update), dann
legt `TestdatenInitializer` `admin`/`monteur1` mit echten BCrypt-Hashes neu
an. Alternativ ab jetzt: den Hash direkt über `/benutzer` neu setzen.

**Geplant, noch nicht umgesetzt:** Microsoft Entra ID (OIDC) als zweite
Login-Möglichkeit neben dem lokalen Login. Der lokale Login bleibt dabei
als Fallback bestehen, die Rollenzuweisung für neue Microsoft-Benutzer
erfolgt weiterhin manuell in der App, und es ist nur Single-Tenant (nur
für die eigene Firma) geplant – siehe Backlog unten.

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
- [x] Lokaler Login (Benutzername/Passwort) mit rollenbasierter Zugriffssteuerung (Spring Security, BCrypt, siehe Abschnitt "Login")
- [x] Eigene Oberfläche zum Anlegen/Bearbeiten von Benutzern und Rollen (`/benutzer`, nur ADMIN; De-/Aktivieren statt Löschen)
- [ ] Microsoft Entra ID (OIDC) als zweite Login-Möglichkeit neben dem lokalen Login (lokaler Login bleibt als Fallback, Rollenzuweisung weiterhin manuell in der App, Single-Tenant – siehe Chat-Verlauf für die Skizze)
