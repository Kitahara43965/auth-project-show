package com.example.myauthapp.web.note.handler;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.example.myauthapp.web.note.error.NoteError;
import com.example.myauthapp.web.note.exception.NoteException;
import com.example.myauthapp.web.note.mapper.NoteErrorMapper;
import com.example.myauthapp.web.note.dto.NoteValidationDetail;
import com.example.myauthapp.web.note.dto.NoteErrorResponse;
import com.example.myauthapp.note.controller.NoteController;

@RestControllerAdvice(
    assignableTypes = {
        NoteController.class
    }
)
public class NoteExceptionHandler {

    @ExceptionHandler(NoteException.class)
    public ResponseEntity<NoteErrorResponse> handleNoteException(
            NoteException noteException
    ) {
        NoteError noteError = noteException.getNoteError();

        return ResponseEntity
            .status(NoteErrorMapper.toHttpStatus(noteError))
            .body(new NoteErrorResponse(
                noteError.getErrorCode(),
                noteError.getErrorMessage()
            ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<NoteErrorResponse> handleValidationException(
            MethodArgumentNotValidException methodArgumentNotValidException
    ) {
        List<NoteValidationDetail> listNoteValidationDetails =
            methodArgumentNotValidException.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                    new NoteValidationDetail(
                        error.getField(),
                        error.getDefaultMessage()
                    )
                )
                .toList();

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new NoteErrorResponse(
                "VALIDATION_ERROR",
                "入力内容が不正です",
                listNoteValidationDetails
            ));
    }

}