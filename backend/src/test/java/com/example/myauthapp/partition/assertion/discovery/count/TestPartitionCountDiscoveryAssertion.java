package com.example.myauthapp.partition.assertion.discovery.count;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.repository.discovery.count.PartitionDefaultCountJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.count.PartitionTargetCountJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.count.PartitionStagingCountJdbcRepository;

@Component
public class TestPartitionCountDiscoveryAssertion {

    private final PartitionDefaultCountJdbcRepository partitionDefaultCountJdbcRepository;
    private final PartitionTargetCountJdbcRepository partitionTargetCountJdbcRepository;
    private final PartitionStagingCountJdbcRepository partitionStagingCountJdbcRepository;

    public TestPartitionCountDiscoveryAssertion(
        PartitionDefaultCountJdbcRepository partitionDefaultCountJdbcRepository,
        PartitionTargetCountJdbcRepository partitionTargetCountJdbcRepository,
        PartitionStagingCountJdbcRepository partitionStagingCountJdbcRepository
    ) {
        this.partitionDefaultCountJdbcRepository = partitionDefaultCountJdbcRepository;
        this.partitionTargetCountJdbcRepository = partitionTargetCountJdbcRepository;
        this.partitionStagingCountJdbcRepository =
            partitionStagingCountJdbcRepository;
    }

    public void assertDefaultCount(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionRangeDTO partitionRangeDTO,
        long expectedCount
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
            expectedCount,
            "expectedCount must not be null"
        );

        long actualCount =
            partitionDefaultCountJdbcRepository
                .countDefaultPartitionRowCount(
                    partitionTablePropertiesContext,
                    partitionRangeDTO
                );

        assertThat(actualCount).isNotNull().isEqualTo(expectedCount);
    }

    public void assertPartitionCount(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionRangeDTO partitionRangeDTO,
        long expectedCount
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
            expectedCount,
            "expectedCount must not be null"
        );
        
        long actualCount =
            partitionTargetCountJdbcRepository
                .countTargetPartitionRowCount(
                    partitionTablePropertiesContext,
                    partitionRangeDTO
                );

        assertThat(actualCount).isNotNull().isEqualTo(expectedCount);
    }

    public void assertStagingCount(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        long expectedCount
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );
        Objects.requireNonNull(
            expectedCount,
            "expectedCount must not be null"
        );

        long actualCount =
            partitionStagingCountJdbcRepository
                .countStagingRowCount();

        assertThat(actualCount).isNotNull().isEqualTo(expectedCount);
    }

    public void assertStagingCountByPublicPartitionOperationId(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        UUID publicPartitionOperationId,
        long expectedCount
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );
        Objects.requireNonNull(
            publicPartitionOperationId,
            "publicPartitionOperationId must not be null"
        );
        Objects.requireNonNull(
            expectedCount,
            "expectedCount must not be null"
        );

        long actualCount =
            partitionStagingCountJdbcRepository
                .countStagingRowCountByPublicPartitionOperationId(
                    publicPartitionOperationId
                );

        assertThat(actualCount).isNotNull().isEqualTo(expectedCount);
    }

    
}