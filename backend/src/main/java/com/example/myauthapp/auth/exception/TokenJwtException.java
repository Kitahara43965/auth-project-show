package com.example.myauthapp.auth.exception;

import com.example.myauthapp.auth.error.TokenJwtError;
import com.example.myauthapp.common.exception.AppException;

public class TokenJwtException extends AppException {

    public TokenJwtException(TokenJwtError tokenJwtError) {
        super(tokenJwtError);
    }
}