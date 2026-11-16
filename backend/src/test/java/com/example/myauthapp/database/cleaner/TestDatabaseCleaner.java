package com.example.myauthapp.database.cleaner;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.example.myauthapp.audit.log.repository.delete.AuditLogDeleteJdbcRepository;
import com.example.myauthapp.token.refreshtoken.repository.delete.RefreshTokenFamilyDeleteJdbcRepository;
import com.example.myauthapp.token.refreshtoken.repository.delete.RefreshTokenDeleteJdbcRepository;
import com.example.myauthapp.user.repository.delete.UserDeleteJdbcRepository;
import com.example.myauthapp.partition.repository.database.delete.TestPartitionTargetDatabaseDeleteRepository;
import com.example.myauthapp.audit.log.config.AuditLogPartitionTableProperties;
import com.example.myauthapp.partition.resolver.PartitionNamingResolver;
import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;
import com.example.myauthapp.partition.factory.PartitionTablePropertiesContextFactory;
import com.example.myauthapp.partition.config.TestPartitionRecoveryFailureProperties;
import com.example.myauthapp.partition.fixture.tool.database.cleanup.TestPartitionDatabaseTriggerCleanupFixture;
import com.example.myauthapp.partition.fixture.tool.database.delete.TestPartitionStagingDatabaseDeleteFixture;

@Component
public class TestDatabaseCleaner {

    private final RefreshTokenDeleteJdbcRepository refreshTokenDeleteJdbcRepository;
    private final RefreshTokenFamilyDeleteJdbcRepository refreshTokenFamilyDeleteJdbcRepository;
    private final AuditLogDeleteJdbcRepository auditLogDeleteJdbcRepository;
    private final UserDeleteJdbcRepository userDeleteJdbcRepository;
    private final TestPartitionTargetDatabaseDeleteRepository testPartitionTargetDatabaseDeleteRepository;
    private final AuditLogPartitionTableProperties auditLogPartitionTableProperties;
    private final PartitionNamingResolver partitionNamingResolver;
    private final PartitionTablePropertiesContextFactory partitionTablePropertiesContextFactory;
    private final TestPartitionRecoveryFailureProperties testPartitionRecoveryFailureProperties;
    private final TestPartitionDatabaseTriggerCleanupFixture testPartitionDatabaseTriggerCleanupFixture;
    private final TestPartitionStagingDatabaseDeleteFixture testPartitionStagingDatabaseDeleteFixture;

    public TestDatabaseCleaner(
        RefreshTokenDeleteJdbcRepository refreshTokenDeleteJdbcRepository,
        RefreshTokenFamilyDeleteJdbcRepository refreshTokenFamilyDeleteJdbcRepository,
        AuditLogDeleteJdbcRepository auditLogDeleteJdbcRepository,
        UserDeleteJdbcRepository userDeleteJdbcRepository,
        TestPartitionTargetDatabaseDeleteRepository testPartitionTargetDatabaseDeleteRepository,
        AuditLogPartitionTableProperties auditLogPartitionTableProperties,
        PartitionNamingResolver partitionNamingResolver,
        PartitionTablePropertiesContextFactory partitionTablePropertiesContextFactory,
        TestPartitionRecoveryFailureProperties testPartitionRecoveryFailureProperties,
        TestPartitionDatabaseTriggerCleanupFixture testPartitionDatabaseTriggerCleanupFixture,
        TestPartitionStagingDatabaseDeleteFixture testPartitionStagingDatabaseDeleteFixture
    ) {
        this.refreshTokenDeleteJdbcRepository = refreshTokenDeleteJdbcRepository;
        this.refreshTokenFamilyDeleteJdbcRepository = refreshTokenFamilyDeleteJdbcRepository;
        this.auditLogDeleteJdbcRepository = auditLogDeleteJdbcRepository;
        this.userDeleteJdbcRepository = userDeleteJdbcRepository;
        this.testPartitionTargetDatabaseDeleteRepository = testPartitionTargetDatabaseDeleteRepository;
        this.auditLogPartitionTableProperties = auditLogPartitionTableProperties;
        this.partitionNamingResolver = partitionNamingResolver;
        this.partitionTablePropertiesContextFactory = partitionTablePropertiesContextFactory;
        this.testPartitionRecoveryFailureProperties = testPartitionRecoveryFailureProperties;
        this.testPartitionDatabaseTriggerCleanupFixture = testPartitionDatabaseTriggerCleanupFixture;
        this.testPartitionStagingDatabaseDeleteFixture = testPartitionStagingDatabaseDeleteFixture;
    }

    private void deletePartitionsAndDatabases(
        PartitionTablePropertiesContext partitionTablePropertiesContext
    ){
        Objects.requireNonNull(
            partitionTablePropertiesContext,
            "partitionTablePropertiesContext must not be null"
        );
        testPartitionTargetDatabaseDeleteRepository.deleteAllTargetPartitions(
            partitionTablePropertiesContext
        );
    }

    private void deleteAuditLogPartitionsAndDatabases(){
        PartitionTablePropertiesContext partitionTablePropertiesContext =
            partitionTablePropertiesContextFactory.create(
                auditLogPartitionTableProperties
            );
        testPartitionDatabaseTriggerCleanupFixture.cleanup(
            partitionTablePropertiesContext
        );
        deletePartitionsAndDatabases(partitionTablePropertiesContext);
    }

    private void deleteAllDatabase() {
        testPartitionStagingDatabaseDeleteFixture.deleteAllPartitionDatabaseData();
        
        refreshTokenDeleteJdbcRepository.deleteAll();
        refreshTokenFamilyDeleteJdbcRepository.deleteAll();
        auditLogDeleteJdbcRepository.deleteAll();
        userDeleteJdbcRepository.deleteAll();
        
    }

    public void resetAllDatabase(){
        deleteAuditLogPartitionsAndDatabases();
        deleteAllDatabase();
    }

}