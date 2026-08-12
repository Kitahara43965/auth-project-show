package com.example.myauthapp.auth.cookie.exception;

import com.example.myauthapp.auth.cookie.error.CookieError;
import com.example.myauthapp.common.exception.AppException;

public class CookieException extends AppException {

    public CookieException(CookieError cookieError) {
        super(cookieError);
    }
}