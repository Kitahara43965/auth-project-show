package com.example.myauthapp.partition.fixture.service.partitionoperation;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.context.PartitionOperationPropertiesContext;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.PartitionOperationDTO;
import com.example.myauthapp.partition.dto.TestPartitionDefaultInsertRecord;
import com.example.myauthapp.partition.repository.discovery.count.PartitionDefaultCountJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.partitionoperation.PartitionOperationDTODiscoveryJdbcRepository;
import com.example.myauthapp.partition.repository.insert.TestPartitionDefaultRecoveryInsertJdbcRepository;
import com.example.myauthapp.partition.service.recovery.PartitionRecoveryService;
import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;
import com.example.myauthapp.partition.service.partitionmetadata.PartitionMetadataService;
import com.example.myauthapp.partition.assertion.discovery.count.TestPartitionCountDiscoveryAssertion;
import com.example.myauthapp.partition.assertion.discovery.name.TestPartitionTargetPartitionNameDiscoveryAssertion;
import com.example.myauthapp.partition.assertion.discovery.partitionoperation.TestPartitionOperationDTODiscoveryAssertion;
import com.example.myauthapp.partition.fixture.tool.insert.TestPartitionInsertRecordFixture;
import com.example.myauthapp.partition.assertion.discovery.discovery.count.TestPartitionCountGroupDiscoveryAssertion;

@Component
public class TestPartitionOperationServiceFixture {

    private static final int MAX_RECORD_COUNT = 1000;

    private final PartitionDefaultCountJdbcRepository partitionDefaultCountJdbcRepository;
    private final PartitionRecoveryService partitionRecoveryService;
    private final PartitionMetadataService partitionMetadataService;
    private final PartitionOperationDTODiscoveryJdbcRepository partitionOperationDTODiscoveryJdbcRepository;
    private final TestPartitionCountDiscoveryAssertion testPartitionCountDiscoveryAssertion;
    private final TestPartitionTargetPartitionNameDiscoveryAssertion testPartitionTargetPartitionNameDiscoveryAssertion;
    private final TestPartitionOperationDTODiscoveryAssertion testPartitionOperationDTODiscoveryAssertion;
    private final TestPartitionInsertRecordFixture testPartitionInsertRecordFixture;
    private final TestPartitionDefaultRecoveryInsertJdbcRepository testPartitionDefaultRecoveryInsertJdbcRepository;
    private final TestPartitionCountGroupDiscoveryAssertion testPartitionCountGroupDiscoveryAssertion;

    public TestPartitionOperationServiceFixture(
        PartitionDefaultCountJdbcRepository partitionDefaultCountJdbcRepository,
        PartitionRecoveryService partitionRecoveryService,
        PartitionMetadataService partitionMetadataService,
        PartitionOperationDTODiscoveryJdbcRepository partitionOperationDTODiscoveryJdbcRepository,
        TestPartitionCountDiscoveryAssertion testPartitionCountDiscoveryAssertion,
        TestPartitionTargetPartitionNameDiscoveryAssertion testPartitionTargetPartitionNameDiscoveryAssertion,
        TestPartitionOperationDTODiscoveryAssertion testPartitionOperationDTODiscoveryAssertion,
        TestPartitionInsertRecordFixture testPartitionInsertRecordFixture,
        TestPartitionDefaultRecoveryInsertJdbcRepository testPartitionDefaultRecoveryInsertJdbcRepository,
        TestPartitionCountGroupDiscoveryAssertion testPartitionCountGroupDiscoveryAssertion
    ) {
        this.partitionDefaultCountJdbcRepository = partitionDefaultCountJdbcRepository;
        this.partitionRecoveryService = partitionRecoveryService;
        this.partitionMetadataService = partitionMetadataService;
        this.partitionOperationDTODiscoveryJdbcRepository = partitionOperationDTODiscoveryJdbcRepository;
        this.testPartitionCountDiscoveryAssertion = testPartitionCountDiscoveryAssertion;
        this.testPartitionTargetPartitionNameDiscoveryAssertion = testPartitionTargetPartitionNameDiscoveryAssertion;
        this.testPartitionOperationDTODiscoveryAssertion = testPartitionOperationDTODiscoveryAssertion;
        this.testPartitionInsertRecordFixture = testPartitionInsertRecordFixture;
        this.testPartitionDefaultRecoveryInsertJdbcRepository = testPartitionDefaultRecoveryInsertJdbcRepository;
        this.testPartitionCountGroupDiscoveryAssertion = testPartitionCountGroupDiscoveryAssertion;
    }


    public void onRecoveryShouldCompleteAndCreateCompletedHistory(
        PartitionContextGroup partitionContextGroup,
        TestPartitionDefaultInsertRecord testPartitionDefaultInsertRecord
    ) {
        PartitionTablePropertiesContext partitionTablePropertiesContext =
            partitionContextGroup.getPartitionTablePropertiesContext();
        PartitionOperationPropertiesContext partitionOperationPropertiesContext =
            partitionContextGroup.getPartitionOperationPropertiesContext();
        PartitionRecoveryTableExpressionProviderContext partitionRecoveryTableExpressionProviderContext =
            partitionContextGroup.getPartitionRecoveryTableExpressionProviderContext();
        LocalDateTime targetMonth = 
            testPartitionDefaultInsertRecord.getPartitionLocalDateTime();

        PartitionRangeDTO partitionRangeDTO =
            partitionMetadataService.ensurePartition(
                partitionTablePropertiesContext,
                partitionOperationPropertiesContext,
                targetMonth
            );

        // DEFAULT partitionへテストデータを投入
        testPartitionInsertRecordFixture.insertTestPartitionDefaultInsertRecord(
            partitionTablePropertiesContext,
            testPartitionDefaultInsertRecord,
            MAX_RECORD_COUNT
        );

        testPartitionCountDiscoveryAssertion.assertDefaultCount(
            partitionTablePropertiesContext,
            partitionRangeDTO,
            MAX_RECORD_COUNT
        );

        // Recovery実行
        partitionRecoveryService.recoverAllPartitions(
            partitionContextGroup
        );

        testPartitionCountDiscoveryAssertion.assertDefaultCount(
            partitionTablePropertiesContext,
            partitionRangeDTO,
            0
        );

        testPartitionCountDiscoveryAssertion.assertPartitionCount(
            partitionTablePropertiesContext,
            partitionRangeDTO,
            MAX_RECORD_COUNT
        );

        // History確認
        PartitionOperationDTO partitionOperationDTO =
            testPartitionOperationDTODiscoveryAssertion
                .findAssertedSinglePartitionOperationDTO();

        assertThat(
            partitionOperationDTO.getPartitionOperationStatus()
        ).isEqualTo(
            PartitionOperationStatus.COMPLETED
        );

        testPartitionCountGroupDiscoveryAssertion.assertThatPartitionRecoveryIsCompleted(
            partitionTablePropertiesContext,
            partitionRangeDTO,
            MAX_RECORD_COUNT
        );

        assertThat(
            partitionOperationDTO.getDefaultPartitionRowCount()
        ).isEqualTo(
            (long) MAX_RECORD_COUNT
        );

        assertThat(
            partitionOperationDTO.getStagingRowCount()
        ).isEqualTo(
            (long) MAX_RECORD_COUNT
        );

        assertThat(
            partitionOperationDTO.getStartedAt()
        ).isNotNull();

        assertThat(partitionOperationDTO.getCompletedAt())
            .isNotNull();
        assertThat(partitionOperationDTO.getFailedAt())
            .isNull();

        assertThat(
            partitionOperationDTO.getErrorMessage()
        ).isNull();
    }
}