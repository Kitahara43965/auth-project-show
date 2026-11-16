package com.example.myauthapp.web.partition.error;

public interface PartitionError {
    String getErrorCode();

    String getErrorMessage();

    default String format(Object... args) {
        return String.format(getErrorMessage(), args);
    }
}