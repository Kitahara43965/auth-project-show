
package com.example.myauthapp.token.refreshtoken.repository.revoke;

import java.util.Optional;
import java.util.Objects;
import java.util.UUID;

import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenFamilyDTO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.web.token.exception.TokenException;
import com.example.myauthapp.token.refreshtoken.error.RefreshTokenFamilyTokenError;
import org.springframework.beans.factory.annotation.Qualifier;

@Repository
public class RefreshTokenFamilyRevokeJdbcRepository {

    private final JdbcTemplate userJdbcTemplate;

    public RefreshTokenFamilyRevokeJdbcRepository(
        @Qualifier("userJdbcTemplate")
        JdbcTemplate userJdbcTemplate
    ) {
        this.userJdbcTemplate = userJdbcTemplate;
    }

    public int revokeRefreshTokenFamilyById(UUID id) {
        Objects.requireNonNull(
            id,
            "id must not be null"
        );

        String sql = """
            UPDATE refresh_token_families
            SET revoked_at = CURRENT_TIMESTAMP
            WHERE id = ?
            AND revoked_at IS NULL
            """;

        return userJdbcTemplate.update(
            sql,
            id
        );
    }

    public int revokeAllRefreshTokenFamiliesByUserId(Long userId) {
        Objects.requireNonNull(
            userId,
            "userId must not be null"
        );

        String sql = """
            UPDATE refresh_token_families
            SET revoked_at = CURRENT_TIMESTAMP
            WHERE user_id = ?
            AND revoked_at IS NULL
            """;

        return userJdbcTemplate.update(sql, userId);
    }


}
