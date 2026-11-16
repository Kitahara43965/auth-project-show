package com.example.myauthapp.web.token.handler;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.example.myauthapp.web.token.error.TokenError;
import com.example.myauthapp.web.token.exception.TokenException;
import com.example.myauthapp.web.token.mapper.TokenErrorMapper;
import com.example.myauthapp.web.token.dto.TokenValidationDetail;
import com.example.myauthapp.web.token.dto.TokenErrorResponse;
import com.example.myauthapp.token.refreshtoken.controller.RefreshTokenController;

@RestControllerAdvice(
    assignableTypes = {
        RefreshTokenController.class
    }
)
public class TokenExceptionHandler {

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<TokenErrorResponse> handleTokenException(
            TokenException tokenException
    ) {
        TokenError tokenError = tokenException.getTokenError();

        return ResponseEntity
            .status(TokenErrorMapper.toHttpStatus(tokenError))
            .body(new TokenErrorResponse(
                tokenError.getErrorCode(),
                tokenError.getErrorMessage()
            ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<TokenErrorResponse> handleValidationException(
            MethodArgumentNotValidException methodArgumentNotValidException
    ) {
        List<TokenValidationDetail> listTokenValidationDetails =
            methodArgumentNotValidException.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                    new TokenValidationDetail(
                        error.getField(),
                        error.getDefaultMessage()
                    )
                )
                .toList();

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new TokenErrorResponse(
                "VALIDATION_ERROR",
                "入力内容が不正です",
                listTokenValidationDetails
            ));
    }

}