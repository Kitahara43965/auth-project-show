package com.example.myauthapp.web.partition.exception;

import java.util.Objects;

import com.example.myauthapp.web.partition.error.PartitionError;

public class PartitionException extends RuntimeException {

    private final PartitionError partitionError;

    public PartitionException(PartitionError partitionError, Object... args) {
        super(partitionError.format(args));
        this.partitionError = Objects.requireNonNull(
            partitionError,
            "partitionError must not be null"
        );
    }

    public PartitionError getPartitionError() {
        return partitionError;
    }
}