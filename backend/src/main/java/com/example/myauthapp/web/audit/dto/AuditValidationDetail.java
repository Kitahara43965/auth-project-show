package com.example.myauthapp.web.audit.dto;

public class AuditValidationDetail {

    private final String field;
    private final String message;

    public AuditValidationDetail(
        String field, 
        String message
    ) {
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