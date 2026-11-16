package com.example.myauthapp.partition.service.recoveryretry;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionOperationDTO;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.repository.create.PartitionTargetCreateJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.partitionoperation.PartitionOperationDTODiscoveryJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.partitionrange.PartitionRangeDTODiscoveryJdbcRepository;
import com.example.myauthapp.partition.repository.partitionoperation.PartitionOperationJdbcRepository;
import com.example.myauthapp.partition.repository.delete.PartitionDefaultDeleteJdbcRepository;
import com.example.myauthapp.partition.service.recoveryfacade.PartitionRecoveryFacadeJdbcService;
import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;
import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionRecoveryPartitionError;
import com.example.myauthapp.partition.service.recovery.PartitionRecoveryVerifyJdbcService;
import com.example.myauthapp.partition.message.PartitionRecoveryPartitionMessage;

@Service
public class PartitionRecoveryRetryService {

    private final PartitionOperationDTODiscoveryJdbcRepository
        partitionOperationDTODiscoveryJdbcRepository;

    private final PartitionRangeDTODiscoveryJdbcRepository
        partitionRangeDTODiscoveryJdbcRepository;

    private final PartitionTargetCreateJdbcRepository
        partitionTargetCreateJdbcRepository;

    private final PartitionDefaultDeleteJdbcRepository
        partitionDefaultDeleteJdbcRepository;

    private final PartitionOperationJdbcRepository
        partitionOperationJdbcRepository;

    private final PartitionRecoveryVerifyJdbcService
        partitionRecoveryVerifyJdbcService;

    private final PartitionRecoveryRetryValidationJdbcService
        partitionRecoveryRetryValidationJdbcService;

    private final PartitionRecoveryFacadeJdbcService
        partitionRecoveryFacadeJdbcService;

    private static final Logger logger =
        LoggerFactory.getLogger(
            PartitionRecoveryRetryService.class
        );

    public PartitionRecoveryRetryService(
        PartitionOperationDTODiscoveryJdbcRepository
            partitionOperationDTODiscoveryJdbcRepository,
        PartitionRangeDTODiscoveryJdbcRepository
            partitionRangeDTODiscoveryJdbcRepository,
        PartitionTargetCreateJdbcRepository
            partitionTargetCreateJdbcRepository,
        PartitionDefaultDeleteJdbcRepository
            partitionDefaultDeleteJdbcRepository,
        PartitionOperationJdbcRepository
            partitionOperationJdbcRepository,
        PartitionRecoveryVerifyJdbcService
            partitionRecoveryVerifyJdbcService,
        PartitionRecoveryRetryValidationJdbcService
            partitionRecoveryRetryValidationJdbcService,
        PartitionRecoveryFacadeJdbcService
            partitionRecoveryFacadeJdbcService
    ) {
        this.partitionOperationDTODiscoveryJdbcRepository =
            partitionOperationDTODiscoveryJdbcRepository;

        this.partitionRangeDTODiscoveryJdbcRepository =
            partitionRangeDTODiscoveryJdbcRepository;

        this.partitionTargetCreateJdbcRepository =
            partitionTargetCreateJdbcRepository;

        this.partitionDefaultDeleteJdbcRepository =
            partitionDefaultDeleteJdbcRepository;

        this.partitionOperationJdbcRepository =
            partitionOperationJdbcRepository;

        this.partitionRecoveryVerifyJdbcService =
            partitionRecoveryVerifyJdbcService;

        this.partitionRecoveryRetryValidationJdbcService =
            partitionRecoveryRetryValidationJdbcService;

        this.partitionRecoveryFacadeJdbcService =
            partitionRecoveryFacadeJdbcService;
    }

    public void retryFailedOperationsByPartitionRangeDTO(
        PartitionContextGroup partitionContextGroup,
        PartitionRangeDTO partitionRangeDTO
    ) {

        List<PartitionOperationDTO> failedOperations =
            partitionOperationDTODiscoveryJdbcRepository
                .findFailedPartitionOperationDTOsByPartitionRangeId(
                    partitionRangeDTO.getId()
                );

        for (PartitionOperationDTO operation : failedOperations) {

            UUID operationId =
                operation.getPublicPartitionOperationId();

            try {
                partitionRecoveryFacadeJdbcService.executeRecoveryAndRecoveryRetry(
                    partitionContextGroup,
                    partitionRangeDTO,
                    operationId
                );

            } catch (Exception exception) {

                logger.error(
                    "Failed to retry previous partition recovery. "
                        + "partitionRangeId={}, publicPartitionOperationId={}",
                    partitionRangeDTO.getId(),
                    operationId,
                    exception
                );

                throw exception;
            }
        }
    }

    /**
     * FAILEDの全PartitionOperationをRetryする。
     */
    public void retryAllFailedPartitionOperations(
        PartitionContextGroup partitionContextGroup
    ) {
        Objects.requireNonNull(
            partitionContextGroup,
            "partitionContextGroup must not be null"
        );

        List<PartitionOperationDTO> failedPartitionOperationDTOs =
            partitionOperationDTODiscoveryJdbcRepository
                .findPartitionOperationDTOsByPartitionOperationStatus(
                    PartitionOperationStatus.FAILED
                );

        for (
            PartitionOperationDTO partitionOperationDTO
                : failedPartitionOperationDTOs
        ) {
            Long partitionRangeId =
                Objects.requireNonNull(
                    partitionOperationDTO.getPartitionRangeId(),
                    "partitionRangeId must not be null"
                );

            UUID publicPartitionOperationId =
                Objects.requireNonNull(
                    partitionOperationDTO
                        .getPublicPartitionOperationId(),
                    "publicPartitionOperationId must not be null"
                );

            PartitionRangeDTO partitionRangeDTO =
                partitionRangeDTODiscoveryJdbcRepository
                    .findPartitionRangeDTOById(partitionRangeId);

            try {
                partitionRecoveryFacadeJdbcService.executeRecoveryAndRecoveryRetry(
                    partitionContextGroup,
                    partitionRangeDTO,
                    publicPartitionOperationId
                );
            } catch (Exception exception) {
                
                logger.error(
                    PartitionRecoveryPartitionMessage
                        .PARTITION_RECOVERY_RETRY_FAILED
                        .getMessage()
                        .formatted(
                            publicPartitionOperationId,
                            exception.getMessage()
                        )
                );

            }
        }
    }

    
}