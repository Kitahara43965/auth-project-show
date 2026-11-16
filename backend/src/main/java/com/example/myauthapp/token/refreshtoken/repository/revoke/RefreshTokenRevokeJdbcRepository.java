
package com.example.myauthapp.token.refreshtoken.repository.revoke;

import java.util.Optional;
import java.util.Objects;
import java.util.UUID;

import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenFamilyDTO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.web.token.exception.TokenException;
import com.example.myauthapp.token.refreshtoken.error.RefreshTokenTokenError;
import org.springframework.beans.factory.annotation.Qualifier;

@Repository
public class RefreshTokenRevokeJdbcRepository {

    private final JdbcTemplate userJdbcTemplate;

    public RefreshTokenRevokeJdbcRepository(
        @Qualifier("userJdbcTemplate")
        JdbcTemplate userJdbcTemplate
    ) {
        this.userJdbcTemplate = userJdbcTemplate;
    }

    public int revokeRefreshTokensByRefreshTokenFamilyId(UUID refreshTokenFamilyId) {
        Objects.requireNonNull(
            refreshTokenFamilyId,
            "refreshTokenFamilyId must not be null"
        );

        String sql = """
            UPDATE refresh_tokens
            SET revoked_at = CURRENT_TIMESTAMP
            WHERE refresh_token_family_id = ?
            AND revoked_at IS NULL
            """;

        int updatedRowCount = userJdbcTemplate.update(
            sql,
            refreshTokenFamilyId
        );

        return updatedRowCount;
    }

    public int revokeRefreshTokenById(Long id) {
        Objects.requireNonNull(
            id,
            "id must not be null"
        );

        String sql = """
            UPDATE refresh_tokens
            SET revoked_at = CURRENT_TIMESTAMP
            WHERE id = ?
            AND revoked_at IS NULL
            """;

        return userJdbcTemplate.update(
            sql,
            id
        );
    }


}
