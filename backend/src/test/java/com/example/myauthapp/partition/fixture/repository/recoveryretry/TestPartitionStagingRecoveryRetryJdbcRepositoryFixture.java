package com.example.myauthapp.partition.fixture.repository.recoveryretry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.context.PartitionOperationPropertiesContext;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionOperationDTO;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.TestPartitionDefaultInsertRecord;
import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;
import com.example.myauthapp.partition.fixture.tool.create.TestPartitionRecoveryFailedOperationCreateServiceFixture;
import com.example.myauthapp.partition.fixture.tool.insert.TestPartitionInsertRecordFixture;
import com.example.myauthapp.partition.repository.discovery.partitionoperation.PartitionOperationDTODiscoveryJdbcRepository;
import com.example.myauthapp.partition.repository.partitionoperation.PartitionOperationJdbcRepository;
import com.example.myauthapp.partition.repository.recoveryretry.PartitionStagingRecoveryRetryJdbcRepository;
import com.example.myauthapp.partition.service.partitionmetadata.PartitionMetadataService;
import com.example.myauthapp.partition.service.recoveryfacade.PartitionRecoveryFacadeJdbcService;
import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionDatabasePartitionError;
@Component
public class TestPartitionStagingRecoveryRetryJdbcRepositoryFixture {

    private static final int MAX_RECORD_COUNT = 1000;

    private final PartitionStagingRecoveryRetryJdbcRepository partitionStagingRecoveryRetryJdbcRepository;
    private final PartitionOperationDTODiscoveryJdbcRepository
        partitionOperationDTODiscoveryJdbcRepository;
    private final PartitionMetadataService partitionMetadataService;
    private final PartitionRecoveryFacadeJdbcService partitionRecoveryRetryJdbcService;
    private final PartitionOperationJdbcRepository partitionOperationJdbcRepository;
    private final TestPartitionInsertRecordFixture testPartitionInsertRecordFixture;
    private final TestPartitionRecoveryFailedOperationCreateServiceFixture
        testPartitionRecoveryFailedOperationCreateServiceFixture;

    public TestPartitionStagingRecoveryRetryJdbcRepositoryFixture(
        PartitionStagingRecoveryRetryJdbcRepository partitionStagingRecoveryRetryJdbcRepository,
        PartitionOperationDTODiscoveryJdbcRepository partitionOperationDTODiscoveryJdbcRepository,
        PartitionMetadataService partitionMetadataService,
        PartitionRecoveryFacadeJdbcService partitionRecoveryRetryJdbcService,
        PartitionOperationJdbcRepository partitionOperationJdbcRepository,
        TestPartitionInsertRecordFixture testPartitionInsertRecordFixture,
        TestPartitionRecoveryFailedOperationCreateServiceFixture
            testPartitionRecoveryFailedOperationCreateServiceFixture
    ) {
        this.partitionStagingRecoveryRetryJdbcRepository =
            partitionStagingRecoveryRetryJdbcRepository;
        this.partitionOperationDTODiscoveryJdbcRepository =
            partitionOperationDTODiscoveryJdbcRepository;
        this.partitionMetadataService = partitionMetadataService;
        this.partitionRecoveryRetryJdbcService =
            partitionRecoveryRetryJdbcService;
        this.partitionOperationJdbcRepository =
            partitionOperationJdbcRepository;
        this.testPartitionInsertRecordFixture =
            testPartitionInsertRecordFixture;
        this.testPartitionRecoveryFailedOperationCreateServiceFixture =
            testPartitionRecoveryFailedOperationCreateServiceFixture;
    }

    /**
     * FAILED 状態の操作を RUNNING に遷移させられることを確認する。
     */
    public void onRetryStartChangesFailedToRunning(
        PartitionContextGroup partitionContextGroup,
        TestPartitionDefaultInsertRecord testPartitionDefaultInsertRecord
    ) {
        PartitionTablePropertiesContext partitionTablePropertiesContext =
            partitionContextGroup.getPartitionTablePropertiesContext();

        PartitionOperationPropertiesContext partitionOperationPropertiesContext =
            partitionContextGroup.getPartitionOperationPropertiesContext();

        LocalDateTime targetMonth =
            testPartitionDefaultInsertRecord.getPartitionLocalDateTime();

        partitionMetadataService.ensurePartition(
            partitionTablePropertiesContext,
            partitionOperationPropertiesContext,
            targetMonth
        );

        testPartitionInsertRecordFixture.insertTestPartitionDefaultInsertRecord(
            partitionTablePropertiesContext,
            testPartitionDefaultInsertRecord,
            MAX_RECORD_COUNT
        );

        PartitionOperationDTO failedPartitionOperationDTO =
            testPartitionRecoveryFailedOperationCreateServiceFixture
                .createFailedPartitionOperationDTO(
                    partitionContextGroup,
                    targetMonth
                );

        partitionStagingRecoveryRetryJdbcRepository.startRetry(
            failedPartitionOperationDTO.getPublicPartitionOperationId(),
            LocalDateTime.now()
        );

        PartitionOperationDTO actual =
            partitionOperationDTODiscoveryJdbcRepository
                .findPartitionOperationDTOByPublicPartitionOperationId(
                    failedPartitionOperationDTO.getPublicPartitionOperationId()
                );

        assertThat(actual.getPartitionOperationStatus())
            .isEqualTo(PartitionOperationStatus.RUNNING);
    }

    /**
     * COMPLETED 状態の操作を retry しても、COMPLETED のままであることを確認する。
     */
    public void onRetryCompletedOperationShouldRemainCompleted(
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

        PartitionOperationDTO partitionOperationDTO =
            partitionOperationJdbcRepository.create(
                partitionOperationPropertiesContext,
                partitionRangeDTO,
                PartitionOperationStatus.COMPLETED
            );

        UUID publicPartitionOperationId =
            partitionOperationDTO.getPublicPartitionOperationId();

        assertThatThrownBy(
            () -> partitionStagingRecoveryRetryJdbcRepository.startRetry(
                publicPartitionOperationId,
                LocalDateTime.now()
            )
        )
        .isInstanceOf(PartitionException.class)
        .hasMessageContaining(
            PartitionDatabasePartitionError
                .PARTITION_OPERATION_IS_NOT_AVAILABLE_FOR_RETRY
                .format(publicPartitionOperationId)
        );

        PartitionOperationDTO actualPartitionOperationDTO =
            partitionOperationDTODiscoveryJdbcRepository
                .findPartitionOperationDTOByPublicPartitionOperationId(
                    publicPartitionOperationId
                );

        assertThat(actualPartitionOperationDTO.getPartitionOperationStatus())
            .isEqualTo(PartitionOperationStatus.COMPLETED);
    }

    /**
     * RUNNING 状態の操作は retry できず、RUNNING のままであることを確認する。
     */
    public void onRetryRunningOperationShouldBeRejected(
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

        PartitionOperationDTO partitionOperationDTO =
            partitionOperationJdbcRepository.create(
                partitionOperationPropertiesContext,
                partitionRangeDTO,
                PartitionOperationStatus.RUNNING
            );

        UUID publicPartitionOperationId =
            partitionOperationDTO.getPublicPartitionOperationId();

        assertThatThrownBy(
            () -> partitionStagingRecoveryRetryJdbcRepository.startRetry(
                publicPartitionOperationId,
                LocalDateTime.now()
            )
        )
        .isInstanceOf(PartitionException.class)
        .hasMessageContaining(
            PartitionDatabasePartitionError
                .PARTITION_OPERATION_IS_NOT_AVAILABLE_FOR_RETRY
                .format(publicPartitionOperationId)
        );

        PartitionOperationDTO actualPartitionOperationDTO =
            partitionOperationDTODiscoveryJdbcRepository
                .findPartitionOperationDTOByPublicPartitionOperationId(
                    publicPartitionOperationId
                );

        assertThat(actualPartitionOperationDTO.getPartitionOperationStatus())
            .isEqualTo(PartitionOperationStatus.RUNNING);
    }
}