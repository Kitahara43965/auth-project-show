package com.example.myauthapp.partition.assertion.deactivate;

import org.junit.jupiter.api.Assertions;
import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.repository.discovery.partitionrange
    .PartitionRangeDTODiscoveryJdbcRepository;

@Component
public class TestPartitionRangeDeactivateAssertion {

    private final PartitionRangeDTODiscoveryJdbcRepository
        partitionRangeDTODiscoveryJdbcRepository;

    public TestPartitionRangeDeactivateAssertion(
        PartitionRangeDTODiscoveryJdbcRepository
            partitionRangeDTODiscoveryJdbcRepository
    ) {
        this.partitionRangeDTODiscoveryJdbcRepository =
            partitionRangeDTODiscoveryJdbcRepository;
    }

    public void assertThatActualPartitionRangeDTOIsDeactivated(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionRangeDTO partitionRangeDTO
    ) {
        PartitionRangeDTO actualPartitionRangeDTO =
            partitionRangeDTODiscoveryJdbcRepository
                .findPartitionRangeDTOByPartitionName(
                    partitionTablePropertiesContext,
                    partitionRangeDTO.getTargetPartitionName()
                );

        Assertions.assertNotNull(
            actualPartitionRangeDTO.getDeactivatedAt(),
            "actualPartitionRangeDTO should be deactivated"
        );
    }

    public void assertThatActualPartitionRangeDTOIsNotDeactivated(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionRangeDTO partitionRangeDTO
    ) {
        PartitionRangeDTO actualPartitionRangeDTO =
            partitionRangeDTODiscoveryJdbcRepository
                .findPartitionRangeDTOByPartitionName(
                    partitionTablePropertiesContext,
                    partitionRangeDTO.getTargetPartitionName()
                );

        Assertions.assertNull(
            actualPartitionRangeDTO.getDeactivatedAt(),
            "actualPartitionRangeDTO should not be deactivated"
        );
    }
}