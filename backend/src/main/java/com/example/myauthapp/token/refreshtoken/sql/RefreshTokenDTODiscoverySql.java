
package com.example.myauthapp.token.refreshtoken.sql;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;

import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenDTO;

public final class RefreshTokenDTODiscoverySql {

    public static final String COLUMN_NAMES = """
        id,
        token_hash,
        expires_at,
        refresh_token_family_id,
        revoked_at,
        created_at
        """;

    public static final RowMapper<RefreshTokenDTO> ROW_MAPPER =
        (rs, rowNum) -> new RefreshTokenDTO(
            rs.getLong("id"),
            rs.getString("token_hash"),
            rs.getObject(
                "expires_at",
                LocalDateTime.class
            ),
            rs.getObject(
                "refresh_token_family_id",
                UUID.class
            ),
            rs.getObject(
                "revoked_at",
                LocalDateTime.class
            ),
            rs.getObject(
                "created_at",
                LocalDateTime.class
            )
        );

    private RefreshTokenDTODiscoverySql() {
    }
}
