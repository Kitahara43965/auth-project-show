package com.example.myauthapp.audit.log.multidb;

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
import com.example.myauthapp.partition.multidb.TestPartitionRecoveryPartialFailureServiceFixture;

@SpringBootTest
@ActiveProfiles("test")
class AuditLogPartitionRecoveryPartialFailureServiceTest {

    @Autowired
    private TestPartitionRecoveryPartialFailureServiceFixture
        testPartitionRecoveryPartialFailureServiceFixture;

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @Autowired
    private TestPartitionDataFactory testPartitionDataFactory;

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
    void recoveryShouldFailWhenDeletingStaging() {

        PartitionContextGroup partitionContextGroup =
            auditLogPartitionContextGroupFactory.create();

        LocalDateTime targetMonth =
            LocalDateTime.now();

        TestPartitionDefaultInsertRecord testPartitionDefaultInsertRecord =
            testAuditLogPartitionDefaultInsertRecordFactory.create(
                targetMonth,
                targetMonth,
                AuditEventType.LOGIN,
                AuditResult.SUCCESS,
                testPartitionDataFactory.createPartitionUserId()
            );

        testPartitionRecoveryPartialFailureServiceFixture
            .onDeleteStagingFailure(
                partitionContextGroup,
                testPartitionDefaultInsertRecord
            );
    }

    @Test
    void recoveryShouldFailWhenDeletingDefault() {

        PartitionContextGroup partitionContextGroup =
            auditLogPartitionContextGroupFactory.create();

        LocalDateTime targetMonth =
            LocalDateTime.now();

        TestPartitionDefaultInsertRecord testPartitionDefaultInsertRecord =
            testAuditLogPartitionDefaultInsertRecordFactory.create(
                targetMonth,
                targetMonth,
                AuditEventType.LOGIN,
                AuditResult.SUCCESS,
                testPartitionDataFactory.createPartitionUserId()
            );

        testPartitionRecoveryPartialFailureServiceFixture
            .onDeleteDefaultFailure(
                partitionContextGroup,
                testPartitionDefaultInsertRecord
            );
    }
}