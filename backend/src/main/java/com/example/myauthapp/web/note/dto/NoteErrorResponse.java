package com.example.myauthapp.web.note.dto;

import java.util.List;
import com.example.myauthapp.web.note.dto.NoteValidationDetail;

public class NoteErrorResponse {

    private final String errorCode;
    private final String errorMessage;
    private final List<NoteValidationDetail> noteValidationDetails;

    public NoteErrorResponse(
        String errorCode,
        String errorMessage
    ) {
        this(errorCode, errorMessage, null);
    }

    public NoteErrorResponse(
        String errorCode,
        String errorMessage,
        List<NoteValidationDetail> noteValidationDetails
    ) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.noteValidationDetails = noteValidationDetails;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<NoteValidationDetail> getNoteValidationDetails() {
        return noteValidationDetails;
    }
}