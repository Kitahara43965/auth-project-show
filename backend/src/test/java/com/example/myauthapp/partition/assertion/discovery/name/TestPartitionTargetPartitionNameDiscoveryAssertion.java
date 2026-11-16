package com.example.myauthapp.partition.assertion.discovery.name;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.PartitionOperationDTO;
import com.example.myauthapp.partition.repository.discovery.name.PartitionTargetPartitionNameDiscoveryJdbcRepository;

@Component
public class TestPartitionTargetPartitionNameDiscoveryAssertion {

    private final PartitionTargetPartitionNameDiscoveryJdbcRepository
        partitionTargetPartitionNameDiscoveryJdbcRepository;

    public TestPartitionTargetPartitionNameDiscoveryAssertion(
        PartitionTargetPartitionNameDiscoveryJdbcRepository
            partitionTargetPartitionNameDiscoveryJdbcRepository
    ) {
        this.partitionTargetPartitionNameDiscoveryJdbcRepository =
            partitionTargetPartitionNameDiscoveryJdbcRepository;
    }

    public void assertThatTargetPartitionExists(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionRangeDTO partitionRangeDTO
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );
        Objects.requireNonNull(
            partitionRangeDTO,
            "partitionRangeDTO must not be null"
        );

        assertThat(
            partitionTargetPartitionNameDiscoveryJdbcRepository.doesTargetPartitionExist(
                partitionTablePropertiesContext,
                partitionRangeDTO
            )
        ).isTrue();
    }

    public void assertThatTargetPartitionDoesNotExist(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionRangeDTO partitionRangeDTO
    ) {
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );
        Objects.requireNonNull(
            partitionRangeDTO,
            "partitionRangeDTO must not be null"
        );

        assertThat(
            partitionTargetPartitionNameDiscoveryJdbcRepository.doesTargetPartitionExist(
                partitionTablePropertiesContext,
                partitionRangeDTO
            )
        ).isFalse();
    }
}