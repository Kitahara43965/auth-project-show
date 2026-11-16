package com.example.myauthapp.partition.fixture.service.recovery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.context.PartitionOperationPropertiesContext;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionOperationDTO;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.TestPartitionDefaultInsertRecord;
import com.example.myauthapp.partition.fixture.tool.failure.TestPartitionDefaultFailureTriggerCreateFixture;
import com.example.myauthapp.partition.fixture.tool.failure.TestPartitionStagingFailureTriggerCreateFixture;
import com.example.myauthapp.partition.fixture.tool.failure.TestPartitionDefaultFailureTriggerDropFixture;
import com.example.myauthapp.partition.fixture.tool.failure.TestPartitionStagingFailureTriggerDropFixture;
import com.example.myauthapp.partition.repository.discovery.count.PartitionDefaultCountJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.count.PartitionStagingCountJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.name.PartitionTargetPartitionNameDiscoveryJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.partitionoperation.PartitionOperationDTODiscoveryJdbcRepository;
import com.example.myauthapp.partition.repository.insert.TestPartitionDefaultRecoveryInsertJdbcRepository;
import com.example.myauthapp.partition.service.partitionmetadata.PartitionMetadataService;
import com.example.myauthapp.partition.service.recoveryfacade.PartitionRecoveryFacadeJdbcService;
import com.example.myauthapp.partition.service.recovery.PartitionRecoveryService;
import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;
import com.example.myauthapp.partition.repository.discovery.source.PartitionDefaultPartitionRecordSourceDiscoveryJdbcRepository;
import com.example.myauthapp.partition.repository.discovery.source.PartitionStagingPartitionRecordSourceDiscoveryJdbcRepository;
import com.example.myauthapp.partition.assertion.discovery.count.TestPartitionCountDiscoveryAssertion;
import com.example.myauthapp.partition.assertion.discovery.name.TestPartitionTargetPartitionNameDiscoveryAssertion;
import com.example.myauthapp.partition.assertion.discovery.partitionoperation.TestPartitionOperationDTODiscoveryAssertion;
import com.example.myauthapp.partition.fixture.tool.insert.TestPartitionInsertRecordFixture;
import com.example.myauthapp.web.partition.exception.PartitionException;

@Component
public class TestPartitionRecoveryAndRecoveryRetryServiceFixture {

    private static final int MAX_RECORD_COUNT = 100;

    private final PartitionDefaultCountJdbcRepository partitionDefaultCountJdbcRepository;
    private final PartitionStagingCountJdbcRepository partitionStagingCountJdbcRepository;
    private final PartitionTargetPartitionNameDiscoveryJdbcRepository partitionTargetPartitionNameDiscoveryJdbcRepository;

    private final PartitionRecoveryService partitionRecoveryService;
    private final PartitionRecoveryFacadeJdbcService partitionRecoveryFacadeJdbcService;
    private final PartitionMetadataService partitionMetadataService;

    private final PartitionOperationDTODiscoveryJdbcRepository
        partitionOperationDTODiscoveryJdbcRepository;

    private final PartitionDefaultPartitionRecordSourceDiscoveryJdbcRepository
        partitionDefaultPartitionRecordSourceDiscoveryJdbcRepository;

    private final PartitionStagingPartitionRecordSourceDiscoveryJdbcRepository
        partitionStagingPartitionRecordSourceDiscoveryJdbcRepository;

    private final TestPartitionCountDiscoveryAssertion
        testPartitionCountDiscoveryAssertion;

    private final TestPartitionTargetPartitionNameDiscoveryAssertion
        testPartitionTargetPartitionNameDiscoveryAssertion;

    private final TestPartitionOperationDTODiscoveryAssertion
        testPartitionOperationDTODiscoveryAssertion;

    private final TestPartitionInsertRecordFixture
        testPartitionInsertRecordFixture;

    private final TestPartitionDefaultFailureTriggerCreateFixture
        testPartitionDefaultFailureTriggerCreateFixture;

    private final TestPartitionStagingFailureTriggerCreateFixture
        testPartitionStagingFailureTriggerCreateFixture;

    private final TestPartitionDefaultFailureTriggerDropFixture
        testPartitionDefaultFailureTriggerDropFixture;

    private final TestPartitionStagingFailureTriggerDropFixture
        testPartitionStagingFailureTriggerDropFixture;

    private final TestPartitionDefaultRecoveryInsertJdbcRepository
        testPartitionDefaultRecoveryInsertJdbcRepository;

    public TestPartitionRecoveryAndRecoveryRetryServiceFixture(
        PartitionDefaultCountJdbcRepository partitionDefaultCountJdbcRepository,
        PartitionStagingCountJdbcRepository partitionStagingCountJdbcRepository,
        PartitionTargetPartitionNameDiscoveryJdbcRepository partitionTargetPartitionNameDiscoveryJdbcRepository,
        PartitionRecoveryService partitionRecoveryService,
        PartitionRecoveryFacadeJdbcService partitionRecoveryFacadeJdbcService,
        PartitionMetadataService partitionMetadataService,
        PartitionOperationDTODiscoveryJdbcRepository
            partitionOperationDTODiscoveryJdbcRepository,
        PartitionDefaultPartitionRecordSourceDiscoveryJdbcRepository
            partitionDefaultPartitionRecordSourceDiscoveryJdbcRepository,
        PartitionStagingPartitionRecordSourceDiscoveryJdbcRepository
            partitionStagingPartitionRecordSourceDiscoveryJdbcRepository,
        TestPartitionCountDiscoveryAssertion
            testPartitionCountDiscoveryAssertion,
        TestPartitionTargetPartitionNameDiscoveryAssertion
            testPartitionTargetPartitionNameDiscoveryAssertion,
        TestPartitionOperationDTODiscoveryAssertion
            testPartitionOperationDTODiscoveryAssertion,
        TestPartitionInsertRecordFixture
            testPartitionInsertRecordFixture,
        TestPartitionDefaultFailureTriggerCreateFixture
            testPartitionDefaultFailureTriggerCreateFixture,
        TestPartitionStagingFailureTriggerCreateFixture
            testPartitionStagingFailureTriggerCreateFixture,
        TestPartitionDefaultFailureTriggerDropFixture
            testPartitionDefaultFailureTriggerDropFixture,
        TestPartitionStagingFailureTriggerDropFixture
            testPartitionStagingFailureTriggerDropFixture,
        TestPartitionDefaultRecoveryInsertJdbcRepository
            testPartitionDefaultRecoveryInsertJdbcRepository
    ) {
        this.partitionDefaultCountJdbcRepository =
            partitionDefaultCountJdbcRepository;
        this.partitionStagingCountJdbcRepository =
            partitionStagingCountJdbcRepository;
        this.partitionTargetPartitionNameDiscoveryJdbcRepository =
            partitionTargetPartitionNameDiscoveryJdbcRepository;
        this.partitionRecoveryService =
            partitionRecoveryService;
        this.partitionRecoveryFacadeJdbcService =
            partitionRecoveryFacadeJdbcService;
        this.partitionMetadataService =
            partitionMetadataService;
        this.partitionOperationDTODiscoveryJdbcRepository =
            partitionOperationDTODiscoveryJdbcRepository;
        this.partitionDefaultPartitionRecordSourceDiscoveryJdbcRepository =
            partitionDefaultPartitionRecordSourceDiscoveryJdbcRepository;
        this.partitionStagingPartitionRecordSourceDiscoveryJdbcRepository =
            partitionStagingPartitionRecordSourceDiscoveryJdbcRepository;
        this.testPartitionCountDiscoveryAssertion =
            testPartitionCountDiscoveryAssertion;
        this.testPartitionTargetPartitionNameDiscoveryAssertion =
            testPartitionTargetPartitionNameDiscoveryAssertion;
        this.testPartitionOperationDTODiscoveryAssertion =
            testPartitionOperationDTODiscoveryAssertion;
        this.testPartitionInsertRecordFixture =
            testPartitionInsertRecordFixture;
        this.testPartitionDefaultFailureTriggerCreateFixture =
            testPartitionDefaultFailureTriggerCreateFixture;
        this.testPartitionStagingFailureTriggerCreateFixture =
            testPartitionStagingFailureTriggerCreateFixture;
        this.testPartitionDefaultFailureTriggerDropFixture =
            testPartitionDefaultFailureTriggerDropFixture;
        this.testPartitionStagingFailureTriggerDropFixture =
            testPartitionStagingFailureTriggerDropFixture;
        this.testPartitionDefaultRecoveryInsertJdbcRepository =
            testPartitionDefaultRecoveryInsertJdbcRepository;
    }

    public void onRetryAfterRetryFailureShouldComplete(
        PartitionContextGroup partitionContextGroup,
        TestPartitionDefaultInsertRecord testPartitionDefaultInsertRecord
    ) {
        PartitionTablePropertiesContext partitionTablePropertiesContext =
            partitionContextGroup.getPartitionTablePropertiesContext();

        PartitionOperationPropertiesContext
            partitionOperationPropertiesContext =
                partitionContextGroup
                    .getPartitionOperationPropertiesContext();

        LocalDateTime targetMonth = 
            testPartitionDefaultInsertRecord.getPartitionLocalDateTime();

        PartitionRangeDTO partitionRangeDTO =
            partitionMetadataService.ensurePartition(
                partitionTablePropertiesContext,
                partitionOperationPropertiesContext,
                targetMonth
            );

        try {
            /*
             * 初回Recoveryを失敗させる。
             */
            testPartitionDefaultFailureTriggerCreateFixture
                .createDeleteDefaultFailureTrigger(
                    partitionTablePropertiesContext
                );

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

            assertThatThrownBy(
                () ->
                    partitionRecoveryService.recoverAllPartitions(
                        partitionContextGroup
                    )
            ).isInstanceOf(PartitionException.class);

            /*
             * 初回失敗後の状態。
             */
            testPartitionCountDiscoveryAssertion.assertDefaultCount(
                partitionTablePropertiesContext,
                partitionRangeDTO,
                MAX_RECORD_COUNT
            );

            PartitionOperationDTO failedPartitionOperationDTO =
                testPartitionOperationDTODiscoveryAssertion
                    .findAssertedSinglePartitionOperationDTO();

            assertThat(
                failedPartitionOperationDTO.getPartitionOperationStatus()
            ).isEqualTo(PartitionOperationStatus.FAILED);

            assertThat(failedPartitionOperationDTO.getDefaultPartitionRowCount())
                .isNull();

            assertThat(failedPartitionOperationDTO.getStagingRowCount())
                .isNull();

            UUID failedPublicPartitionOperationId =
                failedPartitionOperationDTO.getPublicPartitionOperationId();

            /*
             * 初回失敗原因を解除。
             */
            testPartitionDefaultFailureTriggerDropFixture
                .dropDeleteDefaultFailureTrigger(
                    partitionTablePropertiesContext
                );

            /*
             * retry #1 はSTAGING削除を失敗させる。
             */
            testPartitionStagingFailureTriggerCreateFixture
                .createDeleteStagingFailureTrigger(
                    partitionTablePropertiesContext
                );

            assertThatThrownBy(
                () ->
                    partitionRecoveryFacadeJdbcService.executeRecoveryAndRecoveryRetry(
                        partitionContextGroup,
                        partitionRangeDTO,
                        failedPublicPartitionOperationId
                    )
            ).isInstanceOf(PartitionException.class);

            /*
             * retry #1 失敗後。
             *
             * audit transaction はrollbackされるので、
             * DEFAULT / target partition は元に戻る。
             *
             * staging delete は失敗しているので
             * STAGINGも残る。
             */
            testPartitionCountDiscoveryAssertion.assertDefaultCount(
                partitionTablePropertiesContext,
                partitionRangeDTO,
                MAX_RECORD_COUNT
            );

            testPartitionCountDiscoveryAssertion.assertStagingCountByPublicPartitionOperationId(
                partitionTablePropertiesContext,
                failedPublicPartitionOperationId,
                MAX_RECORD_COUNT
            );

            testPartitionTargetPartitionNameDiscoveryAssertion.assertThatTargetPartitionDoesNotExist(
                partitionTablePropertiesContext,
                partitionRangeDTO
            );

            PartitionOperationDTO failedAgainPartitionOperationDTO =
                partitionOperationDTODiscoveryJdbcRepository
                    .findPartitionOperationDTOByPublicPartitionOperationId(
                        failedPublicPartitionOperationId
                    );

            assertThat(
                failedAgainPartitionOperationDTO.getPartitionOperationStatus()
            ).isEqualTo(PartitionOperationStatus.FAILED);

            assertThat(failedAgainPartitionOperationDTO.getCompletedAt())
                .isNull();

            assertThat(failedAgainPartitionOperationDTO.getFailedAt())
                .isNotNull();

            /*
             * retry #1 の失敗原因を解除。
             */
            testPartitionStagingFailureTriggerDropFixture
                .dropDeleteStagingFailureTrigger(
                    partitionTablePropertiesContext
                );

            /*
             * retry #2。
             */
            partitionRecoveryFacadeJdbcService.executeRecoveryAndRecoveryRetry(
                partitionContextGroup,
                partitionRangeDTO,
                failedPublicPartitionOperationId
            );

            /*
             * 最終状態。
             */
            testPartitionCountDiscoveryAssertion.assertDefaultCount(
                partitionTablePropertiesContext,
                partitionRangeDTO,
                0
            );

            testPartitionCountDiscoveryAssertion.assertStagingCountByPublicPartitionOperationId(
                partitionTablePropertiesContext,
                failedPublicPartitionOperationId,
                0
            );

            testPartitionTargetPartitionNameDiscoveryAssertion.assertThatTargetPartitionExists(
                partitionTablePropertiesContext,
                partitionRangeDTO
            );

            testPartitionCountDiscoveryAssertion.assertPartitionCount(
                partitionTablePropertiesContext,
                partitionRangeDTO,
                MAX_RECORD_COUNT
            );

            PartitionOperationDTO completedPartitionOperationDTO =
                partitionOperationDTODiscoveryJdbcRepository
                    .findPartitionOperationDTOByPublicPartitionOperationId(
                        failedPublicPartitionOperationId
                    );

            assertThat(
                completedPartitionOperationDTO.getPartitionOperationStatus()
            ).isEqualTo(PartitionOperationStatus.COMPLETED);

            assertThat(completedPartitionOperationDTO.getCompletedAt())
                .isNotNull();

            assertThat(completedPartitionOperationDTO.getFailedAt())
                .isNull();

            assertThat(completedPartitionOperationDTO.getDefaultPartitionRowCount())
                .isEqualTo(MAX_RECORD_COUNT);

            assertThat(completedPartitionOperationDTO.getStagingRowCount())
                .isEqualTo(MAX_RECORD_COUNT);

        } finally {

            testPartitionDefaultFailureTriggerDropFixture
                .dropDeleteDefaultFailureTrigger(
                    partitionTablePropertiesContext
                );

            testPartitionStagingFailureTriggerDropFixture
                .dropDeleteStagingFailureTrigger(
                    partitionTablePropertiesContext
                );
        }
    }

}