
package com.example.myauthapp.token.refreshtoken.dto;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class RefreshTokenFamilyCreateDTO {

    private final UUID id;
    private final Long userId;

    public RefreshTokenFamilyCreateDTO(
        UUID id,
        Long userId
    ) {
        this.id = Objects.requireNonNull(
            id,
            "id must not be null"
        );

        this.userId = Objects.requireNonNull(
            userId,
            "userId must not be null"
        );
    }

    public UUID getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

}
