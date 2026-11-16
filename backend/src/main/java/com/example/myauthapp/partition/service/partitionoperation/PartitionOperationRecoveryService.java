package com.example.myauthapp.partition.service.partitionoperation;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.myauthapp.partition.context.PartitionOperationPropertiesContext;
import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.PartitionOperationDTO;
import com.example.myauthapp.partition.dto.PartitionRecoverPartitionResult;
import com.example.myauthapp.partition.dto.PartitionRecoverStalePartitionOperationResult;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.service.discovery.time.PartitionTimeDiscoveryJdbcService;
import com.example.myauthapp.partition.repository.partitionoperation.PartitionOperationJdbcRepository;
import com.example.myauthapp.partition.service.partitionmetadata.PartitionMetadataService;
import com.example.myauthapp.partition.service.recoveryfacade.PartitionRecoveryFacadeJdbcService;
import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;
import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionRecoveryPartitionError;
import com.example.myauthapp.partition.message.PartitionRecoveryPartitionMessage;

import com.example.myauthapp.partition.repository.discovery.partitionoperation
    .PartitionOperationDTODiscoveryJdbcRepository;
import com.example.myauthapp.partition.service.partitionoperation
    .PartitionOperationRecoveryFacadeJdbcService;

@Service
public class PartitionOperationRecoveryService {

    private final PartitionOperationDTODiscoveryJdbcRepository
        partitionOperationDTODiscoveryJdbcRepository;
    private final PartitionOperationRecoveryFacadeJdbcService
        partitionOperationRecoveryFacadeJdbcService;

    public PartitionOperationRecoveryService(
        PartitionOperationDTODiscoveryJdbcRepository
            partitionOperationDTODiscoveryJdbcRepository,
        PartitionOperationRecoveryFacadeJdbcService
            partitionOperationRecoveryFacadeJdbcService
    ){
        this.partitionOperationDTODiscoveryJdbcRepository =
            partitionOperationDTODiscoveryJdbcRepository;
        this.partitionOperationRecoveryFacadeJdbcService =
            partitionOperationRecoveryFacadeJdbcService;
    }

    public void recoverStalePartitionOperations(
        PartitionContextGroup partitionContextGroup,
        LocalDateTime thresholdLocalDateTime
    ) {
        Objects.requireNonNull(
            partitionContextGroup,
            "partitionContextGroup must not be null"
        );
        Objects.requireNonNull(
            thresholdLocalDateTime,
            "thresholdLocalDateTime must not be null"
        );

        List<PartitionOperationDTO> staleRunningPartitionOperationDTOs =
            partitionOperationDTODiscoveryJdbcRepository
                .findPartitionOperationDTOsByPartitionOperationStatusAndThresholdLocalDateTime(
                    PartitionOperationStatus.RUNNING,
                    thresholdLocalDateTime
                );

        for (PartitionOperationDTO staleRunningPartitionOperationDTO
                : staleRunningPartitionOperationDTOs) {

            PartitionRecoverStalePartitionOperationResult partitionRecoverStalePartitionOperationResult =
                partitionOperationRecoveryFacadeJdbcService.recoverStalePartitionOperation(
                    partitionContextGroup,
                    thresholdLocalDateTime,
                    staleRunningPartitionOperationDTO
                );

            PartitionOperationStatus partitionOperationStatus =
                partitionRecoverStalePartitionOperationResult.getPartitionOperationStatus();

            if (partitionOperationStatus == PartitionOperationStatus.FAILED) {

            }//partitionOperationStatus

        }
    }

}