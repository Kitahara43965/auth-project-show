package com.example.myauthapp.web.auth.mapper;

import org.springframework.http.HttpStatus;
import com.example.myauthapp.web.auth.error.AuthError;
import com.example.myauthapp.auth.error.FieldAuthError;

public final class AuthErrorMapper {

    private AuthErrorMapper() {
    }

    public static HttpStatus toHttpStatus(AuthError authError) {

        if (authError instanceof FieldAuthError error) {
            switch (error) {
                case EMAIL_ALREADY_EXISTS:
                    return HttpStatus.CONFLICT;
                case INVALID_CREDENTIALS:
                    return HttpStatus.UNAUTHORIZED;
                case AUTH_NO_USER:
                    return HttpStatus.INTERNAL_SERVER_ERROR;
                case AUTH_NO_EMAIL:
                    return HttpStatus.INTERNAL_SERVER_ERROR;
                case DATA_INTEGRITY_VIOLATION:
                    return HttpStatus.INTERNAL_SERVER_ERROR;
                default:
                    throw new IllegalArgumentException("Unknown FieldAuthError: " + error);
            }
        }

        throw new IllegalArgumentException(
            "Unknown AuthError: " + authError
        );


    }
}