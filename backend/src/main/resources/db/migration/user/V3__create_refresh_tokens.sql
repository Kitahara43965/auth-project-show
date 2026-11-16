CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,

    token_hash VARCHAR(64) NOT NULL,

    expires_at TIMESTAMP NOT NULL,

    refresh_token_family_id UUID NOT NULL,

    revoked_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL,

    CONSTRAINT uk_refresh_tokens_token_hash
        UNIQUE (token_hash),

    CONSTRAINT fk_refresh_token_family
        FOREIGN KEY (refresh_token_family_id)
        REFERENCES refresh_token_families(id)
);