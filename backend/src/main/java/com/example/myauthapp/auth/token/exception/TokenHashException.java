package com.example.myauthapp.auth.token.exception;

import com.example.myauthapp.auth.token.error.TokenHashError;
import com.example.myauthapp.common.exception.AppException;

public class TokenHashException extends AppException {

    public TokenHashException(TokenHashError tokenHashError) {
        super(tokenHashError);
    }
}