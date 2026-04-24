DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = '${app_role}') THEN
        EXECUTE format('CREATE ROLE %I LOGIN PASSWORD %L', '${app_role}', '${app_role_password}');
    ELSE
        EXECUTE format('ALTER ROLE %I LOGIN PASSWORD %L', '${app_role}', '${app_role_password}');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = '${migration_role}') THEN
        EXECUTE format('CREATE ROLE %I LOGIN PASSWORD %L', '${migration_role}', '${migration_role_password}');
    ELSE
        EXECUTE format('ALTER ROLE %I LOGIN PASSWORD %L', '${migration_role}', '${migration_role_password}');
    END IF;
END
$$;

CREATE TABLE IF NOT EXISTS text_adventure (
    id VARCHAR(255) PRIMARY KEY,
    title TEXT NOT NULL,
    learning_language VARCHAR(64) NOT NULL,
    translation_language VARCHAR(64) NOT NULL,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS text_adventure_message (
    adventure_id VARCHAR(255) NOT NULL,
    sender VARCHAR(32) NOT NULL,
    is_ending BOOLEAN NOT NULL,
    created_at BIGINT NOT NULL,
    message_index INT NOT NULL,
    PRIMARY KEY (adventure_id, message_index),
    CONSTRAINT fk_text_adventure_message_adventure
        FOREIGN KEY (adventure_id)
        REFERENCES text_adventure(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS text_adventure_sentence (
    adventure_id VARCHAR(255) NOT NULL,
    message_index INT NOT NULL,
    paragraph_index INT NOT NULL,
    sentence_index INT NOT NULL,
    language VARCHAR(64) NOT NULL,
    text TEXT NOT NULL,
    PRIMARY KEY (adventure_id, message_index, paragraph_index, sentence_index, language),
    CONSTRAINT fk_text_adventure_sentence_message
        FOREIGN KEY (adventure_id, message_index)
        REFERENCES text_adventure_message(adventure_id, message_index)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_text_adventure_updated_at
    ON text_adventure(updated_at DESC);

CREATE INDEX IF NOT EXISTS idx_text_adventure_message_adventure_id
    ON text_adventure_message(adventure_id);

CREATE INDEX IF NOT EXISTS idx_text_adventure_sentence_adventure_message
    ON text_adventure_sentence(adventure_id, message_index, paragraph_index, sentence_index);

REVOKE CREATE ON SCHEMA public FROM PUBLIC;
GRANT USAGE ON SCHEMA public TO "${app_role}";
GRANT USAGE, CREATE ON SCHEMA public TO "${migration_role}";

GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE text_adventure TO "${app_role}";
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE text_adventure_message TO "${app_role}";
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE text_adventure_sentence TO "${app_role}";

ALTER DEFAULT PRIVILEGES FOR ROLE "${migration_role}" IN SCHEMA public
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO "${app_role}";
