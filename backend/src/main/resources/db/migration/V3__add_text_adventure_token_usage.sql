ALTER TABLE text_adventure
    ADD COLUMN IF NOT EXISTS input_tokens_used BIGINT NOT NULL DEFAULT 0;

ALTER TABLE text_adventure
    ADD COLUMN IF NOT EXISTS output_tokens_used BIGINT NOT NULL DEFAULT 0;
