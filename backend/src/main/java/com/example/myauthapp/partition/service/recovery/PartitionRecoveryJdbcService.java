package com.example.myauthapp.partition.service.recovery;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.PartitionRecoverPartitionResult;
import com.example.myauthapp.partition.repository.discovery.count.PartitionStagingCountJdbcRepository;

@Service
public class PartitionRecoveryJdbcService {
    
    private final PartitionStagingCountJdbcRepository
        partitionStagingCountJdbcRepository;

    private final PartitionRecoveryMoveJdbcService
        partitionRecoveryMoveJdbcService;

    private final PartitionRecoveryRestoreJdbcService
        partitionRecoveryRestoreJdbcService;

    public PartitionRecoveryJdbcService(
        PartitionStagingCountJdbcRepository partitionStagingCountJdbcRepository,
        PartitionRecoveryMoveJdbcService partitionRecoveryMoveJdbcService,
        PartitionRecoveryRestoreJdbcService partitionRecoveryRestoreJdbcService
    ) {
        this.partitionStagingCountJdbcRepository =
            partitionStagingCountJdbcRepository;

        this.partitionRecoveryMoveJdbcService =
            partitionRecoveryMoveJdbcService;

        this.partitionRecoveryRestoreJdbcService =
            partitionRecoveryRestoreJdbcService;
    }

    @Transactional(
        value = "auditTransactionManager",
        rollbackFor = Exception.class
    )
    public PartitionRecoverPartitionResult recoverPartition(
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

        PartitionTablePropertiesContext 
            partitionTablePropertiesContext =
                partitionContextGroup.getPartitionTablePropertiesContext();

        PartitionRecoveryTableExpressionProviderContext 
            partitionRecoveryTableExpressionProviderContext =
                partitionContextGroup.getPartitionRecoveryTableExpressionProviderContext();

        long defaultPartitionRowCount = partitionRecoveryMoveJdbcService.moveDefaultToStaging(
            partitionTablePropertiesContext,
            partitionRecoveryTableExpressionProviderContext,
            partitionRangeDTO,
            publicPartitionOperationId
        );

        long stagingRowCount = 
            partitionStagingCountJdbcRepository.countStagingRowCountByPublicPartitionOperationId(
                publicPartitionOperationId
            );

        partitionRecoveryRestoreJdbcService.restorePartition(
            partitionTablePropertiesContext,
            partitionRecoveryTableExpressionProviderContext,
            partitionRangeDTO,
            publicPartitionOperationId
        );

        return new PartitionRecoverPartitionResult(
            publicPartitionOperationId,
            defaultPartitionRowCount,
            stagingRowCount
        );

    }
}