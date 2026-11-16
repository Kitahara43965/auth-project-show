package com.example.myauthapp.token.refreshtoken.repository.insert;

import java.sql.Timestamp;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenCreateDTO;
import com.example.myauthapp.web.token.exception.TokenException;
import com.example.myauthapp.token.refreshtoken.error.RefreshTokenTokenError;

@Repository
public class RefreshTokenInsertJdbcRepository {

    private final JdbcTemplate userJdbcTemplate;

    public RefreshTokenInsertJdbcRepository(
        @Qualifier("userJdbcTemplate")
        JdbcTemplate userJdbcTemplate
    ) {
        this.userJdbcTemplate = userJdbcTemplate;
    }

    public int insertRefreshTokenDTOByRefreshTokenCreateDTO(
        RefreshTokenCreateDTO refreshTokenCreateDTO
    ) {
        Objects.requireNonNull(
            refreshTokenCreateDTO,
            "refreshTokenCreateDTO must not be null"
        );

        String sql = """
            INSERT INTO refresh_tokens (
                token_hash,
                expires_at,
                refresh_token_family_id,
                revoked_at,
                created_at
            )
            VALUES (?, ?, ?, NULL, CURRENT_TIMESTAMP)
            """;

        int insertedRowCount = userJdbcTemplate.update(
            sql,
            refreshTokenCreateDTO.getTokenHash(),
            Timestamp.valueOf(refreshTokenCreateDTO.getExpiresAt()),
            refreshTokenCreateDTO.getRefreshTokenFamilyId()
        );

        if (insertedRowCount != 1) {
            throw new TokenException(
                RefreshTokenTokenError.FAILED_TO_SAVE_REFRESH_TOKEN,
                insertedRowCount
            );
        }

        return insertedRowCount;
    }
}