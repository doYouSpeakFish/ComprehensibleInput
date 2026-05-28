ALTER TABLE text_adventure
    ADD COLUMN IF NOT EXISTS account_id VARCHAR(255);

ALTER TABLE text_adventure
    DROP CONSTRAINT IF EXISTS fk_text_adventure_account;

ALTER TABLE text_adventure
    ADD CONSTRAINT fk_text_adventure_account
        FOREIGN KEY (account_id)
        REFERENCES account_user(id)
        ON DELETE CASCADE;

DELETE FROM text_adventure;

CREATE INDEX IF NOT EXISTS idx_text_adventure_account_id_updated_at
    ON text_adventure(account_id, updated_at DESC);
