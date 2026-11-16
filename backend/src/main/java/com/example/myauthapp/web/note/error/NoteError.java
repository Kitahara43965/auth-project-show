package com.example.myauthapp.web.note.error;

public interface NoteError {
    String getErrorCode();
    String getErrorMessage();

    default String format(Object... args) {
        return String.format(getErrorMessage(), args);
    }
}