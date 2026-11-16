package com.example.myauthapp.web.auth.handler;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.example.myauthapp.web.auth.error.AuthError;
import com.example.myauthapp.web.auth.exception.AuthException;
import com.example.myauthapp.web.auth.mapper.AuthErrorMapper;
import com.example.myauthapp.web.auth.dto.AuthValidationDetail;
import com.example.myauthapp.web.auth.dto.AuthErrorResponse;
import com.example.myauthapp.auth.controller.AuthController;
import com.example.myauthapp.auth.controller.LogoutController;

@RestControllerAdvice(
    assignableTypes = {
        AuthController.class,
        LogoutController.class
    }
)
public class AuthExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<AuthErrorResponse> handleAuthException(
            AuthException authException
    ) {
        AuthError authError = authException.getAuthError();

        return ResponseEntity
            .status(AuthErrorMapper.toHttpStatus(authError))
            .body(new AuthErrorResponse(
                authError.getErrorCode(),
                authError.getErrorMessage()
            ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AuthErrorResponse> handleValidationException(
            MethodArgumentNotValidException methodArgumentNotValidException
    ) {
        List<AuthValidationDetail> listAuthValidationDetails =
            methodArgumentNotValidException.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                    new AuthValidationDetail(
                        error.getField(),
                        error.getDefaultMessage()
                    )
                )
                .toList();

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new AuthErrorResponse(
                "VALIDATION_ERROR",
                "入力内容が不正です",
                listAuthValidationDetails
            ));
    }
}