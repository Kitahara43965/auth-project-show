
package com.example.myauthapp.token.refreshtoken.sql;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;

import com.example.myauthapp.token.refreshtoken.dto.RefreshTokenFamilyDTO;

public final class RefreshTokenFamilyDTODiscoverySql {

    public static final String COLUMN_NAMES = """
        id,
        user_id,
        revoked_at,
        created_at
        """;

    public static final RowMapper<RefreshTokenFamilyDTO> ROW_MAPPER =
        (rs, rowNum) -> new RefreshTokenFamilyDTO(
            rs.getObject("id", UUID.class),
            rs.getLong("user_id"),
            rs.getObject(
                "revoked_at",
                LocalDateTime.class
            ),
            rs.getObject(
                "created_at",
                LocalDateTime.class
            )
        );

    private RefreshTokenFamilyDTODiscoverySql() {
    }
}
