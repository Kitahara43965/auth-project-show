package com.example.myauthapp.note.repository.discovery;

import java.util.Objects;
import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.note.dto.NoteDTO;
import com.example.myauthapp.note.sql.NoteDTODiscoverySql;
import com.example.myauthapp.note.error.NoteDTONoteError;
import com.example.myauthapp.web.note.exception.NoteException;

@Repository
public class NoteDiscoveryJdbcRepository {

    private final JdbcTemplate userJdbcTemplate;

    public NoteDiscoveryJdbcRepository(
        @Qualifier("userJdbcTemplate")
        JdbcTemplate userJdbcTemplate
    ) {
        this.userJdbcTemplate = userJdbcTemplate;
    }

    private Optional<NoteDTO> findOptionalNoteDTO(
        String columnName,
        Object value,
        Long userId
    ) {

        String sql = """
            SELECT
                %s
            FROM notes
            WHERE %s = ?
              AND user_id = ?
            LIMIT 1
            """.formatted(
                NoteDTODiscoverySql.COLUMN_NAMES,
                columnName
            );

        return userJdbcTemplate.query(
            sql,
            NoteDTODiscoverySql.ROW_MAPPER,
            value,
            userId
        ).stream().findFirst();
    }

    public NoteDTO findNoteDTOByOptionalNoteDTO(
        Optional<NoteDTO> optionalNoteDTO
    ) {
        Objects.requireNonNull(
            optionalNoteDTO,
            "optionalNoteDTO must not be null"
        );

        return optionalNoteDTO.orElseThrow(
            () -> new NoteException(
                NoteDTONoteError.NO_NOTES_ARE_FOUND
            )
        );
    }

    public Optional<NoteDTO> findOptionalNoteDTOByIdAndUserId(
        Long id,
        Long userId
    ) {

        Objects.requireNonNull(
            id,
            "id must not be null"
        );

        Objects.requireNonNull(
            userId,
            "userId must not be null"
        );

        return findOptionalNoteDTO(
            "id",
            id,
            userId
        );
    }

    public NoteDTO findNoteDTOByIdAndUserId(
        Long id,
        Long userId
    ) {

        Objects.requireNonNull(
            id,
            "id must not be null"
        );

        Objects.requireNonNull(
            userId,
            "userId must not be null"
        );

        Optional<NoteDTO> optionalNoteDTO =
            findOptionalNoteDTOByIdAndUserId(
                id,
                userId
            );

        return findNoteDTOByOptionalNoteDTO(
            optionalNoteDTO
        );
    }

    public List<NoteDTO> findNoteDTOListByUserId(
        Long userId
    ) {

        Objects.requireNonNull(
            userId,
            "userId must not be null"
        );

        String sql = """
            SELECT
                %s
            FROM notes
            WHERE user_id = ?
            ORDER BY updated_at DESC
            """.formatted(
                NoteDTODiscoverySql.COLUMN_NAMES
            );

        return userJdbcTemplate.query(
            sql,
            NoteDTODiscoverySql.ROW_MAPPER,
            userId
        );
    }
}
