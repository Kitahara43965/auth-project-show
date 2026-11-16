package com.example.myauthapp.partition.fixture.repository.create;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.context.PartitionOperationPropertiesContext;

import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.TestPartitionStagingInsertRecord;
import com.example.myauthapp.partition.dto.PartitionOperationDTO;

import com.example.myauthapp.partition.repository.create.PartitionTargetCreateJdbcRepository;
import com.example.myauthapp.partition.repository.partitionoperation.PartitionOperationJdbcRepository;

import com.example.myauthapp.partition.service.recovery.PartitionRecoveryMoveJdbcService;
import com.example.myauthapp.partition.service.partitionmetadata.PartitionMetadataService;

import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;

import com.example.myauthapp.partition.assertion.discovery.count.TestPartitionCountDiscoveryAssertion;

import com.example.myauthapp.partition.fixture.tool.insert.TestPartitionInsertRecordFixture;
import static com.example.myauthapp.partition.assertion.insertrecord
    .TestPartitionInsertRecordAssertion
    .assertThatTestPartitionStagingInsertRecordsHaveIdenticalPartitionLocalDateTimes;

@Component
public class TestPartitionTargetCreateJdbcRepositoryFixture {

    private final PartitionTargetCreateJdbcRepository partitionTargetCreateJdbcRepository;
    private final PartitionRecoveryMoveJdbcService partitionRecoveryMoveJdbcService;
    private final PartitionMetadataService partitionMetadataService;
    private final PartitionOperationJdbcRepository partitionOperationJdbcRepository;
    private final TestPartitionCountDiscoveryAssertion testPartitionCountDiscoveryAssertion;
    private final TestPartitionInsertRecordFixture testPartitionInsertRecordFixture;

    public TestPartitionTargetCreateJdbcRepositoryFixture(
        PartitionTargetCreateJdbcRepository partitionTargetCreateJdbcRepository,
        PartitionRecoveryMoveJdbcService partitionRecoveryMoveJdbcService,
        PartitionMetadataService partitionMetadataService,
        PartitionOperationJdbcRepository partitionOperationJdbcRepository,
        TestPartitionCountDiscoveryAssertion testPartitionCountDiscoveryAssertion,
        TestPartitionInsertRecordFixture testPartitionInsertRecordFixture
    ) {
        this.partitionTargetCreateJdbcRepository = partitionTargetCreateJdbcRepository;
        this.partitionRecoveryMoveJdbcService = partitionRecoveryMoveJdbcService;
        this.partitionMetadataService = partitionMetadataService;
        this.partitionOperationJdbcRepository = partitionOperationJdbcRepository;
        this.testPartitionCountDiscoveryAssertion =
            testPartitionCountDiscoveryAssertion;
        this.testPartitionInsertRecordFixture =
            testPartitionInsertRecordFixture;
    }

    public void onMoveRecoveryToPartitionShouldMoveOnlySpecifiedOperationId(
        PartitionContextGroup partitionContextGroup,
        List<TestPartitionStagingInsertRecord> testPartitionStagingInsertRecords
    ) {
        Objects.requireNonNull(
            partitionContextGroup,
            "partitionContextGroup must not be null"
        );
        Objects.requireNonNull(
            testPartitionStagingInsertRecords,
            "testPartitionStagingInsertRecords must not be null"
        );

        PartitionTablePropertiesContext partitionTablePropertiesContext =
            partitionContextGroup.getPartitionTablePropertiesContext();
        PartitionOperationPropertiesContext partitionOperationPropertiesContext =
            partitionContextGroup.getPartitionOperationPropertiesContext();
        PartitionRecoveryTableExpressionProviderContext partitionRecoveryTableExpressionProviderContext =
            partitionContextGroup.getPartitionRecoveryTableExpressionProviderContext();

        assertThat(testPartitionStagingInsertRecords)
            .isNotEmpty();

        assertThatTestPartitionStagingInsertRecordsHaveIdenticalPartitionLocalDateTimes(
            testPartitionStagingInsertRecords
        );

        LocalDateTime targetMonth = 
            testPartitionStagingInsertRecords.get(1-1).getPartitionLocalDateTime();
            
        PartitionRangeDTO partitionRangeDTO =
            partitionMetadataService.ensurePartition(
                partitionTablePropertiesContext,
                partitionOperationPropertiesContext,
                targetMonth
            );
        PartitionOperationDTO targetPartitionOperationDTO =
            partitionOperationJdbcRepository.create(
                partitionOperationPropertiesContext,
                partitionRangeDTO,
                PartitionOperationStatus.RUNNING
            );
        PartitionOperationDTO otherPartitionOperationDTO =
            partitionOperationJdbcRepository.create(
                partitionOperationPropertiesContext,
                partitionRangeDTO,
                PartitionOperationStatus.RUNNING
            );

        UUID targetPublicPartitionOperationId =
            targetPartitionOperationDTO.getPublicPartitionOperationId();
        UUID otherPublicPartitionOperationId =
            otherPartitionOperationDTO.getPublicPartitionOperationId();

        TestPartitionStagingInsertRecord testPartitionStagingInsertRecord = null;
        
        int maxRecordCount = 0;
        if(testPartitionStagingInsertRecords != null){
           maxRecordCount = testPartitionStagingInsertRecords.size();
        }//testPartitionStagingInsertRecords&null

        partitionTargetCreateJdbcRepository.createPartition(
            partitionTablePropertiesContext,
            partitionRangeDTO,
            targetPublicPartitionOperationId
        );

        for (
            int recordCount = 1;
            recordCount <= maxRecordCount;
            recordCount ++
        ){
            testPartitionStagingInsertRecord =
                    testPartitionStagingInsertRecords.get((recordCount - 1));

            Long recordId =
                Objects.requireNonNull(
                    testPartitionStagingInsertRecord.getRecordId(),
                    "recordId must not be null"
                );

            testPartitionInsertRecordFixture.insertTestPartitionStagingInsertRecord(
                testPartitionStagingInsertRecord,
                targetPublicPartitionOperationId,
                1
            );

            testPartitionInsertRecordFixture.insertTestPartitionStagingInsertRecord(
                testPartitionStagingInsertRecord,
                otherPublicPartitionOperationId,
                1
            );

        }

        testPartitionCountDiscoveryAssertion.assertStagingCountByPublicPartitionOperationId(
            partitionTablePropertiesContext,
            targetPublicPartitionOperationId,
            maxRecordCount
        );

        testPartitionCountDiscoveryAssertion.assertStagingCountByPublicPartitionOperationId(
            partitionTablePropertiesContext,
            otherPublicPartitionOperationId,
            maxRecordCount
        );

        testPartitionCountDiscoveryAssertion.assertPartitionCount(
            partitionTablePropertiesContext,
            partitionRangeDTO,
            0
        );

        partitionRecoveryMoveJdbcService.moveStagingToPartition(
            partitionTablePropertiesContext,
            partitionRecoveryTableExpressionProviderContext,
            partitionRangeDTO,
            targetPublicPartitionOperationId
        );

        testPartitionCountDiscoveryAssertion.assertStagingCountByPublicPartitionOperationId(
            partitionTablePropertiesContext,
            targetPublicPartitionOperationId,
            0
        );

        testPartitionCountDiscoveryAssertion.assertStagingCountByPublicPartitionOperationId(
            partitionTablePropertiesContext,
            otherPublicPartitionOperationId,
            maxRecordCount
        );

        testPartitionCountDiscoveryAssertion.assertPartitionCount(
            partitionTablePropertiesContext,
            partitionRangeDTO,
            maxRecordCount
        );

    }
}