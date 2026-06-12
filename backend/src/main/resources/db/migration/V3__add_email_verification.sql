ALTER TABLE account_user
    ADD COLUMN IF NOT EXISTS email_verified BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE IF NOT EXISTS account_email_verification (
    account_id VARCHAR(255) PRIMARY KEY,
    code VARCHAR(6) NOT NULL,
    expires_at BIGINT NOT NULL,
    CONSTRAINT fk_account_email_verification_account FOREIGN KEY (account_id) REFERENCES account_user(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_account_email_verification_code ON account_email_verification(code);

GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE account_email_verification TO "${app_role}";
