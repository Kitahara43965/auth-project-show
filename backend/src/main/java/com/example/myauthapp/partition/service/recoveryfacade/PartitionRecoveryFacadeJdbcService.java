package com.example.myauthapp.partition.service.recoveryfacade;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;

import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionOperationDTO;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.PartitionRecoverPartitionResult;

import com.example.myauthapp.partition.repository.lock.PartitionTargetLockJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.partitionoperation.PartitionOperationDTODiscoveryJdbcRepository;

import com.example.myauthapp.partition.service.recoveryretry.PartitionRecoveryRetryJdbcService;
import com.example.myauthapp.partition.service.recovery.PartitionRecoveryJdbcService;

@Service
public class PartitionRecoveryFacadeJdbcService {

    private final PartitionTargetLockJdbcRepository
        partitionTargetLockJdbcRepository;

    private final PartitionOperationDTODiscoveryJdbcRepository
        partitionOperationDTODiscoveryJdbcRepository;

    private final PartitionRecoveryRetryJdbcService
        partitionRecoveryRetryJdbcService;

    private final PartitionRecoveryJdbcService
        partitionRecoveryJdbcService;

    public PartitionRecoveryFacadeJdbcService(
        PartitionTargetLockJdbcRepository partitionTargetLockJdbcRepository,
        PartitionOperationDTODiscoveryJdbcRepository partitionOperationDTODiscoveryJdbcRepository,
        PartitionRecoveryRetryJdbcService partitionRecoveryRetryJdbcService,
        PartitionRecoveryJdbcService partitionRecoveryJdbcService
    ){
        this.partitionTargetLockJdbcRepository = partitionTargetLockJdbcRepository;
        this.partitionOperationDTODiscoveryJdbcRepository = partitionOperationDTODiscoveryJdbcRepository;
        this.partitionRecoveryRetryJdbcService = partitionRecoveryRetryJdbcService;
        this.partitionRecoveryJdbcService = partitionRecoveryJdbcService;
    }

   @Transactional(
        value = "auditTransactionManager",
        rollbackFor = Exception.class
    )
    public PartitionRecoverPartitionResult executeRecoveryAndRecoveryRetry(
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
                partitionContextGroup
                    .getPartitionRecoveryTableExpressionProviderContext();

        // 1. 同じPartition Rangeの処理を排他する
        partitionTargetLockJdbcRepository
            .acquirePartitionLock(
                partitionTablePropertiesContext,
                partitionRangeDTO
            );

        // 2. 同じRangeのFAILED Operationを取得
        List<PartitionOperationDTO> failedPartitionOperationDTOs =
            partitionOperationDTODiscoveryJdbcRepository
                .findFailedPartitionOperationDTOsByPartitionRangeId(
                    partitionRangeDTO.getId()
                );

        // 3. FAILED OperationをRetry
        for (
            PartitionOperationDTO failedPartitionOperationDTO
                : failedPartitionOperationDTOs
        ) {
            partitionRecoveryRetryJdbcService.retry(
                partitionContextGroup,
                partitionRangeDTO,
                failedPartitionOperationDTO.getPublicPartitionOperationId()
            );
        }

        // 4. 新しいRecoveryを実行
        return partitionRecoveryJdbcService.recoverPartition(
            partitionContextGroup,
            partitionRangeDTO,
            publicPartitionOperationId
        );
    }
}