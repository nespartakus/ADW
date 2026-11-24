-- ADW initial schema: vehicles and diagnostic sessions (Fase 1).
-- UUIDs are generated application-side (Hibernate). Timestamps are UTC (timestamptz).

CREATE TABLE vehicle (
    id                 UUID PRIMARY KEY,
    owner_id           UUID,
    vin                VARCHAR(17),
    make               VARCHAR(100) NOT NULL,
    model              VARCHAR(100) NOT NULL,
    model_year         INTEGER NOT NULL,
    market             VARCHAR(50),
    platform           VARCHAR(100),
    engine_code        VARCHAR(100),
    engine_description VARCHAR(255),
    displacement       NUMERIC(6,2),
    fuel_type          VARCHAR(50),
    aspiration         VARCHAR(50),
    ecu                VARCHAR(100),
    transmission       VARCHAR(100),
    modifications      TEXT,
    notes              TEXT,
    created_at         TIMESTAMPTZ NOT NULL,
    updated_at         TIMESTAMPTZ NOT NULL,
    deleted_at         TIMESTAMPTZ
);

CREATE INDEX idx_vehicle_owner ON vehicle (owner_id);

CREATE TABLE diagnostic_session (
    id                   UUID PRIMARY KEY,
    vehicle_id           UUID NOT NULL REFERENCES vehicle (id),
    created_by           UUID,
    title                VARCHAR(200) NOT NULL,
    customer_reference   VARCHAR(200),
    complaint            TEXT,
    operating_conditions TEXT,
    status               VARCHAR(20) NOT NULL,
    started_at           TIMESTAMPTZ NOT NULL,
    closed_at            TIMESTAMPTZ
);

CREATE INDEX idx_diagnostic_session_vehicle ON diagnostic_session (vehicle_id);
