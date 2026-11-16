package com.example.myauthapp.web.token.dto;

import java.util.List;
import com.example.myauthapp.web.token.dto.TokenValidationDetail;

public class TokenErrorResponse {

    private final String errorCode;
    private final String errorMessage;
    private final List<TokenValidationDetail> tokenValidationDetails;

    public TokenErrorResponse(
        String errorCode,
        String errorMessage
    ) {
        this(errorCode, errorMessage, null);
    }

    public TokenErrorResponse(
        String errorCode,
        String errorMessage,
        List<TokenValidationDetail> tokenValidationDetails
    ) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.tokenValidationDetails = tokenValidationDetails;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<TokenValidationDetail> getTokenValidationDetails() {
        return tokenValidationDetails;
    }
}