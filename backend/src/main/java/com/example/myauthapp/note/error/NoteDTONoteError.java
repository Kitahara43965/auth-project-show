package com.example.myauthapp.note.error;

import com.example.myauthapp.web.note.error.NoteError;

public enum NoteDTONoteError implements NoteError{

    NO_NOTES_ARE_FOUND(
        "NO_NOTES_ARE_FOUND",
        "Error: No notes are found."
    ),
    
    NO_NOTES_ARE_UPDATED(
        "NO_NOTES_ARE_UPDATED",
        "Error: No notes are updated."
    ),
    
    NO_NOTES_ARE_DELETED(
        "NO_NOTES_ARE_DELETED",
        "Error: No notes are deleted."
    );

    private final String errorCode;
    private final String errorMessage;

    NoteDTONoteError(
        String errorCode,
        String errorMessage
    ) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}