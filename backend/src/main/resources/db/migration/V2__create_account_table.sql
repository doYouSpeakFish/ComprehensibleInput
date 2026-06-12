
CREATE TABLE IF NOT EXISTS account_user (
    id VARCHAR(255) PRIMARY KEY,
    email VARCHAR(320) NOT NULL UNIQUE,
    password_hash VARCHAR(1024) NOT NULL,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS account_session (
    id VARCHAR(255) PRIMARY KEY,
    account_id VARCHAR(255) NOT NULL,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    created_at BIGINT NOT NULL,
    CONSTRAINT fk_account_session_account FOREIGN KEY (account_id) REFERENCES account_user(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_account_user_email ON account_user(email);
CREATE INDEX IF NOT EXISTS idx_account_session_account_id ON account_session(account_id);
CREATE INDEX IF NOT EXISTS idx_account_session_token_hash ON account_session(token_hash);

GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE account_user TO "${app_role}";
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE account_session TO "${app_role}";
