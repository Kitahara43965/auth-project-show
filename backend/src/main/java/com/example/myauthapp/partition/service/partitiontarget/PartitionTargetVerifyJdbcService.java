package com.example.myauthapp.partition.service.partitiontarget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.PartitionRecordSource;
import com.example.myauthapp.partition.dto.PartitionRecordSourceKey;
import com.example.myauthapp.partition.repository.discovery.source.PartitionStagingPartitionRecordSourceDiscoveryJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.source.PartitionTargetPartitionRecordSourceDiscoveryJdbcRepository;
import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionRecoveryPartitionError;

@Service
public class PartitionTargetVerifyJdbcService {

    private final PartitionStagingPartitionRecordSourceDiscoveryJdbcRepository
        partitionStagingPartitionRecordSourceDiscoveryJdbcRepository;

    private final PartitionTargetPartitionRecordSourceDiscoveryJdbcRepository
        partitionTargetPartitionRecordSourceDiscoveryJdbcRepository;

    public PartitionTargetVerifyJdbcService(
        PartitionStagingPartitionRecordSourceDiscoveryJdbcRepository
            partitionStagingPartitionRecordSourceDiscoveryJdbcRepository,
        PartitionTargetPartitionRecordSourceDiscoveryJdbcRepository
            partitionTargetPartitionRecordSourceDiscoveryJdbcRepository
    ) {
        this.partitionStagingPartitionRecordSourceDiscoveryJdbcRepository =
            partitionStagingPartitionRecordSourceDiscoveryJdbcRepository;

        this.partitionTargetPartitionRecordSourceDiscoveryJdbcRepository =
            partitionTargetPartitionRecordSourceDiscoveryJdbcRepository;
    }

    public void verifyTargetPartitionRecords(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionRecoveryTableExpressionProviderContext
            partitionRecoveryTableExpressionProviderContext,
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

        List<PartitionRecordSource> stagingPartitionRecordSources =
            partitionStagingPartitionRecordSourceDiscoveryJdbcRepository
                .findStagingPartitionRecordSources(
                    partitionTablePropertiesContext,
                    publicPartitionOperationId
                );

        List<PartitionRecordSource> targetPartitionRecordSources =
            partitionTargetPartitionRecordSourceDiscoveryJdbcRepository
                .findTargetPartitionRecordSources(
                    partitionTablePropertiesContext,
                    partitionRecoveryTableExpressionProviderContext,
                    partitionRangeDTO
                );

        if (targetPartitionRecordSources.size() != stagingPartitionRecordSources.size()) {
            throw new PartitionException(
                PartitionRecoveryPartitionError
                    .TARGET_PARTITION_ROW_COUNTS_MISMATCH
            );
        }

        Map<PartitionRecordSourceKey, PartitionRecordSource>
            targetPartitionRecordSourceMap = new HashMap<>();

        for (PartitionRecordSource targetPartitionRecordSource : targetPartitionRecordSources) {
            PartitionRecordSourceKey key = new PartitionRecordSourceKey(
                targetPartitionRecordSource.getRecordId(),
                targetPartitionRecordSource.getPartitionLocalDateTime()
            );

            PartitionRecordSource duplicatedPartitionRecordSource =
                targetPartitionRecordSourceMap.put(
                    key,
                    targetPartitionRecordSource
                );

            if (duplicatedPartitionRecordSource != null) {
                throw new PartitionException(
                    PartitionRecoveryPartitionError
                        .TARGET_PARTITION_ROW_COUNTS_MISMATCH
                );
            }
        }

        for (PartitionRecordSource stagingPartitionRecordSource : stagingPartitionRecordSources) {
            PartitionRecordSourceKey partitionRecordSourceKey = new PartitionRecordSourceKey(
                stagingPartitionRecordSource.getRecordId(),
                stagingPartitionRecordSource.getPartitionLocalDateTime()
            );

            PartitionRecordSource targetPartitionRecordSource =
                targetPartitionRecordSourceMap.get(partitionRecordSourceKey);

            if (targetPartitionRecordSource == null
                || !targetPartitionRecordSource.getRecordData().equals(
                    stagingPartitionRecordSource.getRecordData()
                )) {
                throw new PartitionException(
                    PartitionRecoveryPartitionError
                        .TARGET_PARTITION_ROW_COUNTS_MISMATCH
                );
            }
        }
    }
}