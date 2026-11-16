package com.example.myauthapp.audit.log.error;

import com.example.myauthapp.web.audit.error.AuditError;

public enum AuditLogAuditError implements AuditError{

    NO_AUDIT_LOGS_ARE_FOUND(
        "NO_AUDIT_LOGS_ARE_FOUND",
        "Error: No audit logs are found."
    );

    private final String errorCode;
    private final String errorMessage;

    AuditLogAuditError(
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