CREATE TABLE IF NOT EXISTS account_pending_email_change (
    account_id VARCHAR(255) PRIMARY KEY,
    email VARCHAR(320) NOT NULL UNIQUE,
    current_email_code VARCHAR(6) NOT NULL,
    current_email_code_expires_at BIGINT NOT NULL,
    new_email_code VARCHAR(6) NOT NULL,
    new_email_code_expires_at BIGINT NOT NULL,
    CONSTRAINT fk_account_pending_email_change_account FOREIGN KEY (account_id) REFERENCES account_user(id) ON DELETE CASCADE
);

GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE account_pending_email_change TO "${app_role}";
