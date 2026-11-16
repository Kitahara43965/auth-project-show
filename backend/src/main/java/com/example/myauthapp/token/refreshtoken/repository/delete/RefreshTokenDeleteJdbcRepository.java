
package com.example.myauthapp.token.refreshtoken.repository.delete;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshTokenDeleteJdbcRepository {

    private final JdbcTemplate userJdbcTemplate;

    public RefreshTokenDeleteJdbcRepository(
        @Qualifier("userJdbcTemplate")
        JdbcTemplate userJdbcTemplate
    ) {
        this.userJdbcTemplate = userJdbcTemplate;
    }

    public void deleteAll() {
        userJdbcTemplate.update(
            "DELETE FROM refresh_tokens"
        );
    }

    public int deleteRefreshTokensByExpiresAtBefore(
        LocalDateTime expiresAt
    ) {
        Objects.requireNonNull(
            expiresAt,
            "expiresAt must not be null"
        );

        String sql = """
            DELETE FROM refresh_tokens
            WHERE expires_at < ?
            """;

        return userJdbcTemplate.update(
            sql,
            expiresAt
        );
    }

    public int deleteRefreshTokensByCreatedAtBefore(
        LocalDateTime createdAt
    ) {
        Objects.requireNonNull(
            createdAt,
            "createdAt must not be null"
        );

        String sql = """
            DELETE FROM refresh_tokens
            WHERE created_at < ?
            """;

        return userJdbcTemplate.update(
            sql,
            createdAt
        );
    }
}
