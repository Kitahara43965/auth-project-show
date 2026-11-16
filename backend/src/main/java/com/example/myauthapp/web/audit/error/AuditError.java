package com.example.myauthapp.web.audit.error;

public interface AuditError {
    String getErrorCode();

    String getErrorMessage();

    default String format(Object... args) {
        return String.format(getErrorMessage(), args);
    }
}