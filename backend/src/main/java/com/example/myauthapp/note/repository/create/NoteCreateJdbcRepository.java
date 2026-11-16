
package com.example.myauthapp.note.repository.create;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.note.dto.NoteCreateDTO;
import com.example.myauthapp.note.dto.NoteDTO;
import com.example.myauthapp.note.sql.NoteDTODiscoverySql;

@Repository
public class NoteCreateJdbcRepository {

    private final JdbcTemplate userJdbcTemplate;

    public NoteCreateJdbcRepository(
        @Qualifier("userJdbcTemplate")
        JdbcTemplate userJdbcTemplate
    ) {
        this.userJdbcTemplate = userJdbcTemplate;
    }

    public NoteDTO createNoteByNoteCreateDTOAndUserId(
        NoteCreateDTO noteCreateDTO,
        Long userId
    ) {

        Objects.requireNonNull(
            noteCreateDTO,
            "noteCreateDTO must not be null"
        );

        Objects.requireNonNull(
            userId,
            "userId must not be null"
        );

        String sql = """
            INSERT INTO notes (
                user_id,
                title,
                content
            )
            VALUES (?, ?, ?)
            RETURNING
                %s
            """.formatted(NoteDTODiscoverySql.COLUMN_NAMES);

        return userJdbcTemplate.queryForObject(
            sql,
            NoteDTODiscoverySql.ROW_MAPPER,
            userId,
            noteCreateDTO.getTitle(),
            noteCreateDTO.getContent()
        );
    }
}
