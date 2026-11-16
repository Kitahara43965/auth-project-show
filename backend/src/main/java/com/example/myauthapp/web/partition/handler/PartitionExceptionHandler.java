package com.example.myauthapp.web.partition.handler;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;

import com.example.myauthapp.web.partition.error.PartitionError;
import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.web.partition.mapper.PartitionErrorMapper;
import com.example.myauthapp.web.partition.dto.PartitionErrorResponse;

public class PartitionExceptionHandler {

    @ExceptionHandler(PartitionException.class)
    public ResponseEntity<PartitionErrorResponse> handlePartitionException(
            PartitionException partitionException
    ) {
        PartitionError partitionError = partitionException.getPartitionError();

        return ResponseEntity
            .status(PartitionErrorMapper.toHttpStatus(partitionError))
            .body(new PartitionErrorResponse(
                partitionError.getErrorCode(),
                partitionError.getErrorMessage()
            ));
    }

}