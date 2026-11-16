package com.example.myauthapp.web.partition.dto;

import java.util.List;
import com.example.myauthapp.web.partition.dto.PartitionValidationDetail;

public class PartitionErrorResponse {

    private final String errorCode;
    private final String errorMessage;
    private final List<PartitionValidationDetail> partitionValidationDetails;

    public PartitionErrorResponse(
        String errorCode,
        String errorMessage
    ) {
        this(errorCode, errorMessage, null);
    }

    public PartitionErrorResponse(
        String errorCode,
        String errorMessage,
        List<PartitionValidationDetail> partitionValidationDetails
    ) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.partitionValidationDetails = partitionValidationDetails;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<PartitionValidationDetail> getPartitionValidationDetails() {
        return partitionValidationDetails;
    }
}