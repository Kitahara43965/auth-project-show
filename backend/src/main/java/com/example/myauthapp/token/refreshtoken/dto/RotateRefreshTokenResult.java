package com.example.myauthapp.token.refreshtoken.dto;

import java.util.UUID;
import java.time.LocalDateTime;

public class RotateRefreshTokenResult {

    private final UUID refreshTokenFamilyId;
    private final Long userId;
    private final LocalDateTime expiresAt;
    private final boolean revoked;
    private final String stringRefreshToken;

    public RotateRefreshTokenResult(
        UUID refreshTokenFamilyId,
        Long userId,
        LocalDateTime expiresAt,
        boolean revoked,
        String stringRefreshToken
    ) {
        this.refreshTokenFamilyId = refreshTokenFamilyId;
        this.userId = userId;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.stringRefreshToken = stringRefreshToken;
    }

    public UUID getRefreshTokenFamilyId() {
        return refreshTokenFamilyId;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired() {
        return !expiresAt.isAfter(LocalDateTime.now());
    }

    public boolean isRevoked() {
        return revoked;
    }

    public String getStringRefreshToken() {
        return stringRefreshToken;
    }

}