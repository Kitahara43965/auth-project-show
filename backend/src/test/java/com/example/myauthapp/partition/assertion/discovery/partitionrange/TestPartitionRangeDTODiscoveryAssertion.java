package com.example.myauthapp.partition.assertion.discovery.partitionrange;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.repository.discovery.partitionrange
    .PartitionRangeDTODiscoveryJdbcRepository;

@Component
public class TestPartitionRangeDTODiscoveryAssertion {

    private final PartitionRangeDTODiscoveryJdbcRepository
        partitionRangeDTODiscoveryJdbcRepository;

    public TestPartitionRangeDTODiscoveryAssertion(
        PartitionRangeDTODiscoveryJdbcRepository
            partitionRangeDTODiscoveryJdbcRepository
    ) {
        this.partitionRangeDTODiscoveryJdbcRepository =
            partitionRangeDTODiscoveryJdbcRepository;
    }

    public PartitionRangeDTO getAssertedSinglePartitionRangeDTOById(
        Long id
    ){
        PartitionRangeDTO actualPartitionRangeDTO =
            partitionRangeDTODiscoveryJdbcRepository
                .findPartitionRangeDTOById(id);

        assertThat(
            actualPartitionRangeDTO
        ).isNotNull();

        return(actualPartitionRangeDTO);
    }

    public void assertThatSinglePartitionRangeDTOExistsById(Long id) {
        getAssertedSinglePartitionRangeDTOById(id);
    }

    public void assertThatSinglePartitionRangeDTODoesNotExistById(Long id) {
        Optional<PartitionRangeDTO> partitionRangeDTO =
            partitionRangeDTODiscoveryJdbcRepository
                .findOptionalPartitionRangeDTOById(id);

        assertThat(partitionRangeDTO)
            .isEmpty();
    }



}