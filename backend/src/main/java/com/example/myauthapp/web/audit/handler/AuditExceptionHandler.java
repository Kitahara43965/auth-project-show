package com.example.myauthapp.web.audit.handler;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;

import com.example.myauthapp.web.audit.error.AuditError;
import com.example.myauthapp.web.audit.exception.AuditException;
import com.example.myauthapp.web.audit.mapper.AuditErrorMapper;
import com.example.myauthapp.web.audit.dto.AuditErrorResponse;

public class AuditExceptionHandler {

    @ExceptionHandler(AuditException.class)
    public ResponseEntity<AuditErrorResponse> handleAuditException(
        AuditException auditException
    ) {
        AuditError auditError = auditException.getAuditError();

        return ResponseEntity
            .status(AuditErrorMapper.toHttpStatus(auditError))
            .body(new AuditErrorResponse(
                auditError.getErrorCode(),
                auditError.getErrorMessage()
            ));
    }

}