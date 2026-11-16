package com.example.myauthapp.web.audit.mapper;

import org.springframework.http.HttpStatus;

import com.example.myauthapp.web.audit.error.AuditError;
import com.example.myauthapp.audit.log.error.AuditLogAuditError;

public final class AuditErrorMapper {

    private AuditErrorMapper() {
    }

    public static HttpStatus toHttpStatus(AuditError auditError) {

        if (auditError instanceof AuditLogAuditError error) {
            switch (error) {

                case NO_AUDIT_LOGS_ARE_FOUND:
                    return HttpStatus.INTERNAL_SERVER_ERROR;

                default:
                    throw new IllegalArgumentException(
                        "Unknown AuditLogAuditError: " + error
                    );
            }
        }

        throw new IllegalArgumentException(
            "Unknown AuditError: " + auditError
        );
    }
    
}