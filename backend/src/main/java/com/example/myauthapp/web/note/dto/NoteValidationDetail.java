package com.example.myauthapp.web.note.dto;

public class NoteValidationDetail {

    private final String field;
    private final String message;

    public NoteValidationDetail(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}