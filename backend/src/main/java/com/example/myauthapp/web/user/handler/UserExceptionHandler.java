package com.example.myauthapp.web.user.handler;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.example.myauthapp.web.user.error.UserError;
import com.example.myauthapp.web.user.exception.UserException;
import com.example.myauthapp.web.user.mapper.UserErrorMapper;
import com.example.myauthapp.web.user.dto.UserValidationDetail;
import com.example.myauthapp.web.user.dto.UserErrorResponse;
import com.example.myauthapp.user.controller.UserController;

@RestControllerAdvice(
    assignableTypes = {
        UserController.class
    }
)
public class UserExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<UserErrorResponse> handleUserException(
            UserException userException
    ) {
        UserError userError = userException.getUserError();

        return ResponseEntity
            .status(UserErrorMapper.toHttpStatus(userError))
            .body(new UserErrorResponse(
                userError.getErrorCode(),
                userError.getErrorMessage()
            ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<UserErrorResponse> handleValidationException(
            MethodArgumentNotValidException methodArgumentNotValidException
    ) {
        List<UserValidationDetail> listUserValidationDetails =
            methodArgumentNotValidException.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                    new UserValidationDetail(
                        error.getField(),
                        error.getDefaultMessage()
                    )
                )
                .toList();

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new UserErrorResponse(
                "VALIDATION_ERROR",
                "入力内容が不正です",
                listUserValidationDetails
            ));
    }

}