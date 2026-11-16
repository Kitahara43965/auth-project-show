package com.example.myauthapp.partition.service.debug;

import java.util.UUID;
import java.util.Objects;

import org.springframework.stereotype.Repository;

import com.example.myauthapp.partition.repository.discovery.count.PartitionDefaultCountJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.count.PartitionStagingCountJdbcRepository;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;

@Repository
public class PartitionDebugCountJdbcService {

    private PartitionDefaultCountJdbcRepository partitionDefaultCountJdbcRepository;
    private PartitionStagingCountJdbcRepository partitionStagingCountJdbcRepository;

    public PartitionDebugCountJdbcService(
        PartitionDefaultCountJdbcRepository partitionDefaultCountJdbcRepository,
        PartitionStagingCountJdbcRepository partitionStagingCountJdbcRepository
    ){
        this.partitionDefaultCountJdbcRepository = partitionDefaultCountJdbcRepository;
        this.partitionStagingCountJdbcRepository = partitionStagingCountJdbcRepository;
    }

    public void printRecoveryDataCounts(
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

        long defaultPartitionRowCount =
            partitionDefaultCountJdbcRepository.countDefaultPartitionRowCount(
                partitionTablePropertiesContext,
                partitionRangeDTO
            );

        long stagingRowCount =
            partitionStagingCountJdbcRepository
                .countStagingRowCountByPublicPartitionOperationId(
                    publicPartitionOperationId
                );

        System.out.println(
            "[RECOVERY DATA COUNTS] publicPartitionOperationId="
            + publicPartitionOperationId
            + ", defaultPartitionRowCount="
            + defaultPartitionRowCount
            + ", stagingRowCount="
            + stagingRowCount
        );
    }

    

}