-- Digital Therapy Assistant - Schema
-- H2 compatible DDL; Hibernate ddl-auto=update will keep in sync.

CREATE TABLE IF NOT EXISTS users (
    id                  UUID         PRIMARY KEY,
    email               VARCHAR(255) NOT NULL UNIQUE,
    password_hash       VARCHAR(255) NOT NULL,
    name                VARCHAR(100),
    onboarding_complete BOOLEAN      DEFAULT FALSE,
    onboarding_path     VARCHAR(50)  DEFAULT 'SELF',
    severity_level      VARCHAR(50)  DEFAULT 'MILD',
    streak_days         INTEGER      DEFAULT 0,
    created_at          TIMESTAMP,
    updated_at          TIMESTAMP
);

CREATE TABLE IF NOT EXISTS session_modules (
    id          UUID         PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    order_index INTEGER
);

CREATE TABLE IF NOT EXISTS cbt_sessions (
    id               UUID         PRIMARY KEY,
    module_id        UUID         REFERENCES session_modules(id),
    title            VARCHAR(255) NOT NULL,
    description      TEXT,
    duration_minutes INTEGER,
    order_index      INTEGER
);

CREATE TABLE IF NOT EXISTS session_objectives (
    session_id UUID         REFERENCES cbt_sessions(id),
    objective  VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS session_modalities (
    session_id UUID        REFERENCES cbt_sessions(id),
    modality   VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS user_sessions (
    id             UUID        PRIMARY KEY,
    user_id        UUID        NOT NULL REFERENCES users(id),
    cbt_session_id UUID        REFERENCES cbt_sessions(id),
    status         VARCHAR(50) DEFAULT 'IN_PROGRESS',
    started_at     TIMESTAMP,
    ended_at       TIMESTAMP,
    mood_before    INTEGER,
    mood_after     INTEGER
);

CREATE TABLE IF NOT EXISTS chat_messages (
    id              UUID        PRIMARY KEY,
    user_session_id UUID        NOT NULL REFERENCES user_sessions(id),
    role            VARCHAR(50) NOT NULL,
    content         TEXT        NOT NULL,
    modality        VARCHAR(50) DEFAULT 'TEXT',
    timestamp       TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cognitive_distortions (
    id          VARCHAR(100) PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS distortion_examples (
    distortion_id VARCHAR(100) REFERENCES cognitive_distortions(id),
    example       TEXT
);

CREATE TABLE IF NOT EXISTS distortion_reframing_questions (
    distortion_id VARCHAR(100) REFERENCES cognitive_distortions(id),
    question      TEXT
);

CREATE TABLE IF NOT EXISTS diary_entries (
    id                   UUID        PRIMARY KEY,
    user_id              UUID        NOT NULL REFERENCES users(id),
    situation            TEXT,
    automatic_thought    TEXT,
    alternative_thought  TEXT,
    mood_before          INTEGER,
    mood_after           INTEGER,
    belief_rating_before INTEGER,
    belief_rating_after  INTEGER,
    created_at           TIMESTAMP,
    deleted              BOOLEAN     DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS diary_emotions (
    diary_entry_id UUID        REFERENCES diary_entries(id),
    emotion        VARCHAR(100),
    intensity      INTEGER
);

CREATE TABLE IF NOT EXISTS diary_entry_distortions (
    diary_entry_id UUID         REFERENCES diary_entries(id),
    distortion_id  VARCHAR(100) REFERENCES cognitive_distortions(id)
);

CREATE TABLE IF NOT EXISTS trusted_contacts (
    id           UUID         PRIMARY KEY,
    user_id      UUID         NOT NULL REFERENCES users(id),
    name         VARCHAR(255) NOT NULL,
    phone        VARCHAR(50),
    relationship VARCHAR(100)
);
