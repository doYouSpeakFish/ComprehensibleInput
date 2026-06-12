DROP TABLE IF EXISTS text_adventure_sentence;
DROP TABLE IF EXISTS text_adventure_message;
DELETE FROM text_adventure;

ALTER TABLE text_adventure ADD COLUMN account_id VARCHAR(255);
ALTER TABLE text_adventure
    ADD CONSTRAINT text_adventure_account_id_fk
    FOREIGN KEY (account_id)
    REFERENCES account_user(id)
    ON DELETE CASCADE;

CREATE INDEX text_adventure_account_updated_idx ON text_adventure(account_id, updated_at DESC);

CREATE TABLE text_adventure_message (
    id VARCHAR(255) PRIMARY KEY,
    adventure_id VARCHAR(255) NOT NULL REFERENCES text_adventure(id) ON DELETE CASCADE,
    parent_message_id VARCHAR(255) REFERENCES text_adventure_message(id) ON DELETE CASCADE,
    "type" VARCHAR(32) NOT NULL,
    text TEXT,
    is_ending BOOLEAN NOT NULL,
    created_at BIGINT NOT NULL
);

CREATE TABLE text_adventure_sentence (
    message_id VARCHAR(255) NOT NULL REFERENCES text_adventure_message(id) ON DELETE CASCADE,
    paragraph_index INTEGER NOT NULL,
    sentence_index INTEGER NOT NULL,
    "language" VARCHAR(64) NOT NULL,
    text TEXT NOT NULL,
    PRIMARY KEY (message_id, paragraph_index, sentence_index, "language")
);

GRANT SELECT, INSERT, UPDATE, DELETE ON text_adventure_message TO "${app_role}";
GRANT SELECT, INSERT, UPDATE, DELETE ON text_adventure_sentence TO "${app_role}";
