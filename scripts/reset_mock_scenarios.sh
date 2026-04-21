#!/usr/bin/env bash

set -euo pipefail

docker compose exec -T api-mocks sh -lc \
  "wget -qO- --method=POST http://127.0.0.1:8080/__admin/scenarios/reset >/dev/null"

echo "Mock scenarios reset"
