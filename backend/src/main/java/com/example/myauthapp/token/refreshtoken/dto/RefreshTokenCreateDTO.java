
package com.example.myauthapp.token.refreshtoken.dto;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import com.example.myauthapp.web.token.exception.TokenException;
import com.example.myauthapp.token.tokenhash.error.TokenHashTokenError;

public class RefreshTokenCreateDTO {

    private final String tokenHash;
    private final LocalDateTime expiresAt;
    private final UUID refreshTokenFamilyId;
    private final boolean revoked;

    public RefreshTokenCreateDTO(
        String tokenHash,
        LocalDateTime expiresAt,
        UUID refreshTokenFamilyId
    ) {

        this.tokenHash = Objects.requireNonNull(
            tokenHash,
            "tokenHash must not be null"
        );

        if (!tokenHash.matches("[0-9a-fA-F]{64}")) {
            throw new TokenException(
                TokenHashTokenError.TOKEN_HASH_MUST_BE_A_64_CHARACTER_HEXADECIMAL_STRING
            );
        }

        this.expiresAt = Objects.requireNonNull(
            expiresAt,
            "expiresAt must not be null"
        );

        this.refreshTokenFamilyId = Objects.requireNonNull(
            refreshTokenFamilyId,
            "refreshTokenFamilyId must not be null"
        );

        this.revoked = false;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public UUID getRefreshTokenFamilyId() {
        return refreshTokenFamilyId;
    }

    public boolean isRevoked() {
        return revoked;
    }
}
