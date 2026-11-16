package com.example.myauthapp.web.audit.exception;

import java.util.Objects;

import com.example.myauthapp.web.audit.error.AuditError;

public class AuditException extends RuntimeException {

    private final AuditError auditError;

    public AuditException(AuditError auditError, Object... args) {
        super(auditError.format(args));
        this.auditError = Objects.requireNonNull(
            auditError,
            "auditError must not be null"
        );
    }

    public AuditError getAuditError() {
        return auditError;
    }
}