package com.example.myauthapp.partition.multidb;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.example.myauthapp.partition.context.PartitionOperationPropertiesContext;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.partition.dto.TestPartitionDefaultInsertRecord;
import com.example.myauthapp.partition.fixture.tool.failure.TestPartitionDefaultFailureTriggerCreateFixture;
import com.example.myauthapp.partition.fixture.tool.failure.TestPartitionStagingFailureTriggerCreateFixture;
import com.example.myauthapp.partition.fixture.tool.failure.TestPartitionDefaultFailureTriggerDropFixture;
import com.example.myauthapp.partition.fixture.tool.failure.TestPartitionStagingFailureTriggerDropFixture;
import com.example.myauthapp.partition.fixture.tool.insert.TestPartitionInsertRecordFixture;
import com.example.myauthapp.partition.service.partitionmetadata.PartitionMetadataService;
import com.example.myauthapp.partition.service.recovery.PartitionRecoveryService;
import com.example.myauthapp.partition.assertion.discovery.count.TestPartitionCountDiscoveryAssertion;
import com.example.myauthapp.partition.assertion.discovery.name.TestPartitionTargetPartitionNameDiscoveryAssertion;
import com.example.myauthapp.web.partition.exception.PartitionException;

@Component
public class TestPartitionRecoveryPartialFailureServiceFixture {

    private enum PartitionRecoveryPartialFailureType {
        DEFAULT,
        STAGING
    }

    private static final int MAX_RECORD_COUNT = 1000;

    private final PartitionMetadataService partitionMetadataService;
    private final PartitionRecoveryService partitionRecoveryService;

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

    private final TestPartitionCountDiscoveryAssertion
        testPartitionCountDiscoveryAssertion;

    private final TestPartitionTargetPartitionNameDiscoveryAssertion
        testPartitionTargetPartitionNameDiscoveryAssertion;

    public TestPartitionRecoveryPartialFailureServiceFixture(
        PartitionMetadataService partitionMetadataService,
        PartitionRecoveryService partitionRecoveryService,
        TestPartitionInsertRecordFixture testPartitionInsertRecordFixture,
        TestPartitionDefaultFailureTriggerCreateFixture
            testPartitionDefaultFailureTriggerCreateFixture,
        TestPartitionStagingFailureTriggerCreateFixture
            testPartitionStagingFailureTriggerCreateFixture,
        TestPartitionDefaultFailureTriggerDropFixture
            testPartitionDefaultFailureTriggerDropFixture,
        TestPartitionStagingFailureTriggerDropFixture
            testPartitionStagingFailureTriggerDropFixture,
        TestPartitionCountDiscoveryAssertion
            testPartitionCountDiscoveryAssertion,
        TestPartitionTargetPartitionNameDiscoveryAssertion
            testPartitionTargetPartitionNameDiscoveryAssertion
    ) {
        this.partitionMetadataService =
            partitionMetadataService;
        this.partitionRecoveryService =
            partitionRecoveryService;
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
        this.testPartitionCountDiscoveryAssertion =
            testPartitionCountDiscoveryAssertion;
        this.testPartitionTargetPartitionNameDiscoveryAssertion =
            testPartitionTargetPartitionNameDiscoveryAssertion;
    }


    public void executePartialFailureCheck(
        PartitionContextGroup partitionContextGroup,
        TestPartitionDefaultInsertRecord testPartitionDefaultInsertRecord,
        PartitionRecoveryPartialFailureType partitionRecoveryPartialFailureType
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
            if(
                partitionRecoveryPartialFailureType
                == PartitionRecoveryPartialFailureType.STAGING
            ){
                testPartitionStagingFailureTriggerCreateFixture
                    .createDeleteStagingFailureTrigger(
                        partitionTablePropertiesContext
                    );
            }else{
                testPartitionDefaultFailureTriggerCreateFixture
                    .createDeleteDefaultFailureTrigger(
                        partitionTablePropertiesContext
                    );
            }

            testPartitionInsertRecordFixture.insertTestPartitionDefaultInsertRecord(
                partitionTablePropertiesContext,
                testPartitionDefaultInsertRecord,
                MAX_RECORD_COUNT
            );

            testPartitionCountDiscoveryAssertion.assertDefaultCount(
                partitionTablePropertiesContext,
                partitionRangeDTO,
                MAX_RECORD_COUNT
            );;

            assertThatThrownBy(() ->
                partitionRecoveryService.recoverAllPartitions(
                    partitionContextGroup
                )
            ).isInstanceOf(PartitionException.class);

            /*
             * STAGING INSERT は成功。
             */
            testPartitionCountDiscoveryAssertion.assertStagingCount(
                partitionTablePropertiesContext,
                MAX_RECORD_COUNT
            );

            /*
             * DEFAULT DELETE は失敗しているので、
             * audit transaction は rollback。
             */
            testPartitionCountDiscoveryAssertion.assertDefaultCount(
                partitionTablePropertiesContext,
                partitionRangeDTO,
                MAX_RECORD_COUNT
            );

            testPartitionTargetPartitionNameDiscoveryAssertion
                .assertThatTargetPartitionDoesNotExist(
                    partitionTablePropertiesContext,
                    partitionRangeDTO
                );

        } finally {
            if(
                partitionRecoveryPartialFailureType
                == PartitionRecoveryPartialFailureType.STAGING
            ){
                testPartitionStagingFailureTriggerDropFixture
                    .dropDeleteStagingFailureTrigger(
                        partitionTablePropertiesContext
                    );
            }else{
                testPartitionDefaultFailureTriggerDropFixture
                    .dropDeleteDefaultFailureTrigger(
                        partitionTablePropertiesContext
                    );
            }
        }
    }

    public void onDeleteDefaultFailure(
        PartitionContextGroup partitionContextGroup,
        TestPartitionDefaultInsertRecord testPartitionDefaultInsertRecord
    ){
        executePartialFailureCheck(
            partitionContextGroup,
            testPartitionDefaultInsertRecord,
            PartitionRecoveryPartialFailureType.DEFAULT
        );
    }

    public void onDeleteStagingFailure(
        PartitionContextGroup partitionContextGroup,
        TestPartitionDefaultInsertRecord testPartitionDefaultInsertRecord
    ){
        executePartialFailureCheck(
            partitionContextGroup,
            testPartitionDefaultInsertRecord,
            PartitionRecoveryPartialFailureType.STAGING
        );
    }

}