package com.example.myauthapp.auth.exception;

import com.example.myauthapp.auth.error.AuthError;
import com.example.myauthapp.common.exception.AppException;

public class AuthException extends AppException {

    public AuthException(AuthError authError) {
        super(authError);
    }
}