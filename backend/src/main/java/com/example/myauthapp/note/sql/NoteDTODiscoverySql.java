package com.example.myauthapp.note.sql;

import java.time.LocalDateTime;

import org.springframework.jdbc.core.RowMapper;

import com.example.myauthapp.note.dto.NoteDTO;

public final class NoteDTODiscoverySql {

    public static final String COLUMN_NAMES = """
        id,
        user_id,
        title,
        content,
        version,
        created_at,
        updated_at
        """;

    public static final RowMapper<NoteDTO> ROW_MAPPER =
        (rs, rowNum) -> new NoteDTO(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getString("title"),
            rs.getString("content"),
            rs.getLong("version"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getObject("updated_at", LocalDateTime.class)
        );

    private NoteDTODiscoverySql() {
    }
}