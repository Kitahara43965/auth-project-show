package com.example.myauthapp.web.user.dto;

import java.util.List;
import com.example.myauthapp.web.user.dto.UserValidationDetail;

public class UserErrorResponse {

    private final String errorCode;
    private final String errorMessage;
    private final List<UserValidationDetail> userValidationDetails;

    public UserErrorResponse(
        String errorCode,
        String errorMessage
    ) {
        this(errorCode, errorMessage, null);
    }

    public UserErrorResponse(
        String errorCode,
        String errorMessage,
        List<UserValidationDetail> userValidationDetails
    ) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.userValidationDetails = userValidationDetails;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<UserValidationDetail> getUserValidationDetails() {
        return userValidationDetails;
    }
}