package com.example.myauthapp.partition.fixture.service.recovery;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.context.PartitionRecoveryTableExpressionProviderContext;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.context.PartitionOperationPropertiesContext;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.TestPartitionDefaultInsertRecord;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionOperationDTO;
import com.example.myauthapp.partition.repository.discovery.count.PartitionDefaultCountJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.count.PartitionStagingCountJdbcRepository;
import com.example.myauthapp.partition.repository.insert.TestPartitionDefaultRecoveryInsertJdbcRepository;
import com.example.myauthapp.partition.repository.create.PartitionTargetCreateJdbcRepository;
import com.example.myauthapp.partition.service.recovery.PartitionRecoveryMoveJdbcService;
import com.example.myauthapp.partition.service.partitionmetadata.PartitionMetadataService;
import com.example.myauthapp.partition.repository.partitionoperation.PartitionOperationJdbcRepository;
import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;
import com.example.myauthapp.partition.assertion.discovery.count.TestPartitionCountDiscoveryAssertion;
import com.example.myauthapp.partition.assertion.discovery.name.TestPartitionTargetPartitionNameDiscoveryAssertion;
import com.example.myauthapp.partition.assertion.discovery.partitionoperation.TestPartitionOperationDTODiscoveryAssertion;
import com.example.myauthapp.partition.fixture.tool.insert.TestPartitionInsertRecordFixture;

@Component
public class TestPartitionRecoveryMoveServiceFixture {

    private final static int MAX_RECORD_COUNT = 1000;

    private final PartitionTargetCreateJdbcRepository partitionTargetCreateJdbcRepository;
    private final PartitionStagingCountJdbcRepository partitionStagingCountJdbcRepository;
    private final PartitionDefaultCountJdbcRepository partitionDefaultCountJdbcRepository;
    private final PartitionRecoveryMoveJdbcService partitionRecoveryMoveJdbcService;
    private final PartitionMetadataService partitionMetadataService;
    private final PartitionOperationJdbcRepository partitionOperationJdbcRepository;
    private final TestPartitionCountDiscoveryAssertion testPartitionCountDiscoveryAssertion;
    private final TestPartitionTargetPartitionNameDiscoveryAssertion testPartitionTargetPartitionNameDiscoveryAssertion;
    private final TestPartitionOperationDTODiscoveryAssertion testPartitionOperationDTODiscoveryAssertion;
    private final TestPartitionInsertRecordFixture testPartitionInsertRecordFixture;
    private final TestPartitionDefaultRecoveryInsertJdbcRepository testPartitionDefaultRecoveryInsertJdbcRepository;

    public TestPartitionRecoveryMoveServiceFixture(
        PartitionTargetCreateJdbcRepository partitionTargetCreateJdbcRepository,
        PartitionStagingCountJdbcRepository partitionStagingCountJdbcRepository,
        PartitionDefaultCountJdbcRepository partitionDefaultCountJdbcRepository,
        PartitionRecoveryMoveJdbcService partitionRecoveryMoveJdbcService,
        PartitionMetadataService partitionMetadataService,
        PartitionOperationJdbcRepository partitionOperationJdbcRepository,
        TestPartitionCountDiscoveryAssertion testPartitionCountDiscoveryAssertion,
        TestPartitionTargetPartitionNameDiscoveryAssertion testPartitionTargetPartitionNameDiscoveryAssertion,
        TestPartitionOperationDTODiscoveryAssertion testPartitionOperationDTODiscoveryAssertion,
        TestPartitionInsertRecordFixture testPartitionInsertRecordFixture,
        TestPartitionDefaultRecoveryInsertJdbcRepository testPartitionDefaultRecoveryInsertJdbcRepository
    ){
        this.partitionTargetCreateJdbcRepository = partitionTargetCreateJdbcRepository;
        this.partitionStagingCountJdbcRepository = partitionStagingCountJdbcRepository;
        this.partitionDefaultCountJdbcRepository = partitionDefaultCountJdbcRepository;
        this.partitionRecoveryMoveJdbcService = partitionRecoveryMoveJdbcService;
        this.partitionMetadataService = partitionMetadataService;
        this.partitionOperationJdbcRepository = partitionOperationJdbcRepository;
        this.testPartitionCountDiscoveryAssertion = testPartitionCountDiscoveryAssertion;
        this.testPartitionTargetPartitionNameDiscoveryAssertion = testPartitionTargetPartitionNameDiscoveryAssertion;
        this.testPartitionOperationDTODiscoveryAssertion = testPartitionOperationDTODiscoveryAssertion;
        this.testPartitionInsertRecordFixture = testPartitionInsertRecordFixture;
        this.testPartitionDefaultRecoveryInsertJdbcRepository = testPartitionDefaultRecoveryInsertJdbcRepository;
    }

    public void onMoveDefaultToRecoveryAndMoveToPartitionShouldMoveRecords(
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

        PartitionOperationDTO partitionOperationDTO = 
            partitionOperationJdbcRepository.create(
                partitionOperationPropertiesContext,
                partitionRangeDTO,
                PartitionOperationStatus.RUNNING
            );

        UUID publicPartitionOperationId =
            partitionOperationDTO.getPublicPartitionOperationId();

        // 1. 先にdefaultへINSERT
        testPartitionInsertRecordFixture.insertTestPartitionDefaultInsertRecord(
            partitionTablePropertiesContext,
            testPartitionDefaultInsertRecord,
            MAX_RECORD_COUNT
        );

        // 2. default → recovery staging
        partitionRecoveryMoveJdbcService.moveDefaultToStaging(
            partitionTablePropertiesContext,
            partitionRecoveryTableExpressionProviderContext,
            partitionRangeDTO,
            publicPartitionOperationId
        );

        assertThat(publicPartitionOperationId).isNotNull();

        testPartitionCountDiscoveryAssertion.assertStagingCountByPublicPartitionOperationId(
            partitionTablePropertiesContext,
            publicPartitionOperationId,
            MAX_RECORD_COUNT
        );

        // 3. defaultが空になった後で月次パーティションを作る
        partitionTargetCreateJdbcRepository.createPartition(
            partitionTablePropertiesContext,
            partitionRangeDTO,
            publicPartitionOperationId
        );

        // 4. recovery staging → 月次パーティション
        partitionRecoveryMoveJdbcService.moveStagingToPartition(
            partitionTablePropertiesContext,
            partitionRecoveryTableExpressionProviderContext,
            partitionRangeDTO,
            publicPartitionOperationId
        );

        // 5. recovery stagingから消えていること
        testPartitionCountDiscoveryAssertion.assertStagingCountByPublicPartitionOperationId(
            partitionTablePropertiesContext,
            publicPartitionOperationId,
            0
        );

        // 6. 月次パーティションに入っていること
        testPartitionCountDiscoveryAssertion.assertPartitionCount(
            partitionTablePropertiesContext,
            partitionRangeDTO,
            MAX_RECORD_COUNT
        );
    }
}