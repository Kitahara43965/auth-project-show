package com.example.myauthapp.web.note.exception;

import java.util.Objects;

import com.example.myauthapp.web.note.error.NoteError;

public class NoteException extends RuntimeException {

    private final NoteError noteError;

    public NoteException(NoteError noteError, Object... args) {
        super(noteError.format(args));
        this.noteError = Objects.requireNonNull(
            noteError,
            "noteError must not be null"
        );
    }

    public NoteError getNoteError() {
        return noteError;
    }
}