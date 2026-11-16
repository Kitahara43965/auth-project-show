
package com.example.myauthapp.token.refreshtoken.dto;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class RefreshTokenFamilyDTO {

    private final UUID id;
    private final Long userId;
    private final LocalDateTime revokedAt;
    private final LocalDateTime createdAt;

    public RefreshTokenFamilyDTO(
        UUID id,
        Long userId,
        LocalDateTime revokedAt,
        LocalDateTime createdAt
    ) {
        this.id = Objects.requireNonNull(
            id,
            "id must not be null"
        );

        this.userId = Objects.requireNonNull(
            userId,
            "userId must not be null"
        );

        this.revokedAt = revokedAt;

        this.createdAt = Objects.requireNonNull(
            createdAt,
            "createdAt must not be null"
        );
    }

    public UUID getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
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
