package com.example.myauthapp.partition.service.command;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.service.recovery.PartitionRecoveryService;

@Service
public class PartitionRecoveryCommandService {

    private final PartitionRecoveryService partitionRecoveryService;

    public PartitionRecoveryCommandService(
        PartitionRecoveryService partitionRecoveryService
    ) {
        this.partitionRecoveryService = partitionRecoveryService;
    }

    public void partitionRecoveryCommandExecute(
        PartitionContextGroup partitionContextGroup
    ) {
        Objects.requireNonNull(
            partitionContextGroup,
            "partitionContextGroup must not be null"
        );

        partitionRecoveryService.recoverAllPartitions(partitionContextGroup);
    }
}