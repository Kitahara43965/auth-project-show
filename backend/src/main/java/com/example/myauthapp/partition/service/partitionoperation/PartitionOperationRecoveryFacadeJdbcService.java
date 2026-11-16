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
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.service.discovery.time.PartitionTimeDiscoveryJdbcService;

import com.example.myauthapp.partition.repository.partitionoperation.PartitionOperationJdbcRepository;
import com.example.myauthapp.partition.service.partitionmetadata.PartitionMetadataService;
import com.example.myauthapp.partition.service.recoveryfacade.PartitionRecoveryFacadeJdbcService;
import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;
import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionRecoveryPartitionError;
import com.example.myauthapp.partition.message.PartitionRecoveryPartitionMessage;

import com.example.myauthapp.partition.repository.lock.PartitionTargetLockJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.partitionoperation
    .PartitionOperationDTODiscoveryJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.partitionrange
    .PartitionRangeDTODiscoveryJdbcRepository;
import com.example.myauthapp.partition.service.partitionoperation
    .PartitionOperationRecoveryVerifyJdbcService;
import com.example.myauthapp.partition.dto
    .PartitionDeterminePartitionOperationStatusResult;
import com.example.myauthapp.partition.dto
    .PartitionRecoverStalePartitionOperationResult;


@Service
public class PartitionOperationRecoveryFacadeJdbcService {

    private final PartitionOperationDTODiscoveryJdbcRepository
        partitionOperationDTODiscoveryJdbcRepository;
    private final PartitionRangeDTODiscoveryJdbcRepository
        partitionRangeDTODiscoveryJdbcRepository;
    private final PartitionTargetLockJdbcRepository
        partitionTargetLockJdbcRepository;
    private final PartitionOperationJdbcRepository
        partitionOperationJdbcRepository;
    private final PartitionOperationRecoveryVerifyJdbcService
        partitionOperationRecoveryVerifyJdbcService;

    public PartitionOperationRecoveryFacadeJdbcService(
        PartitionOperationDTODiscoveryJdbcRepository
            partitionOperationDTODiscoveryJdbcRepository,
        PartitionRangeDTODiscoveryJdbcRepository
            partitionRangeDTODiscoveryJdbcRepository,
        PartitionTargetLockJdbcRepository
            partitionTargetLockJdbcRepository,
        PartitionOperationJdbcRepository
            partitionOperationJdbcRepository,
        PartitionOperationRecoveryVerifyJdbcService
            partitionOperationRecoveryVerifyJdbcService
    ){
        this.partitionOperationDTODiscoveryJdbcRepository =
            partitionOperationDTODiscoveryJdbcRepository;
        this.partitionRangeDTODiscoveryJdbcRepository =
            partitionRangeDTODiscoveryJdbcRepository;
        this.partitionTargetLockJdbcRepository =
            partitionTargetLockJdbcRepository;
        this.partitionOperationJdbcRepository =
            partitionOperationJdbcRepository;
        this.partitionOperationRecoveryVerifyJdbcService =
            partitionOperationRecoveryVerifyJdbcService;
    }

    @Transactional(
        value = "auditTransactionManager",
        rollbackFor = Exception.class
    )
    public PartitionRecoverStalePartitionOperationResult 
        recoverStalePartitionOperation(
            PartitionContextGroup partitionContextGroup,
            LocalDateTime thresholdLocalDateTime,
            PartitionOperationDTO staleRunningPartitionOperationDTO
    ) {
        Objects.requireNonNull(
            partitionContextGroup,
            "partitionContextGroup must not be null"
        );
        Objects.requireNonNull(
            thresholdLocalDateTime,
            "thresholdLocalDateTime must not be null"
        );
        Objects.requireNonNull(
            staleRunningPartitionOperationDTO,
            "staleRunningPartitionOperationDTO must not be null"
        );

        LocalDateTime localDateTimeNow = LocalDateTime.now();

        PartitionTablePropertiesContext partitionTablePropertiesContext =
            partitionContextGroup.getPartitionTablePropertiesContext();

        Long staleRunningPartitionRangeId =
            staleRunningPartitionOperationDTO.getPartitionRangeId();

        // 1. 操作対象のPartition Rangeを取得
        PartitionRangeDTO actualPartitionRangeDTO =
            partitionRangeDTODiscoveryJdbcRepository
                .findPartitionRangeDTOById(staleRunningPartitionRangeId);

        // 2. 同じPartition Rangeの処理を排他する
        partitionTargetLockJdbcRepository.acquirePartitionLock(
            partitionTablePropertiesContext,
            actualPartitionRangeDTO
        );

        // 3. Operationの最新状態を取得
        PartitionOperationDTO actualPartitionOperationDTO =
            partitionOperationDTODiscoveryJdbcRepository
                .findPartitionOperationDTOByPublicPartitionOperationId(
                    staleRunningPartitionOperationDTO
                        .getPublicPartitionOperationId()
                );

        UUID actualPublicPartitionOperationId = 
            actualPartitionOperationDTO.getPublicPartitionOperationId();
        PartitionOperationStatus actualPartitionOperationStatus = 
            actualPartitionOperationDTO.getPartitionOperationStatus();
        LocalDateTime actualStartedAt =
            actualPartitionOperationDTO.getStartedAt();
        Long actualPartitionRangeId =
            actualPartitionOperationDTO.getPartitionRangeId();

        if (!actualPartitionRangeDTO.getId().equals(actualPartitionRangeId)) {
            throw new PartitionException(
                PartitionRecoveryPartitionError.PARTITION_RANGE_ID_MISMATCH,
                actualPartitionRangeDTO.getId(),
                actualPartitionRangeId
            );
        }

        boolean isPartitionOperationStatusSkipped = false;

        // 4. RUNNINGでなければ復旧対象外
        if (actualPartitionOperationStatus != PartitionOperationStatus.RUNNING) {
            isPartitionOperationStatusSkipped = true;
        }

        if (actualStartedAt == null || actualStartedAt.isAfter(thresholdLocalDateTime)) {
            isPartitionOperationStatusSkipped = true;
        }

        if(isPartitionOperationStatusSkipped){
            return new PartitionRecoverStalePartitionOperationResult(
                actualPartitionRangeId,
                actualPublicPartitionOperationId,
                actualPartitionOperationStatus
            );
        }//isPartitionOperationStatusSkipped

        PartitionDeterminePartitionOperationStatusResult
            partitionDeterminePartitionOperationStatusResult =
                partitionOperationRecoveryVerifyJdbcService
                    .determinePartitionOperationStatus(
                        partitionTablePropertiesContext,
                        actualPartitionRangeDTO,
                        actualPublicPartitionOperationId
                    );
        PartitionOperationStatus determinedPartitionOperationStatus =
            partitionDeterminePartitionOperationStatusResult
                .getPartitionOperationStatus();
        long determinedDefaultPartitionRowCount =
            partitionDeterminePartitionOperationStatusResult
                .getDefaultPartitionRowCount();
        long determinedStagingRowCount =
            partitionDeterminePartitionOperationStatusResult
                .getStagingRowCount();

        // 6. 検証結果に応じてOperation状態を更新
        if(determinedPartitionOperationStatus == PartitionOperationStatus.RUNNING){
            

        }else if(determinedPartitionOperationStatus == PartitionOperationStatus.FAILED){
            // 検証によってRetry可能と確認できた場合
            partitionOperationJdbcRepository.fail(
                actualPublicPartitionOperationId,
                localDateTimeNow,
                "Recovery Retry OK by Recovery Operation Check"
            );

        }else if(determinedPartitionOperationStatus == PartitionOperationStatus.COMPLETED){

            partitionOperationJdbcRepository.complete(
                actualPublicPartitionOperationId,
                localDateTimeNow,
                determinedDefaultPartitionRowCount,
                determinedStagingRowCount
            );

        }else if(determinedPartitionOperationStatus == PartitionOperationStatus.INCONSISTENT){

            /*partitionOperationJdbcRepository.markInconsistent(
                actualPublicPartitionOperationId
            );*/
        }else{//determinedPartitionOperationStatus
            throw new PartitionException(
                PartitionRecoveryPartitionError
                    .PARTITION_OPERATION_RECOVERY_STATE_IS_INVALID,
                determinedPartitionOperationStatus.name()
            );
        }//determinedPartitionOperationStatus

        return new PartitionRecoverStalePartitionOperationResult(
            actualPartitionRangeId,
            actualPublicPartitionOperationId,
            determinedPartitionOperationStatus
        );
    }

}