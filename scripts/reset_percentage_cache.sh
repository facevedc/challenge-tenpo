#!/usr/bin/env bash
set -euo pipefail

docker compose exec -T redis redis-cli DEL tenpo:calculator:percentage >/dev/null
echo "Percentage cache cleared"
