package com.example.myauthapp.partition.fixture.service.partitionoperation;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.context.PartitionOperationPropertiesContext;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.TestPartitionDefaultInsertRecord;
import com.example.myauthapp.partition.repository.failure.TestPartitionTargetRecoveryFailureRepository;
import com.example.myauthapp.partition.repository.discovery.count.PartitionDefaultCountJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.partitionoperation.PartitionOperationDTODiscoveryJdbcRepository;
import com.example.myauthapp.partition.repository.insert.TestPartitionDefaultRecoveryInsertJdbcRepository;
import com.example.myauthapp.partition.service.recovery.PartitionRecoveryService;
import com.example.myauthapp.partition.service.partitionmetadata.PartitionMetadataService;
import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;
import com.example.myauthapp.partition.dto.PartitionOperationDTO;
import com.example.myauthapp.partition.assertion.discovery.count.TestPartitionCountDiscoveryAssertion;
import com.example.myauthapp.partition.assertion.discovery.name.TestPartitionTargetPartitionNameDiscoveryAssertion;
import com.example.myauthapp.partition.assertion.discovery.partitionoperation.TestPartitionOperationDTODiscoveryAssertion;
import com.example.myauthapp.partition.fixture.tool.insert.TestPartitionInsertRecordFixture;
import com.example.myauthapp.web.partition.exception.PartitionException;

@Component
public class TestPartitionOperationFailureServiceFixture {

    private static final int MAX_RECORD_COUNT = 1000;

    private final PartitionDefaultCountJdbcRepository partitionDefaultCountJdbcRepository;
    private final PartitionRecoveryService partitionRecoveryService;
    private final PartitionMetadataService partitionMetadataService;
    private final PartitionOperationDTODiscoveryJdbcRepository partitionOperationDTODiscoveryJdbcRepository;
    private final TestPartitionCountDiscoveryAssertion testPartitionCountDiscoveryAssertion;
    private final TestPartitionTargetPartitionNameDiscoveryAssertion testPartitionTargetPartitionNameDiscoveryAssertion;
    private final TestPartitionOperationDTODiscoveryAssertion testPartitionOperationDTODiscoveryAssertion;
    private final TestPartitionInsertRecordFixture testPartitionInsertRecordFixture;
    private final TestPartitionTargetRecoveryFailureRepository testPartitionTargetRecoveryFailureRepository;
    private final TestPartitionDefaultRecoveryInsertJdbcRepository testPartitionDefaultRecoveryInsertJdbcRepository;

    public TestPartitionOperationFailureServiceFixture(
        PartitionDefaultCountJdbcRepository partitionDefaultCountJdbcRepository,
        PartitionRecoveryService partitionRecoveryService,
        PartitionMetadataService partitionMetadataService,
        PartitionOperationDTODiscoveryJdbcRepository partitionOperationDTODiscoveryJdbcRepository,
        TestPartitionCountDiscoveryAssertion testPartitionCountDiscoveryAssertion,
        TestPartitionTargetPartitionNameDiscoveryAssertion testPartitionTargetPartitionNameDiscoveryAssertion,
        TestPartitionOperationDTODiscoveryAssertion testPartitionOperationDTODiscoveryAssertion,
        TestPartitionInsertRecordFixture testPartitionInsertRecordFixture,
        TestPartitionTargetRecoveryFailureRepository testPartitionTargetRecoveryFailureRepository,
        TestPartitionDefaultRecoveryInsertJdbcRepository testPartitionDefaultRecoveryInsertJdbcRepository
    ) {
        this.partitionDefaultCountJdbcRepository = partitionDefaultCountJdbcRepository;
        this.partitionRecoveryService = partitionRecoveryService;
        this.partitionMetadataService = partitionMetadataService;
        this.partitionOperationDTODiscoveryJdbcRepository = partitionOperationDTODiscoveryJdbcRepository;
        this.testPartitionCountDiscoveryAssertion = testPartitionCountDiscoveryAssertion;
        this.testPartitionTargetPartitionNameDiscoveryAssertion = testPartitionTargetPartitionNameDiscoveryAssertion;
        this.testPartitionOperationDTODiscoveryAssertion = testPartitionOperationDTODiscoveryAssertion;
        this.testPartitionInsertRecordFixture = testPartitionInsertRecordFixture;
        this.testPartitionTargetRecoveryFailureRepository = testPartitionTargetRecoveryFailureRepository;
        this.testPartitionDefaultRecoveryInsertJdbcRepository = testPartitionDefaultRecoveryInsertJdbcRepository;
    }



    public void onRecoveryShouldFailAndCreateFailedHistory(
        PartitionContextGroup partitionContextGroup,
        TestPartitionDefaultInsertRecord testPartitionDefaultInsertRecord
    ) {
        PartitionTablePropertiesContext partitionTablePropertiesContext =
            partitionContextGroup.getPartitionTablePropertiesContext();
        PartitionOperationPropertiesContext partitionOperationPropertiesContext =
            partitionContextGroup.getPartitionOperationPropertiesContext();
        LocalDateTime targetMonth = 
            testPartitionDefaultInsertRecord.getPartitionLocalDateTime();

        PartitionRangeDTO partitionRangeDTO =
            partitionMetadataService.ensurePartition(
                partitionTablePropertiesContext,
                partitionOperationPropertiesContext,
                targetMonth
            );

        try {
            // ① Recovery先と同名の通常テーブルを作る
            testPartitionTargetRecoveryFailureRepository.createRecoveryFailureInducingTable(
                partitionTablePropertiesContext,
                partitionRangeDTO
            );


            testPartitionInsertRecordFixture.insertTestPartitionDefaultInsertRecord(
                partitionTablePropertiesContext,
                testPartitionDefaultInsertRecord,
                MAX_RECORD_COUNT
            );

            // ③ DEFAULTに1000件あることを確認
            testPartitionCountDiscoveryAssertion.assertDefaultCount(
                partitionTablePropertiesContext,
                partitionRangeDTO,
                MAX_RECORD_COUNT
            );

            // ④ Recovery実行 → DB制約違反
            assertThatThrownBy(
                () ->
                    partitionRecoveryService.recoverAllPartitions(
                        partitionContextGroup
                    )
            ).isInstanceOf(PartitionException.class);

            // ⑤ DEFAULTへロールバックされたことを確認
            testPartitionCountDiscoveryAssertion.assertDefaultCount(
                partitionTablePropertiesContext,
                partitionRangeDTO,
                MAX_RECORD_COUNT
            );

            // ⑥ FAILED履歴確認
            PartitionOperationDTO partitionOperationDTO =
                testPartitionOperationDTODiscoveryAssertion
                    .findAssertedSinglePartitionOperationDTO();

            assertThat(partitionOperationDTO.getPublicPartitionOperationId())
                .isNotNull();

            assertThat(partitionOperationDTO.getPartitionOperationStatus())
                .isEqualTo(PartitionOperationStatus.FAILED);

            assertThat(partitionOperationDTO.getStartedAt())
                .isNotNull();

            assertThat(partitionOperationDTO.getCompletedAt())
                .isNull();
            assertThat(partitionOperationDTO.getFailedAt())
                .isNotNull();

            assertThat(partitionOperationDTO.getDefaultPartitionRowCount())
                .isNull();

            assertThat(partitionOperationDTO.getStagingRowCount())
                .isNull();

            assertThat(partitionOperationDTO.getErrorMessage())
                .isNotNull();

        } finally {

            // ⑦ テスト用テーブルを削除
            testPartitionTargetRecoveryFailureRepository.dropRecoveryFailureInducingTable(
                partitionRangeDTO
            );
        }
    }
}