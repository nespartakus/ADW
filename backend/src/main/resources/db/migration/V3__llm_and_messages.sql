-- Fase 2: LLM configuration and diagnostic messages.

CREATE TABLE llm_configuration (
    id                UUID PRIMARY KEY,
    owner_id          UUID,
    provider          VARCHAR(50)  NOT NULL,
    model             VARCHAR(100) NOT NULL,
    encrypted_api_key TEXT,
    base_url          VARCHAR(500),
    enabled           BOOLEAN      NOT NULL DEFAULT FALSE,
    updated_at        TIMESTAMPTZ  NOT NULL
);

CREATE TABLE diagnostic_message (
    id                 UUID PRIMARY KEY,
    session_id         UUID NOT NULL REFERENCES diagnostic_session (id),
    role               VARCHAR(20) NOT NULL,
    content            TEXT,
    structured_payload JSONB,
    created_at         TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_diagnostic_message_session ON diagnostic_message (session_id);
