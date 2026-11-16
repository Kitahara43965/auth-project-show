package com.example.myauthapp.partition.service.recoveryretry;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionOperationDTO;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.PartitionRecoverPartitionResult;
import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;
import com.example.myauthapp.partition.error.PartitionRecoveryPartitionError;
import com.example.myauthapp.partition.repository.discovery.partitionoperation.PartitionOperationDTODiscoveryJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.count.PartitionStagingCountJdbcRepository;
import com.example.myauthapp.partition.repository.recoveryretry.PartitionStagingRecoveryRetryJdbcRepository;
import com.example.myauthapp.partition.repository.delete.PartitionDefaultDeleteJdbcRepository;
import com.example.myauthapp.partition.repository.partitionoperation.PartitionOperationJdbcRepository;
import com.example.myauthapp.partition.service.recovery.PartitionRecoveryRestoreJdbcService;
import com.example.myauthapp.web.partition.exception.PartitionException;

@Service
public class PartitionRecoveryRetryJdbcService {

    private final PartitionOperationDTODiscoveryJdbcRepository
        partitionOperationDTODiscoveryJdbcRepository;

    private final PartitionOperationJdbcRepository
        partitionOperationJdbcRepository;

    private final PartitionRecoveryRetryValidationJdbcService
        partitionRecoveryRetryValidationJdbcService;

    private final PartitionStagingRecoveryRetryJdbcRepository
        partitionStagingRecoveryRetryJdbcRepository;

    private final PartitionStagingCountJdbcRepository
        partitionStagingCountJdbcRepository;

    private final PartitionRecoveryRestoreJdbcService
        partitionRecoveryRestoreJdbcService;

    private final PartitionDefaultDeleteJdbcRepository
        partitionDefaultDeleteJdbcRepository;

    public PartitionRecoveryRetryJdbcService(
        PartitionOperationDTODiscoveryJdbcRepository
            partitionOperationDTODiscoveryJdbcRepository,
        PartitionOperationJdbcRepository
            partitionOperationJdbcRepository,
        PartitionRecoveryRetryValidationJdbcService
            partitionRecoveryRetryValidationJdbcService,
        PartitionStagingRecoveryRetryJdbcRepository
            partitionStagingRecoveryRetryJdbcRepository,
        PartitionStagingCountJdbcRepository
            partitionStagingCountJdbcRepository,
        PartitionRecoveryRestoreJdbcService
            partitionRecoveryRestoreJdbcService,
        PartitionDefaultDeleteJdbcRepository
            partitionDefaultDeleteJdbcRepository
    ) {
        this.partitionOperationDTODiscoveryJdbcRepository =
            partitionOperationDTODiscoveryJdbcRepository;

        this.partitionOperationJdbcRepository =
            partitionOperationJdbcRepository;

        this.partitionRecoveryRetryValidationJdbcService =
            partitionRecoveryRetryValidationJdbcService;

        this.partitionStagingRecoveryRetryJdbcRepository =
            partitionStagingRecoveryRetryJdbcRepository;

        this.partitionStagingCountJdbcRepository =
            partitionStagingCountJdbcRepository;

        this.partitionRecoveryRestoreJdbcService =
            partitionRecoveryRestoreJdbcService;

        this.partitionDefaultDeleteJdbcRepository =
            partitionDefaultDeleteJdbcRepository;
    }

    @Transactional(
        value = "auditTransactionManager",
        rollbackFor = Exception.class
    )
    public void retry(
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

        String errorMessage = null;

        // ① Operationを取得
        PartitionOperationDTO partitionOperationDTO =
            partitionOperationDTODiscoveryJdbcRepository
                .findPartitionOperationDTOByPublicPartitionOperationId(
                    publicPartitionOperationId
                );

        // ② すでに完了していればRetryしない
        if (
            partitionOperationDTO.getPartitionOperationStatus()
                == PartitionOperationStatus.COMPLETED
        ) {
            return;
        }

        if (
            partitionOperationDTO.getPartitionOperationStatus()
                != PartitionOperationStatus.FAILED
        ) {

            throw new PartitionException(
                PartitionRecoveryPartitionError
                    .PARTITION_OPERATION_STATUS_IS_NOT_FAILED,
                partitionOperationDTO.getPartitionOperationStatus()
            );
        }

        // ③ FAILED → RUNNING
        //    ここで同時Retryの実行権を取得する
        partitionStagingRecoveryRetryJdbcRepository.startRetry(
            publicPartitionOperationId,
            LocalDateTime.now()
        );            

        try {

            // ④ Retry実行前の検証
            PartitionRecoverPartitionResult partitionRecoverPartitionResult =
                partitionRecoveryRetryValidationJdbcService
                    .validateRecoveryRetrySourceConsistency(
                        partitionTablePropertiesContext,
                        partitionRecoveryTableExpressionProviderContext,
                        partitionRangeDTO,
                        publicPartitionOperationId
                    );

            long defaultPartitionRowCount = 
                partitionRecoverPartitionResult.getDefaultPartitionRowCount();
            long stagingRowCount = partitionRecoverPartitionResult.getStagingRowCount();

            partitionDefaultDeleteJdbcRepository
                .deleteDefaultPartitionRows(
                    partitionTablePropertiesContext,
                    partitionRangeDTO
                );

            partitionRecoveryRestoreJdbcService.restorePartition(
                partitionTablePropertiesContext,
                partitionRecoveryTableExpressionProviderContext,
                partitionRangeDTO,
                publicPartitionOperationId
            );

            partitionOperationJdbcRepository.complete(
                publicPartitionOperationId,
                LocalDateTime.now(),
                defaultPartitionRowCount,
                stagingRowCount
            );

        } catch (Exception exception) {

            if (exception instanceof PartitionException) {
                PartitionException partitionException = (PartitionException) exception;

                errorMessage =
                    partitionException.getPartitionError().getErrorCode()
                        + ": "
                        + partitionException.getMessage();
            } else {
                errorMessage = exception.getMessage();
            }

            partitionOperationJdbcRepository.fail(
                publicPartitionOperationId,
                LocalDateTime.now(),
                errorMessage
            );

            if (exception instanceof PartitionException) {
                throw (PartitionException)exception;
            }else{
                throw new PartitionException(
                    PartitionRecoveryPartitionError.PARTITION_RECOVERY_FAILED,
                    exception,
                    errorMessage
                );

            }
        }

    }


}