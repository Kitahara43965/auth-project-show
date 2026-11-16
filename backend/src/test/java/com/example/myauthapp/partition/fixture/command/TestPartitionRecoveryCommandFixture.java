package com.example.myauthapp.partition.fixture.command;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.TestPartitionDefaultInsertRecord;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.context.PartitionOperationPropertiesContext;
import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;
import com.example.myauthapp.partition.context.PartitionCommandPropertiesContext;
import com.example.myauthapp.partition.service.command.PartitionRecoveryCommandService;
import com.example.myauthapp.partition.service.partitionmetadata.PartitionMetadataService;
import com.example.myauthapp.partition.repository.discovery.time.PartitionDefaultTimeDiscoveryJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.count.PartitionDefaultCountJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.count.PartitionStagingCountJdbcRepository;
import com.example.myauthapp.partition.repository.insert.TestPartitionDefaultRecoveryInsertJdbcRepository;
import com.example.myauthapp.partition.assertion.discovery.count.TestPartitionCountDiscoveryAssertion;
import com.example.myauthapp.partition.assertion.discovery.name.TestPartitionTargetPartitionNameDiscoveryAssertion;
import com.example.myauthapp.partition.assertion.discovery.partitionoperation.TestPartitionOperationDTODiscoveryAssertion;
import com.example.myauthapp.partition.fixture.tool.insert.TestPartitionInsertRecordFixture;

@Component
public class TestPartitionRecoveryCommandFixture {

    private static final int MAX_RECORD_COUNT = 1000;

    private final PartitionRecoveryCommandService partitionRecoveryCommandService;
    private final PartitionMetadataService partitionMetadataService;
    private final TestPartitionCountDiscoveryAssertion testPartitionCountDiscoveryAssertion;
    private final TestPartitionInsertRecordFixture testPartitionInsertRecordFixture;

    public TestPartitionRecoveryCommandFixture(
        PartitionRecoveryCommandService partitionRecoveryCommandService,
        PartitionMetadataService partitionMetadataService,
        TestPartitionCountDiscoveryAssertion testPartitionCountDiscoveryAssertion,
        TestPartitionInsertRecordFixture testPartitionInsertRecordFixture
    ) {
        this.partitionRecoveryCommandService = partitionRecoveryCommandService;
        this.partitionMetadataService = partitionMetadataService;
        this.testPartitionCountDiscoveryAssertion = testPartitionCountDiscoveryAssertion;
        this.testPartitionInsertRecordFixture = testPartitionInsertRecordFixture;
    }

    public void onRunShouldRecoverWhenRecoveryCommandIsPassed(
        PartitionContextGroup partitionContextGroup,
        TestPartitionDefaultInsertRecord testPartitionDefaultInsertRecord
    ) {
        PartitionTablePropertiesContext partitionTablePropertiesContext =
            partitionContextGroup.getPartitionTablePropertiesContext();
        PartitionOperationPropertiesContext partitionOperationPropertiesContext =
            partitionContextGroup.getPartitionOperationPropertiesContext();
        PartitionRecoveryTableExpressionProviderContext partitionRecoveryTableExpressionProviderContext = 
                partitionContextGroup.getPartitionRecoveryTableExpressionProviderContext();
        PartitionCommandPropertiesContext partitionCommandPropertiesContext =
            partitionContextGroup.getPartitionCommandPropertiesContext();

        LocalDateTime targetMonth = 
            testPartitionDefaultInsertRecord.getPartitionLocalDateTime();

        PartitionRangeDTO partitionRangeDTO =
            partitionMetadataService.ensurePartition(
                partitionTablePropertiesContext,
                partitionOperationPropertiesContext,
                targetMonth
            );

        String partitionRecoveryCommandText = 
            partitionCommandPropertiesContext.getPartitionRecoveryCommandText();

        testPartitionInsertRecordFixture.insertTestPartitionDefaultInsertRecord(
            partitionTablePropertiesContext,
            testPartitionDefaultInsertRecord,
            MAX_RECORD_COUNT
        );

        // 実際のCommandを実行
        partitionRecoveryCommandService.partitionRecoveryCommandExecute(
            partitionContextGroup
        );

        // default partition から対象月のデータがなくなっていること
        testPartitionCountDiscoveryAssertion.assertDefaultCount(
            partitionTablePropertiesContext,
            partitionRangeDTO,
            0
        );

        // 対象月パーティションにデータが入っていること
        testPartitionCountDiscoveryAssertion.assertPartitionCount(
            partitionTablePropertiesContext,
            partitionRangeDTO,
            MAX_RECORD_COUNT
        );

        // recovery staging に残っていないこと
        testPartitionCountDiscoveryAssertion.assertStagingCount(
            partitionTablePropertiesContext,
            0
        );
    }
}