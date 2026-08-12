#!/bin/bash

set -e

DATABASES=("my_auth" "my_auth_test")

for DB in "${DATABASES[@]}"; do
    echo "Resetting $DB..."

    psql -U apple -d "$DB" -c "DROP SCHEMA public CASCADE;"
    psql -U apple -d "$DB" -c "CREATE SCHEMA public;"

    echo "$DB reset complete."
done

echo "All databases reset."