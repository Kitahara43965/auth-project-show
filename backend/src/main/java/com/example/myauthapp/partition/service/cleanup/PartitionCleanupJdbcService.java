package com.example.myauthapp.partition.service.cleanup;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.error.PartitionRecoveryPartitionError;
import com.example.myauthapp.partition.repository.cleanup.PartitionTargetCleanupJdbcRepository;
import com.example.myauthapp.partition.repository.deactivate.PartitionRangeDeactivateJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.partitionrange.PartitionRangeDTODiscoveryJdbcRepository;
import com.example.myauthapp.partition.repository.lock.PartitionTargetLockJdbcRepository;
import com.example.myauthapp.web.partition.exception.PartitionException;


@Service
public class PartitionCleanupJdbcService {

    private final PartitionTargetLockJdbcRepository partitionTargetLockJdbcRepository;
    private final PartitionTargetCleanupJdbcRepository partitionTargetCleanupJdbcRepository;
    private final PartitionRangeDeactivateJdbcRepository partitionRangeDeactivateJdbcRepository;
    private final PartitionRangeDTODiscoveryJdbcRepository partitionRangeDTODiscoveryJdbcRepository;

    public PartitionCleanupJdbcService(
        PartitionTargetLockJdbcRepository partitionTargetLockJdbcRepository,
        PartitionTargetCleanupJdbcRepository partitionTargetCleanupJdbcRepository,
        PartitionRangeDeactivateJdbcRepository partitionRangeDeactivateJdbcRepository,
        PartitionRangeDTODiscoveryJdbcRepository partitionRangeDTODiscoveryJdbcRepository
    ) {
        this.partitionTargetLockJdbcRepository = partitionTargetLockJdbcRepository;
        this.partitionTargetCleanupJdbcRepository = partitionTargetCleanupJdbcRepository;
        this.partitionRangeDeactivateJdbcRepository = partitionRangeDeactivateJdbcRepository;
        this.partitionRangeDTODiscoveryJdbcRepository = partitionRangeDTODiscoveryJdbcRepository;
    }

    
    public void cleanupPartition(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionRangeDTO partitionRangeDTO
    ) {

        // ① Advisory Lock取得
        partitionTargetLockJdbcRepository.acquirePartitionLock(
            partitionTablePropertiesContext,
            partitionRangeDTO
        );

        // ② Lock取得後に最新のPartitionRangeを取得
        PartitionRangeDTO actualPartitionRangeDTO =
            partitionRangeDTODiscoveryJdbcRepository
                .findPartitionRangeDTOByPartitionName(
                    partitionTablePropertiesContext,
                    partitionRangeDTO.getTargetPartitionName()
                );

        if (actualPartitionRangeDTO == null) {
            return;
        }

        // ③ 未無効化の場合だけ無効化
        if (actualPartitionRangeDTO.getDeactivatedAt() == null) {

            int updatedRowCount =
                partitionRangeDeactivateJdbcRepository.deactivatePartitionRange(
                    actualPartitionRangeDTO,
                    LocalDateTime.now()
                );

            if (updatedRowCount >= 2) {
                throw new PartitionException(
                    PartitionRecoveryPartitionError
                        .MULTIPLE_PARTITION_RANGE_RECORDS_ARE_UPDATED
                );
            }

            if (updatedRowCount == 0) {

                // 最新状態を再取得
                actualPartitionRangeDTO =
                    partitionRangeDTODiscoveryJdbcRepository
                        .findPartitionRangeDTOByPartitionName(
                            partitionTablePropertiesContext,
                            partitionRangeDTO.getTargetPartitionName()
                        );

                if (actualPartitionRangeDTO == null
                    || actualPartitionRangeDTO.getDeactivatedAt() == null
                ) {
                    throw new PartitionException(
                        PartitionRecoveryPartitionError
                            .NO_PARTITION_RANGE_DEACTIVATION_TARGETS_ARE_FOUND
                    );
                }
            }
            
        }

        // ④ 無効化済みを確認してDROP
        partitionTargetCleanupJdbcRepository.dropPartition(
            actualPartitionRangeDTO
        );
    }

}
