package com.example.myauthapp.user.sql;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.jdbc.core.RowMapper;

import com.example.myauthapp.user.dto.UserDTO;

public final class UserDTODiscoverySql {

    public static final String COLUMN_NAMES = """
        id,
        email,
        password,
        name,
        email_verified_at,
        created_at
        """;

    public static final RowMapper<UserDTO> ROW_MAPPER =
        (rs, rowNum) -> new UserDTO(
            rs.getLong("id"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("name"),
            rs.getTimestamp("email_verified_at") != null
                ? rs.getTimestamp("email_verified_at")
                    .toLocalDateTime()
                : null,
            rs.getTimestamp("created_at")
                .toLocalDateTime()
        );

    private UserDTODiscoverySql() {
    }
}