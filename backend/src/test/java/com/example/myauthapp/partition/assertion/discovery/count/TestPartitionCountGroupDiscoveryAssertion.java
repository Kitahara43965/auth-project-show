package com.example.myauthapp.partition.assertion.discovery.discovery.count;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.PartitionOperationDTO;
import com.example.myauthapp.partition.assertion.discovery.count.TestPartitionCountDiscoveryAssertion;
import com.example.myauthapp.partition.repository.discovery.partitionoperation.PartitionOperationDTODiscoveryJdbcRepository;
import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;
import com.example.myauthapp.partition.repository.discovery.name.PartitionTargetPartitionNameDiscoveryJdbcRepository;


@Component
public class TestPartitionCountGroupDiscoveryAssertion {

    private final PartitionOperationDTODiscoveryJdbcRepository
        partitionOperationDTODiscoveryJdbcRepository;
    private final PartitionTargetPartitionNameDiscoveryJdbcRepository
        partitionTargetPartitionNameDiscoveryJdbcRepository;
    private final TestPartitionCountDiscoveryAssertion
        testPartitionCountDiscoveryAssertion;

    public TestPartitionCountGroupDiscoveryAssertion(
        PartitionOperationDTODiscoveryJdbcRepository
            partitionOperationDTODiscoveryJdbcRepository,
        PartitionTargetPartitionNameDiscoveryJdbcRepository
            partitionTargetPartitionNameDiscoveryJdbcRepository,
        TestPartitionCountDiscoveryAssertion
            testPartitionCountDiscoveryAssertion
        
    ) {
        this.partitionOperationDTODiscoveryJdbcRepository =
            partitionOperationDTODiscoveryJdbcRepository;
        this.partitionTargetPartitionNameDiscoveryJdbcRepository =
            partitionTargetPartitionNameDiscoveryJdbcRepository;
        this.testPartitionCountDiscoveryAssertion =
            testPartitionCountDiscoveryAssertion;
    }

    public void assertThatPartitionRecoveryIsCompleted(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionRangeDTO partitionRangeDTO,
        int maxInsertedRecordCount
    ) {


        testPartitionCountDiscoveryAssertion.assertDefaultCount(
            partitionTablePropertiesContext,
            partitionRangeDTO,
            0
        );

        testPartitionCountDiscoveryAssertion.assertPartitionCount(
            partitionTablePropertiesContext,
            partitionRangeDTO,
            maxInsertedRecordCount
        );

        testPartitionCountDiscoveryAssertion.assertStagingCount(
            partitionTablePropertiesContext,
            0
        );
        
    }

    public void assertThatPartitionRecoveryIsCompletedByPublicPartitionOperationId(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionRangeDTO partitionRangeDTO,
        UUID publicPartitionOperationId,
        int maxInsertedRecordCount
    ) {
        assertThatPartitionRecoveryIsCompleted(
            partitionTablePropertiesContext,
            partitionRangeDTO,
            maxInsertedRecordCount
        );

        PartitionOperationDTO partitionOperationDTO =
            partitionOperationDTODiscoveryJdbcRepository
                .findPartitionOperationDTOByPublicPartitionOperationId(
                    publicPartitionOperationId
                );

        assertThat(
            partitionOperationDTO.getPartitionOperationStatus()
        ).isEqualTo(
            PartitionOperationStatus.COMPLETED
        );
    }


    public void assertThatPartitionRecoveryAndCleanupAreCompleted(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionRangeDTO partitionRangeDTO,
        int maxInsertedRecordCount
    ) {
        boolean doesTargetPartitionExist =
            partitionTargetPartitionNameDiscoveryJdbcRepository
                .doesTargetPartitionExist(
                    partitionTablePropertiesContext,
                    partitionRangeDTO
                );

        if (doesTargetPartitionExist) {
            testPartitionCountDiscoveryAssertion.assertPartitionCount(
                partitionTablePropertiesContext,
                partitionRangeDTO,
                maxInsertedRecordCount
            );
        }

        testPartitionCountDiscoveryAssertion.assertDefaultCount(
            partitionTablePropertiesContext,
            partitionRangeDTO,
            0
        );

        testPartitionCountDiscoveryAssertion.assertStagingCount(
            partitionTablePropertiesContext,
            0
        );

    }


    public void assertThatPartitionRecoveryAndCleanupAreCompletedByPublicPartitionOperationId(
        PartitionTablePropertiesContext partitionTablePropertiesContext,
        PartitionRangeDTO partitionRangeDTO,
        int maxInsertedRecordCount,
        UUID publicPartitionOperationId
    ) {
        assertThatPartitionRecoveryAndCleanupAreCompleted(
            partitionTablePropertiesContext,
            partitionRangeDTO,
            maxInsertedRecordCount
        );

        PartitionOperationDTO partitionOperationDTO =
            partitionOperationDTODiscoveryJdbcRepository
                .findPartitionOperationDTOByPublicPartitionOperationId(
                    publicPartitionOperationId
                );

        assertThat(
            partitionOperationDTO.getPartitionOperationStatus()
        ).isEqualTo(
            PartitionOperationStatus.COMPLETED
        );

    }

}