package com.example.myauthapp.web.audit.dto;

import java.util.List;
import com.example.myauthapp.web.audit.dto.AuditValidationDetail;

public class AuditErrorResponse {

    private final String errorCode;
    private final String errorMessage;
    private final List<AuditValidationDetail> auditValidationDetails;

    public AuditErrorResponse(
        String errorCode,
        String errorMessage
    ) {
        this(errorCode, errorMessage, null);
    }

    public AuditErrorResponse(
        String errorCode,
        String errorMessage,
        List<AuditValidationDetail> auditValidationDetails
    ) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.auditValidationDetails = auditValidationDetails;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<AuditValidationDetail> getAuditValidationDetails() {
        return auditValidationDetails;
    }
}