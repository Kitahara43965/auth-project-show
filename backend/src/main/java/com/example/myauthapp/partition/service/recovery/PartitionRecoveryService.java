package com.example.myauthapp.partition.service.recovery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.myauthapp.partition.context.PartitionOperationPropertiesContext;
import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.PartitionOperationDTO;
import com.example.myauthapp.partition.dto.PartitionRecoverPartitionResult;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.service.discovery.time.PartitionTimeDiscoveryJdbcService;
import com.example.myauthapp.partition.repository.partitionoperation.PartitionOperationJdbcRepository;
import com.example.myauthapp.partition.service.partitionmetadata.PartitionMetadataService;
import com.example.myauthapp.partition.service.recoveryfacade.PartitionRecoveryFacadeJdbcService;
import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;
import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionRecoveryPartitionError;
import com.example.myauthapp.partition.message.PartitionRecoveryPartitionMessage;

@Service
public class PartitionRecoveryService {

    private final PartitionRecoveryJdbcService partitionRecoveryJdbcService;
    private final PartitionMetadataService partitionMetadataService;
    private final PartitionTimeDiscoveryJdbcService partitionTimeDiscoveryJdbcService;
    private final PartitionOperationJdbcRepository partitionOperationJdbcRepository;
    private final PartitionRecoveryFacadeJdbcService partitionRecoveryFacadeJdbcService;

    private static final Logger logger =
        LoggerFactory.getLogger(
            PartitionRecoveryService.class
        );

    public PartitionRecoveryService(
        PartitionRecoveryJdbcService partitionRecoveryJdbcService,
        PartitionMetadataService partitionMetadataService,
        PartitionTimeDiscoveryJdbcService partitionTimeDiscoveryJdbcService,
        PartitionOperationJdbcRepository partitionOperationJdbcRepository,
        PartitionRecoveryFacadeJdbcService partitionRecoveryFacadeJdbcService
    ) {
        this.partitionRecoveryJdbcService =
            partitionRecoveryJdbcService;

        this.partitionMetadataService =
            partitionMetadataService;

        this.partitionTimeDiscoveryJdbcService =
            partitionTimeDiscoveryJdbcService;

        this.partitionOperationJdbcRepository =
            partitionOperationJdbcRepository;

        this.partitionRecoveryFacadeJdbcService =
            partitionRecoveryFacadeJdbcService;
    }


    public void performPartitionRecovery(
        PartitionContextGroup partitionContextGroup,
        LocalDateTime targetMonth
    ) {
        Objects.requireNonNull(
            partitionContextGroup,
            "partitionContextGroup must not be null"
        );

        Objects.requireNonNull(
            targetMonth,
            "targetMonth must not be null"
        );

        PartitionTablePropertiesContext partitionTablePropertiesContext =
            partitionContextGroup.getPartitionTablePropertiesContext();

        PartitionOperationPropertiesContext partitionOperationPropertiesContext =
            partitionContextGroup.getPartitionOperationPropertiesContext();

        PartitionRangeDTO partitionRangeDTO =
            partitionMetadataService.ensurePartition(
                partitionTablePropertiesContext,
                partitionOperationPropertiesContext,
                targetMonth
            );

        // 新しいRecovery OperationをRUNNINGで登録
        PartitionOperationDTO partitionOperationDTO =
            partitionOperationJdbcRepository.create(
                partitionOperationPropertiesContext,
                partitionRangeDTO,
                PartitionOperationStatus.RUNNING
            );

        UUID publicPartitionOperationId =
            partitionOperationDTO.getPublicPartitionOperationId();

        String newErrorMessage = null;

        try {
            // FAILED OperationのRetryと新しいRecoveryを実行
            PartitionRecoverPartitionResult partitionRecoverPartitionResult =
                partitionRecoveryFacadeJdbcService
                    .executeRecoveryAndRecoveryRetry(
                        partitionContextGroup,
                        partitionRangeDTO,
                        publicPartitionOperationId
                    );

            // 新しいOperationをCOMPLETEDにする
            partitionOperationJdbcRepository.complete(
                publicPartitionOperationId,
                LocalDateTime.now(),
                partitionRecoverPartitionResult.getDefaultPartitionRowCount(),
                partitionRecoverPartitionResult.getStagingRowCount()
            );

        } catch (Exception exception) {

            if (exception.getMessage() == null) {
                newErrorMessage = exception.getClass().getName();
            } else {
                newErrorMessage = exception.getMessage();
            }

            // 新しいOperationをFAILEDにする
            partitionOperationJdbcRepository.fail(
                publicPartitionOperationId,
                LocalDateTime.now(),
                newErrorMessage
            );

            if (exception instanceof PartitionException) {
                throw (PartitionException) exception;
            }

            throw new PartitionException(
                PartitionRecoveryPartitionError.PARTITION_CREATION_FAILED,
                exception,
                newErrorMessage
            );
        }
    }

    public void recoverAllPartitions(
        PartitionContextGroup partitionContextGroup
    ) {
        Objects.requireNonNull(
            partitionContextGroup,
            "partitionContextGroup must not be null"
        );

        List<LocalDateTime> targetMonths =
            partitionTimeDiscoveryJdbcService.findRecoveryTargetMonths(
                partitionContextGroup.getPartitionTablePropertiesContext()
            );

        PartitionException firstPartitionException = null;

        for (LocalDateTime targetMonth : targetMonths) {
            try {
                performPartitionRecovery(
                    partitionContextGroup,
                    targetMonth
                );
            } catch (RuntimeException exception) {
                logger.error(
                    PartitionRecoveryPartitionMessage.SIMULTANEOUS_PARTITION_CREATION_FAILED
                        .getMessage()
                        .formatted(targetMonth, exception.getMessage())
                );

                if (firstPartitionException == null) {
                    if (exception instanceof PartitionException partitionException) {
                        firstPartitionException = partitionException;
                    } else {
                        firstPartitionException = new PartitionException(
                            PartitionRecoveryPartitionError.SIMULTANEOUS_PARTITION_CREATION_FAILED,
                            exception,
                            exception.getMessage()
                        );
                    }
                }
            }
        }

        if (firstPartitionException != null) {
            throw firstPartitionException;
        }
    }
}