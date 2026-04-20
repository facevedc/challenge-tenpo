#!/usr/bin/env bash
set -euo pipefail

docker compose exec -T redis redis-cli GET tenpo:calculator:percentage
