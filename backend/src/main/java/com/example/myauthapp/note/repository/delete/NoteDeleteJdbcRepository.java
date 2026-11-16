package com.example.myauthapp.note.repository.delete;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.note.error.NoteDTONoteError;
import com.example.myauthapp.web.note.exception.NoteException;

@Repository
public class NoteDeleteJdbcRepository {

    private final JdbcTemplate userJdbcTemplate;

    public NoteDeleteJdbcRepository(
        @Qualifier("userJdbcTemplate")
        JdbcTemplate userJdbcTemplate
    ) {
        this.userJdbcTemplate = userJdbcTemplate;
    }

    public int deleteNoteByIdAndUserId(
        Long id,
        Long userId
    ) {

        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(userId, "userId must not be null");

        String sql = """
            DELETE FROM notes
            WHERE
                id = ?
                AND user_id = ?
            """;

        int deletedRowCount = userJdbcTemplate.update(
            sql,
            id,
            userId
        );

        if (deletedRowCount != 1) {
            throw new NoteException(
                NoteDTONoteError.NO_NOTES_ARE_DELETED
            );
        }

        return deletedRowCount;
    }

    public void deleteAll() {
        userJdbcTemplate.update(
            "DELETE FROM notes"
        );
    }
}