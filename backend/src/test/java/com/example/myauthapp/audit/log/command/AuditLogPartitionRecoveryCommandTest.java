package com.example.myauthapp.audit.log.command;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.log.factory.AuditLogPartitionContextGroupFactory;
import com.example.myauthapp.audit.result.AuditResult;
import com.example.myauthapp.database.cleaner.TestDatabaseCleaner;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.TestPartitionDefaultInsertRecord;
import com.example.myauthapp.partition.factory.TestPartitionDataFactory;
import com.example.myauthapp.partition.fixture.command.TestPartitionRecoveryCommandFixture;
import com.example.myauthapp.audit.log.factory.TestAuditLogPartitionDefaultInsertRecordFactory;

@SpringBootTest
@ActiveProfiles("test")
class AuditLogPartitionRecoveryCommandTest {

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @Autowired
    private TestPartitionRecoveryCommandFixture testPartitionRecoveryCommandFixture;

    @Autowired
    private TestPartitionDataFactory testPartitionDataFactory;

    @Autowired
    private AuditLogPartitionContextGroupFactory auditLogPartitionContextGroupFactory;

    @Autowired
    private TestAuditLogPartitionDefaultInsertRecordFactory testAuditLogPartitionDefaultInsertRecordFactory;

    @BeforeEach
    void setUp() {
        testDatabaseCleaner.resetAllDatabase();
    }

    @Test
    void runShouldRecoverWhenRecoveryCommandIsPassed() {

        PartitionContextGroup partitionContextGroup =
            auditLogPartitionContextGroupFactory.create();

        LocalDateTime targetMonth = LocalDateTime.now();

        TestPartitionDefaultInsertRecord testPartitionDefaultInsertRecord = 
            testAuditLogPartitionDefaultInsertRecordFactory.create(
                targetMonth,
                targetMonth,
                AuditEventType.LOGIN,
                AuditResult.SUCCESS,
                testPartitionDataFactory.createPartitionUserId()
            );

        testPartitionRecoveryCommandFixture
            .onRunShouldRecoverWhenRecoveryCommandIsPassed(
                partitionContextGroup,
                testPartitionDefaultInsertRecord
            );
    }
}