#!/bin/bash

set -e

DATABASES=(
    "user_db"
    "audit_db"
    "partition_db"
    "user_db_test"
    "audit_db_test"
    "partition_db_test"
)

for DB in "${DATABASES[@]}"; do
    echo "Resetting $DB..."

    psql -U apple -d "$DB" -c "DROP SCHEMA public CASCADE;"
    psql -U apple -d "$DB" -c "CREATE SCHEMA public;"

    echo "$DB reset complete."
done

echo "All databases reset."