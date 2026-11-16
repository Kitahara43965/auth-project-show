package com.example.myauthapp.audit.log.repository.create;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.myauthapp.partition.dto.TestPartitionStagingInsertRecord;
import com.example.myauthapp.partition.dto.PartitionRangeDTO;
import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.result.AuditResult;
import com.example.myauthapp.audit.log.factory.AuditLogPartitionContextGroupFactory;
import com.example.myauthapp.audit.log.factory.TestAuditLogPartitionStagingInsertRecordFactory;
import com.example.myauthapp.database.cleaner.TestDatabaseCleaner;

import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.factory.TestPartitionDataFactory;
import com.example.myauthapp.partition.fixture.repository.create.TestPartitionTargetCreateJdbcRepositoryFixture;


@SpringBootTest
@ActiveProfiles("test")
class AuditLogPartitionTargetCreateJdbcRepositoryTest {

    private final static int MAX_RECORD_COUNT = 1000;

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @Autowired
    private TestPartitionDataFactory testPartitionDataFactory;

    @Autowired
    private TestPartitionTargetCreateJdbcRepositoryFixture testPartitionTargetCreateJdbcRepositoryFixture;

    @Autowired
    private AuditLogPartitionContextGroupFactory auditLogPartitionContextGroupFactory;

    @Autowired
    private TestAuditLogPartitionStagingInsertRecordFactory testAuditLogPartitionStagingInsertRecordFactory;

    @BeforeEach
    void setup() {
        testDatabaseCleaner.resetAllDatabase();
    }

    @Test
    void moveStagingToPartitionShouldMoveOnlySpecifiedOperationId() {

        PartitionContextGroup partitionContextGroup =
            auditLogPartitionContextGroupFactory.create();

        LocalDateTime targetMonth =
            LocalDateTime.now().minusMonths(1);

        List<TestPartitionStagingInsertRecord>
            testPartitionStagingInsertRecords = new ArrayList<>();

        for (int i = 1; i <= MAX_RECORD_COUNT; i++) {

            testPartitionStagingInsertRecords.add(
                testAuditLogPartitionStagingInsertRecordFactory.create(
                    (long) i,
                    targetMonth,
                    targetMonth,
                    AuditEventType.LOGIN,
                    AuditResult.SUCCESS,
                    testPartitionDataFactory.createPartitionUserId()
                )
            );
        }

        testPartitionTargetCreateJdbcRepositoryFixture
            .onMoveRecoveryToPartitionShouldMoveOnlySpecifiedOperationId(
                partitionContextGroup,
                testPartitionStagingInsertRecords
            );
    }
}