package com.example.myauthapp.audit.auditlog.partition.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import com.example.myauthapp.user.entity.User;
import com.example.myauthapp.support.factory.TestUserFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import com.example.myauthapp.support.fixture.TestAuditLogFixture;
import com.example.myauthapp.support.database.cleaner.TestDatabaseCleaner;
import com.example.myauthapp.audit.event.AuditEventType;
import com.example.myauthapp.audit.result.AuditResult;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuditLogPartitionRecoveryServiceTest {

    @Autowired
    private AuditLogPartitionRecoveryService recoveryService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestUserFactory testUserFactory;

    @Autowired
    private TestDatabaseCleaner testDatabaseCleaner;

    @Autowired
    private TestAuditLogFixture testAuditLogFixture;

    @BeforeEach
    void setup() {
        testDatabaseCleaner.deleteAllDatabase();
    }

    @Test
    void recoverAllShouldMoveDefaultLogsToMonthlyPartition() {
        // given
        LocalDateTime occurredAt = LocalDateTime.now();
        int year = occurredAt.getYear();
        int month = occurredAt.getMonthValue();
        int day = occurredAt.getDayOfMonth();
        int hour = occurredAt.getHour();
        int minute = occurredAt.getMinute();
        int second = occurredAt.getSecond();
        String partitionName =
            "audit_logs_" + year + "_" + String.format("%02d", month);

        User user = testUserFactory.testCreateUser();

        LocalDateTime startTimeAt =
            occurredAt.withDayOfMonth(1)
                    .toLocalDate()
                    .atStartOfDay();

        LocalDateTime endTimeAt =
            occurredAt.plusMonths(1)
                    .withDayOfMonth(1)
                    .toLocalDate()
                    .atStartOfDay();

        testAuditLogFixture.insertDefaultAuditLog(
            occurredAt,
            occurredAt,
            AuditEventType.LOGIN,
            user.getId(),
            AuditResult.SUCCESS
        );

        // DEFAULTにデータが存在することを確認
        Integer defaultCount =
            testAuditLogFixture.countDefaultLogs(
                startTimeAt,
                endTimeAt
            );

        assertThat(defaultCount).isEqualTo(1);

        // when
        recoveryService.recoverAll();

        // then

        // DEFAULTから移動されたことを確認
        Integer afterDefaultCount =
            testAuditLogFixture.countDefaultLogs(
                startTimeAt,
                endTimeAt
            );

        assertThat(afterDefaultCount).isZero();

        // 月別Partitionに存在することを確認
        Integer partitionCount =
            testAuditLogFixture.countPartitionLogs(
                startTimeAt,
                endTimeAt
            );

        assertThat(partitionCount).isEqualTo(1);
    }
}
