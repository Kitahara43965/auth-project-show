package com.example.myauthapp.partition.service.recovery;

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

@Service
public class PartitionRecoveryVerifyJdbcService {

    private final PartitionDefaultCountJdbcRepository partitionDefaultCountJdbcRepository;
    private final PartitionStagingCountJdbcRepository partitionStagingCountJdbcRepository;
    private final PartitionTargetCountJdbcRepository partitionTargetCountJdbcRepository;

    public PartitionRecoveryVerifyJdbcService(
        PartitionDefaultCountJdbcRepository partitionDefaultCountJdbcRepository,
        PartitionStagingCountJdbcRepository partitionStagingCountJdbcRepository,
        PartitionTargetCountJdbcRepository partitionTargetCountJdbcRepository
    ) {
        this.partitionDefaultCountJdbcRepository = partitionDefaultCountJdbcRepository;
        this.partitionStagingCountJdbcRepository = partitionStagingCountJdbcRepository;
        this.partitionTargetCountJdbcRepository = partitionTargetCountJdbcRepository;
    }

    /**
     * Recovery後の状態を検証する。
     *
     * DEFAULTに対象期間のデータが残っていないこと、
     * Recovery stagingにも対象期間のデータが残っていないことを確認する。
     */
    public void verifyRecoveryCompleted(
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

        String targetPartitionName =
            Objects.requireNonNull(
                partitionRangeDTO.getTargetPartitionName(),
                "targetPartitionName must not be null"
            );

        long remainingDefaultPartitionRowCount =
            partitionDefaultCountJdbcRepository.countDefaultPartitionRowCount(
                partitionTablePropertiesContext,
                partitionRangeDTO
            );

        if (remainingDefaultPartitionRowCount >= 1) {

            throw new PartitionException(
                PartitionRecoveryPartitionError.ROWS_REMAIN_IN_DEFAULT_PARTITION,
                targetPartitionName,
                remainingDefaultPartitionRowCount
            );
        }

        long remainingStagingRowCount =
            partitionStagingCountJdbcRepository.countStagingRowCountByPublicPartitionOperationId(
                publicPartitionOperationId
            );

        if(remainingStagingRowCount >= 1) {

            throw new PartitionException(
                PartitionRecoveryPartitionError.ROWS_REMAIN_IN_STAGING_TABLE,
                targetPartitionName,
                remainingStagingRowCount
            );
        }
    }
}
