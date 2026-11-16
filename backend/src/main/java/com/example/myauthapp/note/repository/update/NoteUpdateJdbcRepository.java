package com.example.myauthapp.note.repository.update;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.note.dto.NoteUpdateDTO;
import com.example.myauthapp.note.error.NoteDTONoteError;
import com.example.myauthapp.web.note.exception.NoteException;

@Repository
public class NoteUpdateJdbcRepository {

    private final JdbcTemplate userJdbcTemplate;

    public NoteUpdateJdbcRepository(
        @Qualifier("userJdbcTemplate")
        JdbcTemplate userJdbcTemplate
    ) {
        this.userJdbcTemplate = userJdbcTemplate;
    }

    public int updateNoteByIdAndUserIdAndNoteUpdateDTO(
        Long id,
        Long userId,
        NoteUpdateDTO noteUpdateDTO
    ) {

        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(
            noteUpdateDTO,
            "noteUpdateDTO must not be null"
        );

        String sql = """
            UPDATE notes
            SET
                title = ?,
                content = ?,
                version = version + 1,
                updated_at = CURRENT_TIMESTAMP
            WHERE
                id = ?
                AND user_id = ?
                AND version = ?
            """;

        int updatedRowCount = userJdbcTemplate.update(
            sql,
            noteUpdateDTO.getTitle(),
            noteUpdateDTO.getContent(),
            id,
            userId,
            noteUpdateDTO.getVersion()
        );

        if (updatedRowCount != 1) {
            throw new NoteException(
                NoteDTONoteError.NO_NOTES_ARE_UPDATED
            );
        }

        return updatedRowCount;
    }
}