package com.example.myauthapp.audit.database;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class AuditDatabaseConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.audit")
    public DataSourceProperties auditDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource auditDataSource(
        @Qualifier("auditDataSourceProperties")
        DataSourceProperties properties
    ) {
        return properties
            .initializeDataSourceBuilder()
            .build();
    }

    @Bean
    public JdbcTemplate auditJdbcTemplate(
        @Qualifier("auditDataSource")
        DataSource dataSource
    ) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager auditTransactionManager(
        @Qualifier("auditDataSource")
        DataSource dataSource
    ) {
        return new DataSourceTransactionManager(dataSource);
    }
}