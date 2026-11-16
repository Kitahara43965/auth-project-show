package com.example.myauthapp.audit.log.service.cleanup;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.result.AuditResult;
import com.example.myauthapp.audit.log.factory.AuditLogPartitionContextGroupFactory;
import com.example.myauthapp.audit.log.factory.TestAuditLogPartitionDefaultInsertRecordFactory;
import com.example.myauthapp.database.cleaner.TestDatabaseCleaner;
import com.example.myauthapp.partition.dto.PartitionContextGroup;
import com.example.myauthapp.partition.dto.TestPartitionDefaultInsertRecord;
import com.example.myauthapp.partition.factory.TestPartitionDataFactory;
import com.example.myauthapp.partition.fixture.service.cleanup
    .TestPartitionRecoveryAndCleanupServiceFixture;

@SpringBootTest
@ActiveProfiles("test")
class AuditLogPartitionRecoveryAndCleanupServiceTest {

    @Autowired
    private TestPartitionRecoveryAndCleanupServiceFixture
        testPartitionRecoveryAndCleanupServiceFixture;

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @Autowired
    private TestPartitionDataFactory testPartitionDataFactory;

    @Autowired
    private AuditLogPartitionContextGroupFactory auditLogPartitionContextGroupFactory;

    @Autowired
    private TestAuditLogPartitionDefaultInsertRecordFactory
        testAuditLogPartitionDefaultInsertRecordFactory;

    @BeforeEach
    void setup() {
        testDatabaseCleaner.resetAllDatabase();
    }

    @Test
    void recoveryThenCleanupShouldWork(){

        PartitionContextGroup partitionContextGroup =
            auditLogPartitionContextGroupFactory.create();

        LocalDateTime targetMonth = LocalDateTime.now();

        LocalDateTime cleanupBefore = targetMonth.plusMonths(1);

        TestPartitionDefaultInsertRecord testPartitionDefaultInsertRecord =
            testAuditLogPartitionDefaultInsertRecordFactory.create(
                targetMonth,
                targetMonth,
                AuditEventType.LOGIN,
                AuditResult.SUCCESS,
                testPartitionDataFactory.createPartitionUserId()
            );

        testPartitionRecoveryAndCleanupServiceFixture
            .onRecoveryThenCleanupShouldWork(
                partitionContextGroup,
                testPartitionDefaultInsertRecord,
                cleanupBefore
            );
    }//recoveryThenCleanupShouldWork

}