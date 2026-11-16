
package com.example.myauthapp.partition.service.recovery;

import java.util.UUID;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.repository.create.PartitionTargetCreateJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.name.PartitionTargetPartitionNameDiscoveryJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.partitionrange
    .PartitionRangeDTODiscoveryJdbcRepository;
import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionRecoveryPartitionError;

@Service
public class PartitionRecoveryRestoreJdbcService {

    private final PartitionTargetPartitionNameDiscoveryJdbcRepository
        partitionTargetPartitionNameDiscoveryJdbcRepository;

    private final PartitionTargetCreateJdbcRepository
        partitionTargetCreateJdbcRepository;

    private final PartitionRecoveryMoveJdbcService
        partitionRecoveryMoveJdbcService;

    private final PartitionRecoveryVerifyJdbcService
        partitionRecoveryVerifyJdbcService;

    private final PartitionRangeDTODiscoveryJdbcRepository
        partitionRangeDTODiscoveryJdbcRepository;

    public PartitionRecoveryRestoreJdbcService(
        PartitionTargetPartitionNameDiscoveryJdbcRepository partitionTargetPartitionNameDiscoveryJdbcRepository,
        PartitionTargetCreateJdbcRepository partitionTargetCreateJdbcRepository,
        PartitionRecoveryMoveJdbcService partitionRecoveryMoveJdbcService,
        PartitionRecoveryVerifyJdbcService partitionRecoveryVerifyJdbcService,
        PartitionRangeDTODiscoveryJdbcRepository partitionRangeDTODiscoveryJdbcRepository
    ) {
        this.partitionTargetPartitionNameDiscoveryJdbcRepository =
            partitionTargetPartitionNameDiscoveryJdbcRepository;

        this.partitionTargetCreateJdbcRepository =
            partitionTargetCreateJdbcRepository;

        this.partitionRecoveryMoveJdbcService =
            partitionRecoveryMoveJdbcService;

        this.partitionRecoveryVerifyJdbcService =
            partitionRecoveryVerifyJdbcService;

        this.partitionRangeDTODiscoveryJdbcRepository =
            partitionRangeDTODiscoveryJdbcRepository;
    }

    public void restorePartition(
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

        // PartitionRangeをロック取得後に再取得
        PartitionRangeDTO actualPartitionRangeDTO =
            partitionRangeDTODiscoveryJdbcRepository
                .findPartitionRangeDTOByPartitionName(
                    partitionTablePropertiesContext,
                    partitionRangeDTO.getTargetPartitionName()
                );

        // PartitionRangeが存在しない、または無効化済みならRecovery対象外
        if (actualPartitionRangeDTO == null || actualPartitionRangeDTO.getDeactivatedAt() != null) {
            throw new PartitionException(
                PartitionRecoveryPartitionError
                    .PARTITION_RANGE_IS_ALREADY_DEACTIVATED
            );
        }

        // 対象Partitionの存在確認
        boolean doesTargetPartitionExist =
            partitionTargetPartitionNameDiscoveryJdbcRepository
                .doesTargetPartitionExist(
                    partitionTablePropertiesContext,
                    actualPartitionRangeDTO
                );

        // 対象Partitionが存在しない場合は作成
        if (!doesTargetPartitionExist) {
            partitionTargetCreateJdbcRepository.createPartition(
                partitionTablePropertiesContext,
                actualPartitionRangeDTO,
                publicPartitionOperationId
            );
        }

        // Stagingから対象Partitionへデータを移動
        partitionRecoveryMoveJdbcService.moveStagingToPartition(
            partitionTablePropertiesContext,
            partitionRecoveryTableExpressionProviderContext,
            actualPartitionRangeDTO,
            publicPartitionOperationId
        );

        // Recovery完了検証
        partitionRecoveryVerifyJdbcService.verifyRecoveryCompleted(
            partitionTablePropertiesContext,
            actualPartitionRangeDTO,
            publicPartitionOperationId
        );
    }
}
