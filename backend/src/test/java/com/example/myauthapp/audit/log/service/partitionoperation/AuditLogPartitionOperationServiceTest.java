package com.example.myauthapp.audit.log.service.paritionoperation;

import java.time.LocalDateTime;

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
import com.example.myauthapp.partition.fixture.service.partitionoperation.TestPartitionOperationServiceFixture;


@SpringBootTest
@ActiveProfiles("test")
class AuditLogPartitionOperationServiceTest {

    @Autowired
    private TestPartitionDataFactory testPartitionDataFactory;

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @Autowired
    private TestPartitionOperationServiceFixture
        testPartitionOperationServiceFixture;

    @Autowired
    private AuditLogPartitionContextGroupFactory
        auditLogPartitionContextGroupFactory;

    @Autowired
    private TestAuditLogPartitionDefaultInsertRecordFactory
        testAuditLogPartitionDefaultInsertRecordFactory;

    @BeforeEach
    void setup() {
        testDatabaseCleaner.resetAllDatabase();
    }

    @Test
    void recoveryShouldFailAndCreateFailedHistory() {

        PartitionContextGroup partitionContextGroup =
            auditLogPartitionContextGroupFactory.create();

        LocalDateTime targetMonth =
            LocalDateTime.now().minusMonths(1);

        TestPartitionDefaultInsertRecord testPartitionDefaultInsertRecord = 
            testAuditLogPartitionDefaultInsertRecordFactory.create(
                targetMonth,
                targetMonth,
                AuditEventType.LOGIN,
                AuditResult.SUCCESS,
                testPartitionDataFactory.createPartitionUserId()
            );

        testPartitionOperationServiceFixture
            .onRecoveryShouldCompleteAndCreateCompletedHistory(
                partitionContextGroup,
                testPartitionDefaultInsertRecord
            );
    }
}