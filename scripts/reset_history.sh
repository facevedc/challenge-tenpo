#!/usr/bin/env bash
set -euo pipefail

docker compose exec -T postgres sh -lc \
  "PGPASSWORD=${DB_PASSWORD:-tenpo_secret} psql -U ${DB_USERNAME:-tenpo} -d ${DB_NAME:-tenpo_challenge} -c 'TRUNCATE TABLE tenpo.api_call_history RESTART IDENTITY;' >/dev/null"

echo "History table cleared"
