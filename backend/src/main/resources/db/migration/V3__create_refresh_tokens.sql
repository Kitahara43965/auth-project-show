CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,

    token_hash VARCHAR(64) NOT NULL UNIQUE,

    expires_at TIMESTAMP NOT NULL,

    refresh_token_family_id UUID NOT NULL,

    revoked BOOLEAN NOT NULL DEFAULT FALSE,

    revoked_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_refresh_token_family
        FOREIGN KEY (refresh_token_family_id)
        REFERENCES refresh_token_families(id)
);