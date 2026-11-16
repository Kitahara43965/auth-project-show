package com.example.myauthapp.web.partition.dto;

public class PartitionValidationDetail {

    private final String field;
    private final String message;

    public PartitionValidationDetail(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}