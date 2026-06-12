CREATE TABLE IF NOT EXISTS account_password_reset (
    account_id VARCHAR(255) PRIMARY KEY,
    code VARCHAR(6) NOT NULL,
    expires_at BIGINT NOT NULL,
    CONSTRAINT fk_account_password_reset_account FOREIGN KEY (account_id) REFERENCES account_user(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_account_password_reset_code ON account_password_reset(code);

GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE account_password_reset TO "${app_role}";
