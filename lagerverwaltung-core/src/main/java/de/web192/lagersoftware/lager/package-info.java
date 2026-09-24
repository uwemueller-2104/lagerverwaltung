/**
 * Bounded Context "Lagerverwaltung" (Core Domain).
 * <p>
 * Fachliche Verantwortung: wissen, was an welchem Lagerplatz liegt.
 * Zentrale Begriffe (Ubiquitous Language): {@link de.web192.lagersoftware.lager.Lager},
 * {@link de.web192.lagersoftware.lager.Lagerplatz}, {@link de.web192.lagersoftware.lager.Material},
 * {@link de.web192.lagersoftware.lager.Bestand}, {@link de.web192.lagersoftware.lager.Buchung}.
 * <p>
 * {@link de.web192.lagersoftware.lager.BestandService} ist der einzige erlaubte Weg, um
 * Bestand zu veraendern (Invarianten wie "nie negativer Bestand" laufen ausschliesslich
 * darueber) - im Sinne von DDD ist {@code Bestand} damit faktisch das Aggregat dieses
 * Contexts, mit {@code BestandService} als Zugriffspunkt.
 * <p>
 * Beziehung zum Context "Projektabwicklung" (Package {@code de.web192.lagersoftware.projekt}):
 * Lagerverwaltung ist bewusst der "Supplier" - sie bietet Buchungsfunktionalitaet an,
 * kennt aber inhaltlich keine Projektabwicklungs-Logik (Angebote, Lieferscheine). Die aktuelle
 * Referenz {@link de.web192.lagersoftware.lager.Buchung#getProjekt()} ist eine bewusste
 * pragmatische Ausnahme fuer v0.1; siehe README fuer die geplante Ablösung durch eine reine
 * {@code projektId}.
 */
package de.web192.lagersoftware.lager;
