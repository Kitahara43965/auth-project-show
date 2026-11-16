package com.example.myauthapp.token.refreshtoken.repository.insert;

import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenFamilyCreateDTO;
import com.example.myauthapp.web.token.exception.TokenException;
import com.example.myauthapp.token.refreshtoken.error.RefreshTokenFamilyTokenError;
import org.springframework.beans.factory.annotation.Qualifier;

@Repository
public class RefreshTokenFamilyInsertJdbcRepository {

    private final JdbcTemplate userJdbcTemplate;

    public RefreshTokenFamilyInsertJdbcRepository(
        @Qualifier("userJdbcTemplate")
        JdbcTemplate userJdbcTemplate
    ) {
        this.userJdbcTemplate = userJdbcTemplate;
    }

    public int insertRefreshTokenFamilyByRefreshTokenFamilyCreateDTO(
        RefreshTokenFamilyCreateDTO refreshTokenFamilyCreateDTO
    ) {
        Objects.requireNonNull(
            refreshTokenFamilyCreateDTO,
            "refreshTokenFamilyCreateDTO must not be null"
        );

        String sql = """
            INSERT INTO refresh_token_families (
                id,
                user_id,
                revoked_at,
                created_at
            )
            VALUES (?, ?, NULL, CURRENT_TIMESTAMP)
            """;

        int insertedRowCount = userJdbcTemplate.update(
            sql,
            refreshTokenFamilyCreateDTO.getId(),
            refreshTokenFamilyCreateDTO.getUserId()
        );

        if(insertedRowCount != 1){
            throw new TokenException(
                RefreshTokenFamilyTokenError.FAILED_TO_SAVE_REFRESH_TOKEN_FAMILY,
                insertedRowCount
            );
        }//insertedRowCount

        return insertedRowCount;
    }
}