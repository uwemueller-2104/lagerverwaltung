-- V1: Ausgangsschema, so wie es Hibernate ddl-auto=update bisher aus den
-- Entities erzeugt hat. Ab jetzt ist Flyway fuer das Schema zustaendig,
-- Hibernate laeuft nur noch mit ddl-auto=validate (siehe application.properties).
--
-- Reihenfolge der Tabellen richtet sich nach den Fremdschluesseln:
-- erst die, auf die andere verweisen, dann die verweisenden.

create table benutzer (
    id             bigint auto_increment primary key,
    benutzername   varchar(255) not null unique,
    anzeigename    varchar(255) not null,
    -- nullable, weil ein rein per Microsoft angemeldeter Benutzer (geplant)
    -- kein lokales Passwort braucht.
    passwort_hash  varchar(255),
    -- Hibernate erzeugt bei @Enumerated(STRING) ohne explizite @Column(length=...)
    -- immer varchar(255) - das muss hier exakt matchen, sonst meckert
    -- ddl-auto=validate beim Start ueber eine abweichende Spaltenlaenge.
    rolle          varchar(255) not null,
    aktiv          boolean      not null default true
);

create table lager (
    id    bigint auto_increment primary key,
    name  varchar(255) not null,
    ort   varchar(255)
);

create table lagerplatz (
    id            bigint auto_increment primary key,
    lager_id      bigint       not null references lager(id),
    bezeichnung   varchar(255) not null
);

create table material (
    id              bigint auto_increment primary key,
    name            varchar(255) not null,
    einheit         varchar(255) not null,
    mindestbestand  double       not null default 0,
    barcode         varchar(255) unique
);

create table projekt (
    id      bigint auto_increment primary key,
    name    varchar(255) not null,
    kunde   varchar(255),
    status  varchar(255) not null default 'OFFEN'
);

create table angebot (
    id           bigint auto_increment primary key,
    projekt_id   bigint not null unique references projekt(id),
    erstellt_am  date   not null
);

create table angebot_position (
    id           bigint auto_increment primary key,
    angebot_id   bigint not null references angebot(id),
    material_id  bigint not null references material(id),
    menge_soll   double not null
);

create table lieferschein (
    id          bigint auto_increment primary key,
    projekt_id  bigint       not null references projekt(id),
    nummer      varchar(255),
    datum       date         not null
);

create table lieferschein_position (
    id                bigint auto_increment primary key,
    lieferschein_id   bigint not null references lieferschein(id),
    material_id       bigint not null references material(id),
    lagerplatz_id     bigint not null references lagerplatz(id),
    menge             double not null
);

create table bestand (
    id             bigint auto_increment primary key,
    material_id    bigint not null references material(id),
    lagerplatz_id  bigint not null references lagerplatz(id),
    menge          double not null,
    constraint uk_bestand_material_lagerplatz unique (material_id, lagerplatz_id)
);

create table buchung (
    id             bigint auto_increment primary key,
    material_id    bigint       not null references material(id),
    lagerplatz_id  bigint       not null references lagerplatz(id),
    benutzer_id    bigint       not null references benutzer(id),
    projekt_id     bigint       references projekt(id),
    typ            varchar(255) not null,
    menge          double       not null,
    zeitpunkt      timestamp    not null
);
