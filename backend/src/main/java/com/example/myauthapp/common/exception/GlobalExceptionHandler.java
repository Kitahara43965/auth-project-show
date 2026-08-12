package com.example.myauthapp.common.exception;

import com.example.myauthapp.common.dto.ErrorResponse;
import com.example.myauthapp.common.error.AppError;
import com.example.myauthapp.common.exception.AppException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.HttpStatus;
import java.util.List;
import com.example.myauthapp.common.dto.ValidationDetail;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException e) {

        AppError appError = e.getAppError();

        return ResponseEntity
                .status(appError.getHttpStatus())
                .body(new ErrorResponse(
                        appError.getErrorCode(),
                        appError.getErrorMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e
    ) {
        List<ValidationDetail> listValidationDetails =
                e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                    new ValidationDetail(
                        error.getField(),
                        error.getDefaultMessage()
                    )
                )
                .toList();


        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "VALIDATION_ERROR",
                        "入力内容が不正です",
                        listValidationDetails
                ));
    }
}