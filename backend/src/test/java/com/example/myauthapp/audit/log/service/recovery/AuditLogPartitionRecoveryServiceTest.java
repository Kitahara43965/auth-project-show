package com.example.myauthapp.audit.log.service.recoveryretry;

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
import com.example.myauthapp.partition.fixture.service.recovery.TestPartitionRecoveryServiceFixture;


@SpringBootTest
@ActiveProfiles("test")
class AuditLogPartitionRecoveryServiceTest {

    private final int PREV_LOOP_TIME_NUMBER = 1;

    private final int CURRENT_LOOP_TIME_NUMBER = 2;

    private final int NEXT_LOOP_TIME_NUMBER = 3;

    private final int MAX_LOOP_TIME_NUMBER = 3;

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @Autowired
    private TestPartitionRecoveryServiceFixture testPartitionRecoveryServiceFixture;

    @Autowired
    private TestPartitionDataFactory testPartitionDataFactory;

    @Autowired
    private AuditLogPartitionContextGroupFactory auditLogPartitionContextGroupFactory;

    @Autowired
    private TestAuditLogPartitionDefaultInsertRecordFactory testAuditLogPartitionDefaultInsertRecordFactory;

    @BeforeEach
    void setup() {
        testDatabaseCleaner.resetAllDatabase();
    }

    @Test
    void recoverAllShouldMoveDefaultAuditLogPartition() {

        PartitionContextGroup partitionContextGroup =
            auditLogPartitionContextGroupFactory.create();
        Long actorId = testPartitionDataFactory.createPartitionUserId();
        int loopTimeNumber = 0;
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        LocalDateTime targetMonth = null;
        TestPartitionDefaultInsertRecord testPartitionDefaultInsertRecord = null;

        for(
            loopTimeNumber = 1;
            loopTimeNumber <= MAX_LOOP_TIME_NUMBER;
            loopTimeNumber ++
        ){
            if(loopTimeNumber == PREV_LOOP_TIME_NUMBER){
                targetMonth = localDateTimeNow.minusMonths(1);
            }else if(loopTimeNumber == CURRENT_LOOP_TIME_NUMBER){
                targetMonth = localDateTimeNow;
            }else if(loopTimeNumber == NEXT_LOOP_TIME_NUMBER){
                targetMonth = localDateTimeNow.plusMonths(1);
            }else{//loopTimeNumber
                targetMonth = localDateTimeNow;
            }//loopTimeNumber

            testPartitionDefaultInsertRecord = 
                testAuditLogPartitionDefaultInsertRecordFactory.create(
                    targetMonth,
                    targetMonth,
                    AuditEventType.LOGIN,
                    AuditResult.SUCCESS,
                    actorId
                );

            testPartitionRecoveryServiceFixture
                .onTestPartitionRecovery(
                    partitionContextGroup,
                    testPartitionDefaultInsertRecord
                );
        }
    }
}