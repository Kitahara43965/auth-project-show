package com.example.myauthapp.partition.service.job;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.service.cleanup.PartitionCleanupService;
import com.example.myauthapp.partition.service.recovery.PartitionRecoveryService;

@Component
public class PartitionJobService {

    private final PartitionRecoveryService partitionRecoveryService;
    private final PartitionCleanupService partitionCleanupService;

    public PartitionJobService(
        PartitionRecoveryService partitionRecoveryService,
        PartitionCleanupService partitionCleanupService
    ) {
        this.partitionRecoveryService = partitionRecoveryService;
        this.partitionCleanupService = partitionCleanupService;
    }

    public void partitionJobExecute(
        PartitionContextGroup partitionContextGroup,
        LocalDateTime localDateTimeCleanupBefore
    ) {
        Objects.requireNonNull(
            partitionContextGroup,
            "partitionContextGroup must not be null"
        );

        Objects.requireNonNull(
            localDateTimeCleanupBefore,
            "localDateTimeCleanupBefore must not be null"
        );

        partitionRecoveryService.recoverAllPartitions(
            partitionContextGroup
        );

        partitionCleanupService.cleanupPartitionBefore(
            partitionContextGroup.getPartitionTablePropertiesContext(),
            localDateTimeCleanupBefore
        );
    }
}