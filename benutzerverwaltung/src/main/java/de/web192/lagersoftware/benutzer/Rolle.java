package de.web192.lagersoftware.benutzer;

/**
 * Rollen fuer Version 0.1.
 *
 * ADMIN         -> darf Stammdaten in der Web-Anwendung bearbeiten (Lager, Lagerplaetze, Material, Benutzer)
 * MITARBEITER   -> darf am Handy nur anzeigen und ein-/ausbuchen
 *
 * TODO (naechster Schritt, gemeinsam): falls mehr Feinsteuerung noetig wird
 * (z.B. einzelne Rechte statt fester Rollen), kann das spaeter zu einer
 * eigenen Rollen-Entitaet mit Rechte-Liste ausgebaut werden, ohne dass
 * andere Module etwas davon merken.
 */
public enum Rolle {
    ADMIN,
    MITARBEITER
}
