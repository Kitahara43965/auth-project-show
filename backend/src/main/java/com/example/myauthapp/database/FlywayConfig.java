package com.example.myauthapp.database;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class FlywayConfig {

    @Bean(initMethod = "migrate")
    public Flyway userFlyway(
        @Qualifier("userDataSource")
        DataSource dataSource
    ) {
        return Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration/user")
            .load();
    }

    @Bean(initMethod = "migrate")
    public Flyway auditFlyway(
        @Qualifier("auditDataSource")
        DataSource dataSource
    ) {
        return Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration/audit")
            .load();
    }

    @Bean(initMethod = "migrate")
    public Flyway partitionFlyway(
        @Qualifier("partitionDataSource")
        DataSource dataSource
    ) {
        return Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration/partition")
            .load();
    }
}