package com.example.myauthapp.token.refreshtoken.dto;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import com.example.myauthapp.web.token.exception.TokenException;
import com.example.myauthapp.token.tokenhash.error.TokenHashTokenError;

public class RefreshTokenDTO {

    private final Long id;
    private final String tokenHash;
    private final LocalDateTime expiresAt;
    private final UUID refreshTokenFamilyId;
    private final LocalDateTime revokedAt;
    private final LocalDateTime createdAt;

    public RefreshTokenDTO(
        Long id,
        String tokenHash,
        LocalDateTime expiresAt,
        UUID refreshTokenFamilyId,
        LocalDateTime revokedAt,
        LocalDateTime createdAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");

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

        this.revokedAt = revokedAt;

        this.createdAt = Objects.requireNonNull(
            createdAt,
            "createdAt must not be null"
        );

    }

    public Long getId() {
        return id;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired() {
        return !expiresAt.isAfter(LocalDateTime.now());
    }

    public UUID getRefreshTokenFamilyId() {
        return refreshTokenFamilyId;
    }

    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }

    public boolean isRevoked() {
        return revokedAt == null ? false : true;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}