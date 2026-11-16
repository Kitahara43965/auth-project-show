package com.example.myauthapp.token.refreshtoken.repository.update;

import java.sql.Timestamp;
import java.util.Objects;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenCreateDTO;
import com.example.myauthapp.web.token.exception.TokenException;
import com.example.myauthapp.token.refreshtoken.error.RefreshTokenTokenError;

@Repository
public class RefreshTokenUpdateJdbcRepository {

    private final JdbcTemplate userJdbcTemplate;

    public RefreshTokenUpdateJdbcRepository(
        @Qualifier("userJdbcTemplate")
        JdbcTemplate userJdbcTemplate
    ) {
        this.userJdbcTemplate = userJdbcTemplate;
    }

    public int updateRefreshTokenExpiresAtByIdAndExpiresAt(
        Long refreshTokenId,
        LocalDateTime expiresAt
    ) {
        Objects.requireNonNull(
            refreshTokenId,
            "refreshTokenId must not be null"
        );
        Objects.requireNonNull(
            expiresAt,
            "expiresAt must not be null"
        );

        String sql = """
            UPDATE refresh_tokens
            SET expires_at = ?
            WHERE id = ?
            AND revoked_at IS NULL
            """;

        return userJdbcTemplate.update(
            sql,
            Timestamp.valueOf(expiresAt),
            refreshTokenId
        );
    }

    public int updateRefreshTokenCreatedAtByIdAndCreatedAt(
        Long refreshTokenId,
        LocalDateTime createdAt
    ) {
        Objects.requireNonNull(
            refreshTokenId, 
            "refreshTokenId must not be null"
        );
        Objects.requireNonNull(
            createdAt, 
            "createdAt must not be null"
        );

        String sql = """
            UPDATE refresh_tokens
            SET created_at = ?
            WHERE id = ?
            """;

        return userJdbcTemplate.update(
            sql,
            Timestamp.valueOf(createdAt),
            refreshTokenId
        );
    }
}