package com.example.myauthapp.partition.fixture.service.cleanup;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.assertion.discovery.name.TestPartitionTargetPartitionNameDiscoveryAssertion;
import com.example.myauthapp.partition.context.PartitionOperationPropertiesContext;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.repository.create.PartitionTargetCreateJdbcRepository;
import com.example.myauthapp.partition.service.cleanup.PartitionCleanupService;
import com.example.myauthapp.partition.service.partitionmetadata.PartitionMetadataService;
import com.example.myauthapp.partition.assertion.deactivate.
    TestPartitionRangeDeactivateAssertion;

@Component
public class TestPartitionCleanupServiceFixture {

    private enum PartitionCleanupType {
        PARTITION_CLEANUP_BEFORE_SHOULD_DROP_OLD_PARTITIONS,
        PARTITION_CLEANUP_BEFORE_SHOULD_KEEP_PARTITION_AT_BOUNDARY
    }

    private final PartitionMetadataService partitionMetadataService;
    private final PartitionTargetCreateJdbcRepository partitionTargetCreateJdbcRepository;
    private final PartitionCleanupService partitionCleanupService;
    private final TestPartitionTargetPartitionNameDiscoveryAssertion
        testPartitionTargetPartitionNameDiscoveryAssertion;
    private final TestPartitionRangeDeactivateAssertion
        testPartitionRangeDeactivateAssertion;

    public TestPartitionCleanupServiceFixture(
        PartitionMetadataService partitionMetadataService,
        PartitionTargetCreateJdbcRepository partitionTargetCreateJdbcRepository,
        PartitionCleanupService partitionCleanupService,
        TestPartitionTargetPartitionNameDiscoveryAssertion
            testPartitionTargetPartitionNameDiscoveryAssertion,
        TestPartitionRangeDeactivateAssertion
            testPartitionRangeDeactivateAssertion
    ) {
        this.partitionMetadataService = partitionMetadataService;
        this.partitionTargetCreateJdbcRepository = partitionTargetCreateJdbcRepository;
        this.partitionCleanupService = partitionCleanupService;
        this.testPartitionTargetPartitionNameDiscoveryAssertion =
            testPartitionTargetPartitionNameDiscoveryAssertion;
        this.testPartitionRangeDeactivateAssertion =
            testPartitionRangeDeactivateAssertion;
    }

    private void onTestPartitionCleanup(
        PartitionContextGroup partitionContextGroup,
        PartitionCleanupType partitionCleanupType
    ) {
        PartitionTablePropertiesContext partitionTablePropertiesContext =
            partitionContextGroup.getPartitionTablePropertiesContext();
        PartitionOperationPropertiesContext partitionOperationPropertiesContext =
            partitionContextGroup.getPartitionOperationPropertiesContext();
        LocalDateTime now = null;
        LocalDateTime oldPartitionStartTime = null;
        PartitionRangeDTO oldPartitionRangeDTO = null;
        LocalDateTime boundaryPartitionStartTime = null;
        PartitionRangeDTO boundaryPartitionRangeDTO = null;
        LocalDateTime newPartitionStartTime = null;
        PartitionRangeDTO newPartitionRangeDTO = null;

        if(partitionCleanupType == 
            PartitionCleanupType.PARTITION_CLEANUP_BEFORE_SHOULD_KEEP_PARTITION_AT_BOUNDARY
        ){
            now =
                LocalDateTime.now()
                    .withDayOfMonth(1)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);
            oldPartitionStartTime =
                now.minusMonths(3).withDayOfMonth(1);
            boundaryPartitionStartTime = 
                now.minusMonths(1).withDayOfMonth(1);
            newPartitionStartTime =
                now.withDayOfMonth(1);
        }else{
            now = LocalDateTime.now();
            oldPartitionStartTime =
                now.minusMonths(2).withDayOfMonth(1);
            boundaryPartitionStartTime = 
                now.minusMonths(1).withDayOfMonth(1);
            newPartitionStartTime =
                now.withDayOfMonth(1);
        }

        oldPartitionRangeDTO =
            partitionMetadataService.ensurePartition(
                partitionTablePropertiesContext,
                partitionOperationPropertiesContext,
                oldPartitionStartTime
            );

        boundaryPartitionRangeDTO =
            partitionMetadataService.ensurePartition(
                partitionTablePropertiesContext,
                partitionOperationPropertiesContext,
                boundaryPartitionStartTime
            );

        newPartitionRangeDTO =
            partitionMetadataService.ensurePartition(
                partitionTablePropertiesContext,
                partitionOperationPropertiesContext,
                newPartitionStartTime
            );

        partitionTargetCreateJdbcRepository.createPartition(
            partitionTablePropertiesContext,
            oldPartitionRangeDTO,
            UUID.randomUUID()
        );
        partitionTargetCreateJdbcRepository.createPartition(
            partitionTablePropertiesContext,
            newPartitionRangeDTO,
            UUID.randomUUID()
        );
        if(partitionCleanupType == 
            PartitionCleanupType.PARTITION_CLEANUP_BEFORE_SHOULD_KEEP_PARTITION_AT_BOUNDARY
        ){
            partitionTargetCreateJdbcRepository.createPartition(
                partitionTablePropertiesContext,
                boundaryPartitionRangeDTO,
                UUID.randomUUID()
            );
        }//partitionCleanupType
        
        partitionCleanupService.cleanupPartitionBefore(
            partitionTablePropertiesContext,
            boundaryPartitionStartTime
        );

        testPartitionTargetPartitionNameDiscoveryAssertion
            .assertThatTargetPartitionDoesNotExist(
                partitionTablePropertiesContext,
                oldPartitionRangeDTO
            );

        testPartitionRangeDeactivateAssertion
            .assertThatActualPartitionRangeDTOIsDeactivated(
                partitionTablePropertiesContext,
                oldPartitionRangeDTO
            );

        testPartitionTargetPartitionNameDiscoveryAssertion
            .assertThatTargetPartitionExists(
                partitionTablePropertiesContext,
                newPartitionRangeDTO
            );

        if (partitionCleanupType == 
            PartitionCleanupType.PARTITION_CLEANUP_BEFORE_SHOULD_KEEP_PARTITION_AT_BOUNDARY
        ) {
            testPartitionTargetPartitionNameDiscoveryAssertion
                .assertThatTargetPartitionExists(
                    partitionTablePropertiesContext,
                    boundaryPartitionRangeDTO
                );

            testPartitionRangeDeactivateAssertion
                .assertThatActualPartitionRangeDTOIsNotDeactivated(
                    partitionTablePropertiesContext,
                    boundaryPartitionRangeDTO
            );
        }
    }

    public void onCleanupPartitionBeforeShouldDropOldPartitions(
        PartitionContextGroup partitionContextGroup
    ){
        onTestPartitionCleanup(
            partitionContextGroup,
            PartitionCleanupType.PARTITION_CLEANUP_BEFORE_SHOULD_DROP_OLD_PARTITIONS
        );
    }

    public void onCleanupPartitionBeforeShouldKeepPartitionAtBoundary(
        PartitionContextGroup partitionContextGroup
    ) {
        onTestPartitionCleanup(
            partitionContextGroup,
            PartitionCleanupType.PARTITION_CLEANUP_BEFORE_SHOULD_KEEP_PARTITION_AT_BOUNDARY
        );
    }

}