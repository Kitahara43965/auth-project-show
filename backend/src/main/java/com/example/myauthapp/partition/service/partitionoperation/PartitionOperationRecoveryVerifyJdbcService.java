package com.example.myauthapp.partition.service.partitionoperation;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.repository.discovery.count.PartitionDefaultCountJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.count.PartitionStagingCountJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.count.PartitionTargetCountJdbcRepository;
import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionRecoveryPartitionError;
import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;
import com.example.myauthapp.partition.dto
    .PartitionDeterminePartitionOperationStatusResult;

@Service
public class PartitionOperationRecoveryVerifyJdbcService {

    private final PartitionDefaultCountJdbcRepository partitionDefaultCountJdbcRepository;
    private final PartitionStagingCountJdbcRepository partitionStagingCountJdbcRepository;
    private final PartitionTargetCountJdbcRepository partitionTargetCountJdbcRepository;

    public PartitionOperationRecoveryVerifyJdbcService(
        PartitionDefaultCountJdbcRepository partitionDefaultCountJdbcRepository,
        PartitionStagingCountJdbcRepository partitionStagingCountJdbcRepository,
        PartitionTargetCountJdbcRepository partitionTargetCountJdbcRepository
    ) {
        this.partitionDefaultCountJdbcRepository = partitionDefaultCountJdbcRepository;
        this.partitionStagingCountJdbcRepository = partitionStagingCountJdbcRepository;
        this.partitionTargetCountJdbcRepository = partitionTargetCountJdbcRepository;
    }

    public PartitionDeterminePartitionOperationStatusResult determinePartitionOperationStatus(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionRangeDTO partitionRangeDTO,
        UUID publicPartitionOperationId
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );
        Objects.requireNonNull(
            partitionRangeDTO,
            "partitionRangeDTO must not be null"
        );
        Objects.requireNonNull(
            publicPartitionOperationId,
            "publicPartitionOperationId must not be null"
        );
        PartitionOperationStatus partitionOperationStatus = null;

        // 1. DEFAULTの対象期間の件数を取得
        long defaultRowCount =
            partitionDefaultCountJdbcRepository.countDefaultPartitionRowCount(
                partitionTablePropertiesContext,
                partitionRangeDTO
            );

        // 2. STAGINGの対象Operationの件数を取得
        long stagingRowCount =
            partitionStagingCountJdbcRepository
                .countStagingRowCountByPublicPartitionOperationId(
                    publicPartitionOperationId
                );

        // 3. TARGETの対象期間の件数を取得
        long targetPartitionRowCount =
            partitionTargetCountJdbcRepository.countTargetPartitionRowCount(
                partitionTablePropertiesContext,
                partitionRangeDTO
            );

        // 0件の正常完了
        if (defaultRowCount == 0L
            && stagingRowCount == 0L
            && targetPartitionRowCount > 0L) {

            partitionOperationStatus = PartitionOperationStatus.COMPLETED;

        } else if (defaultRowCount > 0L
            && stagingRowCount == 0L
            && targetPartitionRowCount == 0L) {

            partitionOperationStatus = PartitionOperationStatus.FAILED;

        } else if (defaultRowCount == 0L
            && stagingRowCount > 0L
            && targetPartitionRowCount == 0L) {

            partitionOperationStatus = PartitionOperationStatus.FAILED;

        } else {

            partitionOperationStatus = PartitionOperationStatus.INCONSISTENT;
        }

        return new PartitionDeterminePartitionOperationStatusResult(
            partitionOperationStatus,
            defaultRowCount,
            stagingRowCount,
            targetPartitionRowCount
        );
    }//determinePartitionOperationStatus
}
