package com.example.myauthapp.partition.service.recoveryretry;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.PartitionRecordSource;
import com.example.myauthapp.partition.dto.PartitionRecoverPartitionResult;
import com.example.myauthapp.partition.repository.discovery.source.PartitionDefaultPartitionRecordSourceDiscoveryJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.source.PartitionStagingPartitionRecordSourceDiscoveryJdbcRepository;
import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionRecoveryPartitionError;

@Service
public class PartitionRecoveryRetryValidationJdbcService {

    private final PartitionDefaultPartitionRecordSourceDiscoveryJdbcRepository
        partitionDefaultPartitionRecordSourceDiscoveryJdbcRepository;

    private final PartitionStagingPartitionRecordSourceDiscoveryJdbcRepository
        partitionStagingPartitionRecordSourceDiscoveryJdbcRepository;

    public PartitionRecoveryRetryValidationJdbcService(
        PartitionDefaultPartitionRecordSourceDiscoveryJdbcRepository
            partitionDefaultPartitionRecordSourceDiscoveryJdbcRepository,
        PartitionStagingPartitionRecordSourceDiscoveryJdbcRepository
            partitionStagingPartitionRecordSourceDiscoveryJdbcRepository
    ) {
        this.partitionDefaultPartitionRecordSourceDiscoveryJdbcRepository =
            partitionDefaultPartitionRecordSourceDiscoveryJdbcRepository;

        this.partitionStagingPartitionRecordSourceDiscoveryJdbcRepository =
            partitionStagingPartitionRecordSourceDiscoveryJdbcRepository;
    }

    public PartitionRecoverPartitionResult validateRecoveryRetrySourceConsistency(
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
            "partitionRecoveryTableExpressionProviderContext "
                + "must not be null"
        );

        Objects.requireNonNull(
            partitionRangeDTO,
            "partitionRangeDTO must not be null"
        );

        Objects.requireNonNull(
            publicPartitionOperationId,
            "publicPartitionOperationId must not be null"
        );
        

        List<PartitionRecordSource> 
            defaultPartitionPartitionRecordSources =
                partitionDefaultPartitionRecordSourceDiscoveryJdbcRepository
                    .findDefaultPartitionPartitionRecordSources(
                        partitionTablePropertiesContext,
                        partitionRecoveryTableExpressionProviderContext,
                        partitionRangeDTO
                    );

        List<PartitionRecordSource> stagingPartitionRecordSources =
            partitionStagingPartitionRecordSourceDiscoveryJdbcRepository
                .findStagingPartitionRecordSources(
                    partitionTablePropertiesContext,
                    publicPartitionOperationId
                );

        long defaultPartitionRowCount = 
            defaultPartitionPartitionRecordSources.size();
        long stagingRowCount = stagingPartitionRecordSources.size();

        if (defaultPartitionRowCount != stagingRowCount) {

            throw new PartitionException(
                PartitionRecoveryPartitionError.RECOVERY_SOURCE_COUNT_MISMATCH,
                publicPartitionOperationId,
                defaultPartitionRowCount,
                stagingRowCount
            );
        }

        for (int i = 1; i <= defaultPartitionRowCount; i++) {
            PartitionRecordSource stagingPartitionPartitionRecordSource =
                defaultPartitionPartitionRecordSources.get((i-1));

            PartitionRecordSource stagingPartitionRecordSource =
                stagingPartitionRecordSources.get((i-1));

            if (!Objects.equals(
                stagingPartitionPartitionRecordSource.getRecordId(),
                stagingPartitionRecordSource.getRecordId())
            ) {

                throw new PartitionException(
                    PartitionRecoveryPartitionError.RECOVERY_SOURCE_RECORD_ID_MISMATCH,
                    publicPartitionOperationId,
                    stagingPartitionPartitionRecordSource.getRecordId(),
                    stagingPartitionRecordSource.getRecordId()
                );
            }

            if (!Objects.equals(
                stagingPartitionPartitionRecordSource.getRecordData(),
                stagingPartitionRecordSource.getRecordData())
            ) {
                throw new PartitionException(
                    PartitionRecoveryPartitionError.RECOVERY_SOURCE_RECORD_DATA_MISMATCH,
                    publicPartitionOperationId,
                    stagingPartitionPartitionRecordSource.getRecordId()
                );
            }
        }
        
        return new PartitionRecoverPartitionResult(
            publicPartitionOperationId,
            defaultPartitionRowCount,
            stagingRowCount
        );
    }
}
