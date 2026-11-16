package com.example.myauthapp.partition.fixture.job;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.context.PartitionOperationPropertiesContext;
import com.example.myauthapp.partition.context.PartitionMaintenancePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.service.partitionmetadata.PartitionMetadataService;
import com.example.myauthapp.partition.repository.create.PartitionTargetCreateJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.count.PartitionDefaultCountJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.name.PartitionTargetPartitionNameDiscoveryJdbcRepository;
import com.example.myauthapp.partition.repository.insert.TestPartitionDefaultRecoveryInsertJdbcRepository;
import com.example.myauthapp.partition.service.job.PartitionJobService;
import com.example.myauthapp.partition.dto.TestPartitionDefaultInsertRecord;
import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;
import com.example.myauthapp.partition.assertion.discovery.count.TestPartitionCountDiscoveryAssertion;
import com.example.myauthapp.partition.assertion.discovery.name.TestPartitionTargetPartitionNameDiscoveryAssertion;
import com.example.myauthapp.partition.assertion.discovery.partitionoperation.TestPartitionOperationDTODiscoveryAssertion;
import com.example.myauthapp.partition.fixture.tool.insert.TestPartitionInsertRecordFixture;

@Component
public class TestPartitionJobFixture {

    private final static int MAX_RECORD_COUNT = 10;
    private final static int MAX_JOB_EXECUTION_COUNT = 20;
    private final PartitionMetadataService partitionMetadataService;
    private final PartitionTargetCreateJdbcRepository partitionTargetCreateJdbcRepository;
    private final PartitionDefaultCountJdbcRepository partitionDefaultCountJdbcRepository;
    private final PartitionJobService partitionJobService;
    private final PartitionTargetPartitionNameDiscoveryJdbcRepository partitionTargetPartitionNameDiscoveryJdbcRepository;
    private final TestPartitionCountDiscoveryAssertion testPartitionCountDiscoveryAssertion;
    private final TestPartitionTargetPartitionNameDiscoveryAssertion testPartitionTargetPartitionNameDiscoveryAssertion;
    private final TestPartitionOperationDTODiscoveryAssertion testPartitionOperationDTODiscoveryAssertion;
    private final TestPartitionInsertRecordFixture testPartitionInsertRecordFixture;
    private final TestPartitionDefaultRecoveryInsertJdbcRepository testPartitionDefaultRecoveryInsertJdbcRepository;

    public TestPartitionJobFixture(
        PartitionMetadataService partitionMetadataService,
        PartitionTargetCreateJdbcRepository partitionTargetCreateJdbcRepository,
        PartitionDefaultCountJdbcRepository partitionDefaultCountJdbcRepository,
        PartitionJobService partitionJobService,
        PartitionTargetPartitionNameDiscoveryJdbcRepository partitionTargetPartitionNameDiscoveryJdbcRepository,
        TestPartitionCountDiscoveryAssertion testPartitionCountDiscoveryAssertion,
        TestPartitionTargetPartitionNameDiscoveryAssertion testPartitionTargetPartitionNameDiscoveryAssertion,
        TestPartitionOperationDTODiscoveryAssertion testPartitionOperationDTODiscoveryAssertion,
        TestPartitionInsertRecordFixture testPartitionInsertRecordFixture,
        TestPartitionDefaultRecoveryInsertJdbcRepository testPartitionDefaultRecoveryInsertJdbcRepository
    ){
        this.partitionMetadataService = partitionMetadataService;
        this.partitionTargetCreateJdbcRepository = partitionTargetCreateJdbcRepository;
        this.partitionDefaultCountJdbcRepository = partitionDefaultCountJdbcRepository;
        this.partitionJobService = partitionJobService;
        this.partitionTargetPartitionNameDiscoveryJdbcRepository = partitionTargetPartitionNameDiscoveryJdbcRepository;
        this.testPartitionCountDiscoveryAssertion = testPartitionCountDiscoveryAssertion;
        this.testPartitionTargetPartitionNameDiscoveryAssertion = testPartitionTargetPartitionNameDiscoveryAssertion;
        this.testPartitionOperationDTODiscoveryAssertion = testPartitionOperationDTODiscoveryAssertion;
        this.testPartitionInsertRecordFixture = testPartitionInsertRecordFixture;
        this.testPartitionDefaultRecoveryInsertJdbcRepository = testPartitionDefaultRecoveryInsertJdbcRepository;
    }

    public void assertRecoverAndCleanupPartitions(
        PartitionContextGroup partitionContextGroup,
        TestPartitionDefaultInsertRecord testPartitionDefaultInsertRecord
    ) {
        PartitionTablePropertiesContext partitionTablePropertiesContext =
            partitionContextGroup.getPartitionTablePropertiesContext();
        PartitionOperationPropertiesContext partitionOperationPropertiesContext =
            partitionContextGroup.getPartitionOperationPropertiesContext();
        PartitionMaintenancePropertiesContext partitionMaintenancePropertiesContext = 
            partitionContextGroup.getPartitionMaintenancePropertiesContext();
        PartitionRecoveryTableExpressionProviderContext partitionRecoveryTableExpressionProviderContext =
            partitionContextGroup.getPartitionRecoveryTableExpressionProviderContext();

        LocalDateTime currentLocalDateTime = 
            testPartitionDefaultInsertRecord.getPartitionLocalDateTime();
        int maintenanceTargetMonthsAgo = 
            partitionMaintenancePropertiesContext.getMaintenanceTargetMonthsAgo();
        LocalDateTime maintenanceTargetMonthsAgoLocalDateTime = 
            currentLocalDateTime.minusMonths(maintenanceTargetMonthsAgo);

        LocalDateTime oldMonthStart = maintenanceTargetMonthsAgoLocalDateTime.minusMonths(1);

        PartitionRangeDTO currentPartitionRangeDTO =
            partitionMetadataService.ensurePartition(
                partitionTablePropertiesContext,
                partitionOperationPropertiesContext,
                currentLocalDateTime
            );

        PartitionRangeDTO oldPartitionRangeDTO =
            partitionMetadataService.ensurePartition(
                partitionTablePropertiesContext,
                partitionOperationPropertiesContext,
                oldMonthStart
            );

        partitionTargetCreateJdbcRepository.createPartition(
            partitionTablePropertiesContext,
            oldPartitionRangeDTO,
            UUID.randomUUID()
        );

        testPartitionTargetPartitionNameDiscoveryAssertion.assertThatTargetPartitionExists(
            partitionTablePropertiesContext,
            oldPartitionRangeDTO
        );

        testPartitionInsertRecordFixture.insertTestPartitionDefaultInsertRecord(
            partitionTablePropertiesContext,
            testPartitionDefaultInsertRecord,
            MAX_RECORD_COUNT
        );

        testPartitionCountDiscoveryAssertion.assertDefaultCount(
            partitionTablePropertiesContext,
            currentPartitionRangeDTO,
            MAX_RECORD_COUNT
        );

        for(
            int jobExecutionCount = 1;
            jobExecutionCount <= MAX_JOB_EXECUTION_COUNT;
            jobExecutionCount++
        ){
            partitionJobService.partitionJobExecute(
                partitionContextGroup,
                maintenanceTargetMonthsAgoLocalDateTime
            );
        }

        testPartitionCountDiscoveryAssertion.assertDefaultCount(
            partitionTablePropertiesContext,
            currentPartitionRangeDTO,
            0
        );

        testPartitionCountDiscoveryAssertion.assertPartitionCount(
            partitionTablePropertiesContext,
            currentPartitionRangeDTO,
            MAX_RECORD_COUNT
        );

        testPartitionTargetPartitionNameDiscoveryAssertion.assertThatTargetPartitionDoesNotExist(
            partitionTablePropertiesContext,
            oldPartitionRangeDTO
        );
    }

}