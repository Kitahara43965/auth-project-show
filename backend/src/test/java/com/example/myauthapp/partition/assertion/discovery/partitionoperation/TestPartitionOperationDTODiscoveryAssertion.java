package com.example.myauthapp.partition.assertion.discovery.partitionoperation;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.PartitionOperationDTO;
import com.example.myauthapp.partition.repository.discovery.partitionoperation
    .PartitionOperationDTODiscoveryJdbcRepository;

@Component
public class TestPartitionOperationDTODiscoveryAssertion {

    private final PartitionOperationDTODiscoveryJdbcRepository
        partitionOperationDTODiscoveryJdbcRepository;

    public TestPartitionOperationDTODiscoveryAssertion(
        PartitionOperationDTODiscoveryJdbcRepository
            partitionOperationDTODiscoveryJdbcRepository
    ) {
        this.partitionOperationDTODiscoveryJdbcRepository =
            partitionOperationDTODiscoveryJdbcRepository;
    }

    public PartitionOperationDTO findAssertedSinglePartitionOperationDTO() {
        List<PartitionOperationDTO> partitionOperationDTOs =
            partitionOperationDTODiscoveryJdbcRepository
                .findPartitionOperationDTOs();

        assertThat(partitionOperationDTOs)
            .hasSize(1);

        return partitionOperationDTOs.get(0);
    }

}