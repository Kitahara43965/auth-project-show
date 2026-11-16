package com.example.myauthapp.audit.log.job;

import static org.assertj.core.api.Assertions.assertThat;

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
import com.example.myauthapp.partition.fixture.job.TestPartitionJobFixture;
import com.example.myauthapp.partition.factory.TestPartitionDataFactory;


@SpringBootTest
@ActiveProfiles("test")
class AuditLogPartitionJobTest {

    @Autowired
    private AuditLogPartitionJob auditLogPartitionJob;

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @Autowired
    private TestPartitionDataFactory testPartitionDataFactory;

    @Autowired
    private TestPartitionJobFixture testAuditLogPartitionJobFixture;

    @Autowired
    private AuditLogPartitionContextGroupFactory auditLogPartitionContextGroupFactory;

    @Autowired
    private TestAuditLogPartitionDefaultInsertRecordFactory testAuditLogPartitionDefaultInsertRecordFactory;

    @BeforeEach
    void setUp() {
        testDatabaseCleaner.resetAllDatabase();
    }

    @Test
    void executeShouldRecoverAndCleanupPartitionsManyTimes(){
        PartitionContextGroup partitionContextGroup =
            auditLogPartitionContextGroupFactory.create();

        LocalDateTime currentLocalDateTime = LocalDateTime.now();

        TestPartitionDefaultInsertRecord testPartitionDefaultInsertRecord = 
            testAuditLogPartitionDefaultInsertRecordFactory.create(
                currentLocalDateTime,
                currentLocalDateTime,
                AuditEventType.LOGIN,
                AuditResult.SUCCESS,
                testPartitionDataFactory.createPartitionUserId()
            );

        testAuditLogPartitionJobFixture.assertRecoverAndCleanupPartitions(
            partitionContextGroup,
            testPartitionDefaultInsertRecord
        );
    }
}