package com.example.myauthapp.partition.service.command;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.service.recoveryfacade.PartitionRecoveryFacadeJdbcService;
import com.example.myauthapp.partition.service.recoveryretry.PartitionRecoveryRetryService;

@Service
public class PartitionRecoveryRetryCommandService {

    private final PartitionRecoveryFacadeJdbcService
        partitionRecoveryFacadeJdbcService;
    private final PartitionRecoveryRetryService
        partitionRecoveryRetryService;

    public PartitionRecoveryRetryCommandService(
        PartitionRecoveryFacadeJdbcService partitionRecoveryFacadeJdbcService,
        PartitionRecoveryRetryService partitionRecoveryRetryService
    ) {
        this.partitionRecoveryFacadeJdbcService =
            partitionRecoveryFacadeJdbcService;
        this.partitionRecoveryRetryService =
            partitionRecoveryRetryService;
    }

    public void partitionRecoveryRetryCommandExecute(
        PartitionContextGroup partitionContextGroup,
        PartitionRangeDTO partitionRangeDTO,
        UUID publicPartitionOperationId
    ) {
        Objects.requireNonNull(
            partitionContextGroup,
            "partitionContextGroup must not be null"
        );

        Objects.requireNonNull(
            partitionRangeDTO,
            "partitionRangeDTO must not be null"
        );

        Objects.requireNonNull(
            publicPartitionOperationId,
            "publicPartitionOperationId must not be null"
        );

        partitionRecoveryFacadeJdbcService.executeRecoveryAndRecoveryRetry(
            partitionContextGroup,
            partitionRangeDTO,
            publicPartitionOperationId
        );
    }

    public void partitionRecoveryRetryAllFailedCommandExecute(
        PartitionContextGroup partitionContextGroup
    ) {
        Objects.requireNonNull(
            partitionContextGroup,
            "partitionContextGroup must not be null"
        );

        partitionRecoveryRetryService.retryAllFailedPartitionOperations(
            partitionContextGroup
        );
    }
}