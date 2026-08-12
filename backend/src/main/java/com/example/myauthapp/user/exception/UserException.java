package com.example.myauthapp.user.exception;

import com.example.myauthapp.user.error.UserError;
import com.example.myauthapp.common.exception.AppException;

public class UserException extends AppException {
    public UserException(UserError userError) {
        super(userError);
    }
}