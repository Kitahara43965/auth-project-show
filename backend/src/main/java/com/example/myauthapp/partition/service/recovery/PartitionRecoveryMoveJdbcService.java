package com.example.myauthapp.partition.service.recovery;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.PartitionRecordSource;
import com.example.myauthapp.partition.repository.discovery.count.PartitionDefaultCountJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.count.PartitionStagingCountJdbcRepository;
import com.example.myauthapp.partition.repository.delete.PartitionDefaultDeleteJdbcRepository;
import com.example.myauthapp.partition.repository.delete.PartitionStagingDeleteJdbcRepository;
import com.example.myauthapp.partition.service.debug.PartitionDebugCountJdbcService;

import com.example.myauthapp.partition.repository.discovery.source
    .PartitionDefaultPartitionRecordSourceDiscoveryJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.source
    .PartitionStagingPartitionRecordSourceDiscoveryJdbcRepository;
import com.example.myauthapp.partition.repository.insert.PartitionDefaultInsertJdbcRepository;
import com.example.myauthapp.partition.repository.insert.PartitionStagingInsertJdbcRepository;
import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionRecoveryPartitionError;
import com.example.myauthapp.partition.enums.type.PartitionTableType;

@Service
public class PartitionRecoveryMoveJdbcService {

    private final PartitionDefaultCountJdbcRepository partitionDefaultCountJdbcRepository;
    private final PartitionStagingCountJdbcRepository partitionStagingCountJdbcRepository;
    private final PartitionDefaultDeleteJdbcRepository partitionDefaultDeleteJdbcRepository;
    private final PartitionStagingDeleteJdbcRepository partitionStagingDeleteJdbcRepository;
    private final PartitionDebugCountJdbcService partitionDebugCountJdbcService;
    private final PartitionDefaultPartitionRecordSourceDiscoveryJdbcRepository
        partitionDefaultPartitionRecordSourceDiscoveryJdbcRepository;
    private final PartitionStagingPartitionRecordSourceDiscoveryJdbcRepository partitionStagingPartitionRecordSourceDiscoveryJdbcRepository;
    private final PartitionDefaultInsertJdbcRepository
        partitionDefaultInsertJdbcRepository;
    private final PartitionStagingInsertJdbcRepository partitionStagingInsertJdbcRepository;
    

    public PartitionRecoveryMoveJdbcService(
        PartitionDefaultCountJdbcRepository partitionDefaultCountJdbcRepository,
        PartitionStagingCountJdbcRepository partitionStagingCountJdbcRepository,
        PartitionDefaultDeleteJdbcRepository partitionDefaultDeleteJdbcRepository,
        PartitionStagingDeleteJdbcRepository partitionStagingDeleteJdbcRepository,
        PartitionDebugCountJdbcService partitionDebugCountJdbcService,
        PartitionDefaultPartitionRecordSourceDiscoveryJdbcRepository
            partitionDefaultPartitionRecordSourceDiscoveryJdbcRepository,
        PartitionStagingPartitionRecordSourceDiscoveryJdbcRepository partitionStagingPartitionRecordSourceDiscoveryJdbcRepository,
        PartitionDefaultInsertJdbcRepository
            partitionDefaultInsertJdbcRepository,
        PartitionStagingInsertJdbcRepository partitionStagingInsertJdbcRepository
    ) {
        this.partitionDefaultCountJdbcRepository = partitionDefaultCountJdbcRepository;
        this.partitionStagingCountJdbcRepository = partitionStagingCountJdbcRepository;
        this.partitionDefaultDeleteJdbcRepository = partitionDefaultDeleteJdbcRepository;
        this.partitionStagingDeleteJdbcRepository = partitionStagingDeleteJdbcRepository;
        this.partitionDebugCountJdbcService = partitionDebugCountJdbcService;
        this.partitionDefaultPartitionRecordSourceDiscoveryJdbcRepository =
            partitionDefaultPartitionRecordSourceDiscoveryJdbcRepository;
        this.partitionStagingPartitionRecordSourceDiscoveryJdbcRepository = partitionStagingPartitionRecordSourceDiscoveryJdbcRepository;
        this.partitionDefaultInsertJdbcRepository =
            partitionDefaultInsertJdbcRepository;
        this.partitionStagingInsertJdbcRepository = partitionStagingInsertJdbcRepository;
    }

    public long moveDefaultToStaging(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionRecoveryTableExpressionProviderContext partitionRecoveryTableExpressionProviderContext,
        PartitionRangeDTO partitionRangeDTO,
        UUID publicPartitionOperationId
    ) { 
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );

        Objects.requireNonNull(
            partitionRecoveryTableExpressionProviderContext,
            "partitionRecoveryTableExpressionProviderContext must not be null"
        );

        Objects.requireNonNull(
            partitionRangeDTO,
            "partitionRangeDTO must not be null"
        );

        Objects.requireNonNull(
            publicPartitionOperationId,
            "publicPartitionOperationId must not be null"
        );

        List<PartitionRecordSource> partitionRecordSources = 
            partitionDefaultPartitionRecordSourceDiscoveryJdbcRepository
                .findDefaultPartitionPartitionRecordSources(
                    partitionTablePropertiesContext,
                    partitionRecoveryTableExpressionProviderContext,
                    partitionRangeDTO
                );

        long beforeDefaultPartitionRowCount = partitionRecordSources.size();

        long insertedStagingRowCount =
            partitionStagingInsertJdbcRepository
                .insertPartitionRecordSourcesIntoStaging(
                    publicPartitionOperationId,
                    partitionRecordSources
                );

        if (insertedStagingRowCount != beforeDefaultPartitionRowCount) {
            throw new PartitionException(
                PartitionRecoveryPartitionError.INSERT_COUNT_MISMATCH,
                PartitionTableType.STAGING.name()
            );
        }

        partitionDefaultDeleteJdbcRepository.deleteDefaultPartitionRows(
            partitionTablePropertiesContext,
            partitionRangeDTO
        );

        long afterDefaultPartitionRowCount =
            partitionDefaultCountJdbcRepository.countDefaultPartitionRowCount(
                partitionTablePropertiesContext,
                partitionRangeDTO
            );

        if (afterDefaultPartitionRowCount >= 1) {
            throw new PartitionException(
                PartitionRecoveryPartitionError
                    .DEFAULT_PARTITION_ROWS_REMAIN_AFTER_MOVING_TO_STAGING
            );
        }

        return beforeDefaultPartitionRowCount;
    }

    /**
     * 1回目:
     * DEFAULT → Recovery(A) → Partition
     *
     * 2回目:
     * Recovery(B) → Partition
     *
     * の両方を処理できる。
     */
    public long moveStagingToPartition(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionRecoveryTableExpressionProviderContext partitionRecoveryTableExpressionProviderContext,
        PartitionRangeDTO partitionRangeDTO,
        UUID publicPartitionOperationId
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );

        Objects.requireNonNull(
            partitionRecoveryTableExpressionProviderContext,
            "partitionRecoveryTableExpressionProviderContext must not be null"
        );

        Objects.requireNonNull(
            partitionRangeDTO,
            "partitionRangeDTO must not be null"
        );

        Objects.requireNonNull(
            publicPartitionOperationId,
            "publicPartitionOperationId must not be null"
        );

        List<PartitionRecordSource> partitionRecordSources =
            partitionStagingPartitionRecordSourceDiscoveryJdbcRepository
                .findStagingPartitionRecordSources(
                    partitionTablePropertiesContext,
                    publicPartitionOperationId
                );

        long beforeStagingRowCount = partitionRecordSources.size();

        long insertedTargetPartitionRowCount =
            partitionDefaultInsertJdbcRepository
                .insertPartitionRecordSourcesIntoPartition(
                    partitionRecoveryTableExpressionProviderContext,
                    partitionRangeDTO,
                    partitionRecordSources
                );

        if (insertedTargetPartitionRowCount != beforeStagingRowCount) {
            throw new PartitionException(
                PartitionRecoveryPartitionError.INSERT_COUNT_MISMATCH,
                PartitionTableType.TARGET_PARTITION.name()
            );
        }

        partitionStagingDeleteJdbcRepository.deleteStagingRows(
            publicPartitionOperationId
        );

        long afterStagingRowCount =
            partitionStagingCountJdbcRepository
                .countStagingRowCountByPublicPartitionOperationId(
                    publicPartitionOperationId
                );

        if (afterStagingRowCount >= 1) {
            throw new PartitionException(
                PartitionRecoveryPartitionError
                    .STAGING_ROWS_REMAIN_AFTER_MOVING_TO_PARTITION
            );
        }

        return beforeStagingRowCount;

    }
}