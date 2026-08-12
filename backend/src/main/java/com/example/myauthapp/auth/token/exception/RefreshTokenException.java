package com.example.myauthapp.auth.token.exception;

import com.example.myauthapp.auth.token.error.RefreshTokenError;
import com.example.myauthapp.common.exception.AppException;

public class RefreshTokenException extends AppException {

    public RefreshTokenException(RefreshTokenError refreshTokenError) {
        super(refreshTokenError);
    }
}