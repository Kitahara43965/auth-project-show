package com.example.myauthapp.web.note.mapper;

import org.springframework.http.HttpStatus;
import com.example.myauthapp.web.note.error.NoteError;
import com.example.myauthapp.note.error.NoteDTONoteError;

public final class NoteErrorMapper {

    private NoteErrorMapper() {
    }

    public static HttpStatus toHttpStatus(NoteError noteError) {

        if (noteError instanceof NoteDTONoteError error) {

            switch (error) {

                case NO_NOTES_ARE_FOUND:
                    return HttpStatus.NOT_FOUND;

                case NO_NOTES_ARE_UPDATED:
                    return HttpStatus.NOT_FOUND;

                case NO_NOTES_ARE_DELETED:
                    return HttpStatus.NOT_FOUND;

                default:
                    throw new IllegalArgumentException(
                        "Unknown NoteDTONoteError: " + error
                    );
            }
        }

        throw new IllegalArgumentException(
            "Unknown NoteError: " + noteError
        );
    }
    
}