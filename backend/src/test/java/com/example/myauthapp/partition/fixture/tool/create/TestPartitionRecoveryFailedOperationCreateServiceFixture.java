package com.example.myauthapp.partition.fixture.tool.create;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.dao.DataAccessException;
import static org.assertj.core.api.Assertions.assertThat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionOperationDTO;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.fixture.tool.failure.TestPartitionDefaultFailureTriggerDropFixture;
import com.example.myauthapp.partition.fixture.tool.failure.TestPartitionDefaultFailureTriggerCreateFixture;
import com.example.myauthapp.partition.repository.discovery.partitionoperation.PartitionOperationDTODiscoveryJdbcRepository;
import com.example.myauthapp.partition.service.recovery.PartitionRecoveryService;
import com.example.myauthapp.partition.enums.status.PartitionOperationStatus;
import com.example.myauthapp.partition.assertion.discovery.count.TestPartitionCountDiscoveryAssertion;
import com.example.myauthapp.partition.assertion.discovery.name.TestPartitionTargetPartitionNameDiscoveryAssertion;
import com.example.myauthapp.partition.assertion.discovery.partitionoperation.TestPartitionOperationDTODiscoveryAssertion;
import com.example.myauthapp.partition.fixture.tool.insert.TestPartitionInsertRecordFixture;
import com.example.myauthapp.web.partition.exception.PartitionException;
import com.example.myauthapp.partition.error.PartitionRecoveryPartitionError;
import com.example.myauthapp.partition.message.PartitionRecoveryPartitionMessage;

@Component
public class TestPartitionRecoveryFailedOperationCreateServiceFixture {

    private final PartitionRecoveryService partitionRecoveryService;

    private final PartitionOperationDTODiscoveryJdbcRepository
        afterPartitionOperationDTODiscoveryJdbcRepository;

    private final TestPartitionCountDiscoveryAssertion testPartitionCountDiscoveryAssertion;
    private final TestPartitionTargetPartitionNameDiscoveryAssertion testPartitionTargetPartitionNameDiscoveryAssertion;
    private final TestPartitionOperationDTODiscoveryAssertion testPartitionOperationDTODiscoveryAssertion;
    private final TestPartitionInsertRecordFixture testPartitionInsertRecordFixture;

    private final TestPartitionDefaultFailureTriggerDropFixture
        testPartitionDefaultFailureTriggerDropFixture;

    private final TestPartitionDefaultFailureTriggerCreateFixture
        testPartitionDefaultFailureTriggerCreateFixture;

     private static final Logger logger =
        LoggerFactory.getLogger(
            TestPartitionRecoveryFailedOperationCreateServiceFixture.class
        );

    public TestPartitionRecoveryFailedOperationCreateServiceFixture(
        PartitionRecoveryService partitionRecoveryService,
        PartitionOperationDTODiscoveryJdbcRepository
            afterPartitionOperationDTODiscoveryJdbcRepository,
        TestPartitionCountDiscoveryAssertion testPartitionCountDiscoveryAssertion,
        TestPartitionTargetPartitionNameDiscoveryAssertion testPartitionTargetPartitionNameDiscoveryAssertion,
        TestPartitionOperationDTODiscoveryAssertion testPartitionOperationDTODiscoveryAssertion,
        TestPartitionInsertRecordFixture testPartitionInsertRecordFixture,
        TestPartitionDefaultFailureTriggerDropFixture
            testPartitionDefaultFailureTriggerDropFixture,
        TestPartitionDefaultFailureTriggerCreateFixture
            testPartitionDefaultFailureTriggerCreateFixture
    ) {
        this.partitionRecoveryService =
            partitionRecoveryService;

        this.afterPartitionOperationDTODiscoveryJdbcRepository =
            afterPartitionOperationDTODiscoveryJdbcRepository;

        this.testPartitionCountDiscoveryAssertion = testPartitionCountDiscoveryAssertion;
        this.testPartitionTargetPartitionNameDiscoveryAssertion = testPartitionTargetPartitionNameDiscoveryAssertion;
        this.testPartitionOperationDTODiscoveryAssertion = testPartitionOperationDTODiscoveryAssertion;
        this.testPartitionInsertRecordFixture = testPartitionInsertRecordFixture;

        this.testPartitionDefaultFailureTriggerDropFixture =
            testPartitionDefaultFailureTriggerDropFixture;

        this.testPartitionDefaultFailureTriggerCreateFixture =
            testPartitionDefaultFailureTriggerCreateFixture;
    }

    public PartitionOperationDTO createFailedPartitionOperationDTO(
        PartitionContextGroup partitionContextGroup,
        LocalDateTime targetMonth
    ) {
        Objects.requireNonNull(
            partitionContextGroup,
            "partitionContextGroup must not be null"
        );

        Objects.requireNonNull(
            targetMonth,
            "targetMonth must not be null"
        );

        PartitionTablePropertiesContext
            partitionTablePropertiesContext =
                partitionContextGroup
                    .getPartitionTablePropertiesContext();

        List<PartitionOperationDTO> beforePartitionOperationDTOs =
            afterPartitionOperationDTODiscoveryJdbcRepository
                .findPartitionOperationDTOs();

        int beforePartitionOperationDTOsCount = beforePartitionOperationDTOs.size();

        testPartitionDefaultFailureTriggerCreateFixture
            .createDeleteDefaultFailureTrigger(
                partitionTablePropertiesContext
            );

        try {
            partitionRecoveryService.performPartitionRecovery(
                partitionContextGroup,
                targetMonth
            );

            throw new AssertionError(
                "Partition recovery should have failed."
            );

        } catch (PartitionException exception) {
            logger.info(
                PartitionRecoveryPartitionMessage
                    .EXPECTED_PARTITION_RECOVERY_FAILURE_OCCURRED
                    .getMessage()
                    .formatted(exception.getMessage())
            );

        } finally {
            testPartitionDefaultFailureTriggerDropFixture
                .dropDeleteDefaultFailureTrigger(
                    partitionTablePropertiesContext
                );
        }

        List<PartitionOperationDTO> afterPartitionOperationDTOs =
            afterPartitionOperationDTODiscoveryJdbcRepository
                .findPartitionOperationDTOs();

        int afterPartitionOperationDTOsCount = afterPartitionOperationDTOs.size();

        if (afterPartitionOperationDTOsCount != beforePartitionOperationDTOsCount + 1) {
            throw new PartitionException(
                PartitionRecoveryPartitionError
                    .EXACTLY_ONE_PARTITION_OPERATION_MUST_BE_CREATED,
                beforePartitionOperationDTOsCount,
                afterPartitionOperationDTOs.size()
            );
        }

        PartitionOperationDTO afterPartitionOperationDTO =
            afterPartitionOperationDTOs.get(afterPartitionOperationDTOsCount - 1);

        if (afterPartitionOperationDTO.getPartitionOperationStatus()
                != PartitionOperationStatus.FAILED) {
            throw new PartitionException(
                PartitionRecoveryPartitionError
                    .PARTITION_OPERATION_MUST_BE_FAILED,
                afterPartitionOperationDTO.getPublicPartitionOperationId(),
                afterPartitionOperationDTO.getPartitionOperationStatus()
            );
        }

        return afterPartitionOperationDTO;
    }
}