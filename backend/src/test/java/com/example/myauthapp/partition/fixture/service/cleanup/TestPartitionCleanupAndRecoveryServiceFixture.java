
package com.example.myauthapp.partition.fixture.service.cleanup;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.context.PartitionOperationPropertiesContext;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.PartitionOperationDTO;
import com.example.myauthapp.partition.dto.PartitionRecoverPartitionResult;
import com.example.myauthapp.partition.dto.TestPartitionDefaultInsertRecord;
import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;
import com.example.myauthapp.partition.enums.type.PartitionConcurrencyType;
import com.example.myauthapp.partition.fixture.tool.insert.TestPartitionInsertRecordFixture;
import com.example.myauthapp.partition.fixture.tool.task.TestPartitionConcurrencyTaskFixture;
import com.example.myauthapp.partition.assertion.discovery.discovery.count.TestPartitionCountGroupDiscoveryAssertion;
import com.example.myauthapp.partition.service.partitionmetadata.PartitionMetadataService;
import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.repository.discovery.name
    .PartitionTargetPartitionNameDiscoveryJdbcRepository;
import com.example.myauthapp.partition.repository.partitionoperation.PartitionOperationJdbcRepository;
import com.example.myauthapp.partition.assertion.discovery.count.TestPartitionCountDiscoveryAssertion;
import com.example.myauthapp.partition.assertion.discovery.name.TestPartitionTargetPartitionNameDiscoveryAssertion;
import com.example.myauthapp.partition.assertion.discovery.partitionrange.TestPartitionRangeDTODiscoveryAssertion;
import com.example.myauthapp.partition.service.recoveryfacade.PartitionRecoveryFacadeJdbcService;
import com.example.myauthapp.partition.service.cleanup.PartitionCleanupService;
import com.example.myauthapp.partition.repository.create.PartitionTargetCreateJdbcRepository;
@Component
public class TestPartitionCleanupAndRecoveryServiceFixture {

    private static final int MAX_RECORD_COUNT = 1000;

    private final PartitionMetadataService partitionMetadataService;

    private final PartitionTargetPartitionNameDiscoveryJdbcRepository
        partitionTargetPartitionNameDiscoveryJdbcRepository;

    private final PartitionOperationJdbcRepository
        partitionOperationJdbcRepository;

    private final PartitionRecoveryFacadeJdbcService
        partitionRecoveryFacadeJdbcService;

    private final PartitionCleanupService
        partitionCleanupService;

    private final PartitionTargetCreateJdbcRepository
        partitionTargetCreateJdbcRepository;

    private final TestPartitionInsertRecordFixture
        testPartitionInsertRecordFixture;

    private final TestPartitionConcurrencyTaskFixture
        testPartitionConcurrencyTaskFixture;

    private final TestPartitionCountGroupDiscoveryAssertion
        testPartitionCountGroupDiscoveryAssertion;

    private final TestPartitionCountDiscoveryAssertion
        testPartitionCountDiscoveryAssertion;

    private final TestPartitionTargetPartitionNameDiscoveryAssertion
        testPartitionTargetPartitionNameDiscoveryAssertion;

    private final TestPartitionRangeDTODiscoveryAssertion
        testPartitionRangeDTODiscoveryAssertion;

    public TestPartitionCleanupAndRecoveryServiceFixture(
        PartitionMetadataService partitionMetadataService,
        PartitionTargetPartitionNameDiscoveryJdbcRepository
            partitionTargetPartitionNameDiscoveryJdbcRepository,
        PartitionOperationJdbcRepository
            partitionOperationJdbcRepository,
        PartitionRecoveryFacadeJdbcService
            partitionRecoveryFacadeJdbcService,
        PartitionCleanupService partitionCleanupService,
        PartitionTargetCreateJdbcRepository
            partitionTargetCreateJdbcRepository,
        TestPartitionInsertRecordFixture testPartitionInsertRecordFixture,
        TestPartitionConcurrencyTaskFixture testPartitionConcurrencyTaskFixture,
        TestPartitionCountGroupDiscoveryAssertion testPartitionCountGroupDiscoveryAssertion,
        TestPartitionCountDiscoveryAssertion testPartitionCountDiscoveryAssertion,
        TestPartitionTargetPartitionNameDiscoveryAssertion
            testPartitionTargetPartitionNameDiscoveryAssertion,
        TestPartitionRangeDTODiscoveryAssertion
            testPartitionRangeDTODiscoveryAssertion
    ) {
        this.partitionMetadataService = partitionMetadataService;
        this.partitionTargetPartitionNameDiscoveryJdbcRepository =
            partitionTargetPartitionNameDiscoveryJdbcRepository;
        this.partitionOperationJdbcRepository =
            partitionOperationJdbcRepository;
        this.partitionRecoveryFacadeJdbcService =
            partitionRecoveryFacadeJdbcService;
        this.partitionCleanupService = partitionCleanupService;
        this.partitionTargetCreateJdbcRepository = partitionTargetCreateJdbcRepository;
        this.testPartitionInsertRecordFixture =
            testPartitionInsertRecordFixture;
        this.testPartitionConcurrencyTaskFixture =
            testPartitionConcurrencyTaskFixture;
        this.testPartitionCountGroupDiscoveryAssertion =
            testPartitionCountGroupDiscoveryAssertion;
        this.testPartitionCountDiscoveryAssertion =
            testPartitionCountDiscoveryAssertion;
        this.testPartitionTargetPartitionNameDiscoveryAssertion =
            testPartitionTargetPartitionNameDiscoveryAssertion;
        this.testPartitionRangeDTODiscoveryAssertion =
            testPartitionRangeDTODiscoveryAssertion;
    }


    public void onCleanupThenRecoveryShouldBeRejected(
        PartitionContextGroup partitionContextGroup,
        TestPartitionDefaultInsertRecord testPartitionDefaultInsertRecord,
        LocalDateTime cleanupBefore
    ) throws PartitionException {

        Objects.requireNonNull(
        partitionContextGroup,
        "partitionContextGroup must not be null"
    );

    Objects.requireNonNull(
        testPartitionDefaultInsertRecord,
        "testPartitionDefaultInsertRecord must not be null"
    );

    Objects.requireNonNull(
        cleanupBefore,
        "cleanupBefore must not be null"
    );

    PartitionTablePropertiesContext partitionTablePropertiesContext =
        partitionContextGroup.getPartitionTablePropertiesContext();

    PartitionOperationPropertiesContext partitionOperationPropertiesContext =
        partitionContextGroup.getPartitionOperationPropertiesContext();

    LocalDateTime targetMonth =
        testPartitionDefaultInsertRecord.getPartitionLocalDateTime();

    // ① Partitionを準備

    PartitionRangeDTO partitionRangeDTO =
        partitionMetadataService.ensurePartition(
            partitionTablePropertiesContext,
            partitionOperationPropertiesContext,
            targetMonth
        );

    // ③ 物理パーティションを作成
    partitionTargetCreateJdbcRepository.createPartition(
        partitionTablePropertiesContext,
        partitionRangeDTO,
        UUID.randomUUID()
    );

    // ④ 物理パーティションの存在確認
    testPartitionTargetPartitionNameDiscoveryAssertion
        .assertThatTargetPartitionExists(
            partitionTablePropertiesContext,
            partitionRangeDTO
        );

    // ⑤ Cleanup
    partitionCleanupService.cleanupPartitionBefore(
        partitionTablePropertiesContext,
        cleanupBefore
    );

    PartitionRangeDTO actualPartitionRangeDTO =
        testPartitionRangeDTODiscoveryAssertion
            .getAssertedSinglePartitionRangeDTOById(
                partitionRangeDTO.getId()
            );

    assertThat(actualPartitionRangeDTO.getDeactivatedAt())
        .isNotNull();

    // ④ Recoveryを実行しても拒否されること

    PartitionOperationDTO partitionOperationDTO =
        partitionOperationJdbcRepository.create(
            partitionOperationPropertiesContext,
            partitionRangeDTO,
            PartitionOperationStatus.RUNNING
        );

    UUID publicPartitionOperationId =
        partitionOperationDTO.getPublicPartitionOperationId();

    assertThatThrownBy(() ->
        partitionRecoveryFacadeJdbcService
            .executeRecoveryAndRecoveryRetry(
                partitionContextGroup,
                partitionRangeDTO,
                publicPartitionOperationId
            )
    ).isInstanceOf(PartitionException.class);

    // ⑤ Cleanup後の状態が維持されていること

    testPartitionTargetPartitionNameDiscoveryAssertion
        .assertThatTargetPartitionDoesNotExist(
            partitionTablePropertiesContext,
            partitionRangeDTO
        );

    actualPartitionRangeDTO =
        testPartitionRangeDTODiscoveryAssertion
            .getAssertedSinglePartitionRangeDTOById(
                partitionRangeDTO.getId()
            );

    assertThat(actualPartitionRangeDTO.getDeactivatedAt())
        .isNotNull();
    }
}
