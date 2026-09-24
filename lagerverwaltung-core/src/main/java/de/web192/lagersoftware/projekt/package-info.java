/**
 * Bounded Context "Projektabwicklung" (Core Domain).
 * <p>
 * Fachliche Verantwortung: Projekte einer Handwerksfirma von Angebot bis Lieferung begleiten.
 * Zentrale Begriffe (Ubiquitous Language): {@link de.web192.lagersoftware.projekt.Projekt}
 * (Aggregate Root), {@link de.web192.lagersoftware.projekt.Angebot} /
 * {@link de.web192.lagersoftware.projekt.AngebotPosition} (Soll-Material), sowie
 * {@link de.web192.lagersoftware.projekt.Lieferschein} / {@link de.web192.lagersoftware.projekt.LieferscheinPosition}
 * (Ist-Wareneingang). {@link de.web192.lagersoftware.projekt.ProjektService#sollVsIst(Long)}
 * ist die zentrale fachliche Auswertung dieses Contexts.
 * <p>
 * Beziehung zum Context "Lagerverwaltung" (Package {@code de.web192.lagersoftware.lager}):
 * Projektabwicklung ist der "Customer" - sie kennt die Lagerverwaltung und nutzt
 * {@link de.web192.lagersoftware.lager.BestandService}, um bei jedem Lieferschein eine echte
 * Einbuchung auszuloesen (siehe {@link de.web192.lagersoftware.projekt.LieferscheinService#anlegen}).
 * Die Abhaengigkeit geht damit bewusst nur in eine Richtung: Projektabwicklung darf
 * Lagerverwaltung kennen, umgekehrt nicht (siehe README zur geplanten reinen {@code projektId}
 * in {@code Buchung}, die diese Regel technisch absichert).
 */
package de.web192.lagersoftware.projekt;
