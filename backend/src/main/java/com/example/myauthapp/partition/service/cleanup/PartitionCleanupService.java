package com.example.myauthapp.partition.service.cleanup;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.error.PartitionRecoveryPartitionError;
import com.example.myauthapp.partition.repository.cleanup.PartitionTargetCleanupJdbcRepository;
import com.example.myauthapp.partition.repository.deactivate.PartitionRangeDeactivateJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.name.PartitionTargetPartitionNameDiscoveryJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.partitionrange.PartitionRangeDTODiscoveryJdbcRepository;
import com.example.myauthapp.web.partition.exception.PartitionException;

@Service
public class PartitionCleanupService {

    private final PartitionTargetCleanupJdbcRepository partitionTargetCleanupJdbcRepository;
    private final PartitionTargetPartitionNameDiscoveryJdbcRepository partitionTargetPartitionNameDiscoveryJdbcRepository;
    private final PartitionRangeDeactivateJdbcRepository partitionRangeDeactivateJdbcRepository;
    private final PartitionRangeDTODiscoveryJdbcRepository partitionRangeDTODiscoveryJdbcRepository;
    private final PartitionCleanupJdbcService partitionCleanupJdbcService;

    public PartitionCleanupService(
        PartitionTargetCleanupJdbcRepository partitionTargetCleanupJdbcRepository,
        PartitionTargetPartitionNameDiscoveryJdbcRepository partitionTargetPartitionNameDiscoveryJdbcRepository,
        PartitionRangeDeactivateJdbcRepository partitionRangeDeactivateJdbcRepository,
        PartitionRangeDTODiscoveryJdbcRepository partitionRangeDTODiscoveryJdbcRepository,
        PartitionCleanupJdbcService partitionCleanupJdbcService
    ) {
        this.partitionTargetCleanupJdbcRepository = partitionTargetCleanupJdbcRepository;
        this.partitionTargetPartitionNameDiscoveryJdbcRepository = partitionTargetPartitionNameDiscoveryJdbcRepository;
        this.partitionRangeDeactivateJdbcRepository = partitionRangeDeactivateJdbcRepository;
        this.partitionRangeDTODiscoveryJdbcRepository = partitionRangeDTODiscoveryJdbcRepository;
        this.partitionCleanupJdbcService = partitionCleanupJdbcService;
    }

    @Transactional(
        value = "auditTransactionManager",
        rollbackFor = Exception.class
    )
    public void cleanupPartitionBefore(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        LocalDateTime localDateTimeBefore
    ) {
        List<String> targetPartitionNames =
            partitionTargetPartitionNameDiscoveryJdbcRepository.findAllTargetPartitionNames(
                partitionTablePropertiesContext
            );

        for (String targetPartitionName : targetPartitionNames) {

            PartitionRangeDTO partitionRangeDTO =
                partitionRangeDTODiscoveryJdbcRepository
                    .findPartitionRangeDTOByPartitionName(
                        partitionTablePropertiesContext,
                        targetPartitionName
                    );

            if (partitionRangeDTO != null) {

                LocalDateTime rangeStartAt = partitionRangeDTO.getRangeStartAt();

                if (rangeStartAt.isBefore(localDateTimeBefore)) {

                    partitionCleanupJdbcService.cleanupPartition(
                        partitionTablePropertiesContext,
                        partitionRangeDTO
                    );
                }

            }//partitionRangeDTO&null
        }
    }
}