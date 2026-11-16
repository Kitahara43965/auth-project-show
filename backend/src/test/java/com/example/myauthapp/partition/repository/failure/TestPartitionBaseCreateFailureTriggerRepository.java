package com.example.myauthapp.partition.repository.failure;

import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.myauthapp.partition.context.PartitionTablePropertiesContext;


@Repository
public class TestPartitionBaseCreateFailureTriggerRepository{

    public TestPartitionBaseCreateFailureTriggerRepository(
    ){
    }

    public void createDeleteFailureTrigger(
        JdbcTemplate jdbcTemplate,
        String functionName,
        String triggerName,
        String tableName,
        String errorMessage
    ) {
        Objects.requireNonNull(jdbcTemplate, "jdbcTemplate must not be null");
        Objects.requireNonNull(functionName, "functionName must not be null");
        Objects.requireNonNull(triggerName, "triggerName must not be null");
        Objects.requireNonNull(tableName, "tableName must not be null");
        Objects.requireNonNull(errorMessage, "errorMessage must not be null");

        jdbcTemplate.execute(
            """
            CREATE OR REPLACE FUNCTION %s()
            RETURNS trigger
            LANGUAGE plpgsql
            AS $$
            BEGIN
                RAISE EXCEPTION '%s';
            END;
            $$;
            """.formatted(
                functionName,
                errorMessage
            )
        );

        jdbcTemplate.execute(
            """
            CREATE TRIGGER %s
            BEFORE DELETE ON %s
            FOR EACH ROW
            EXECUTE FUNCTION %s();
            """.formatted(
                triggerName,
                tableName,
                functionName
            )
        );
    }

}