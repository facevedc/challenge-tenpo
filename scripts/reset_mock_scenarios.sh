#!/usr/bin/env bash

set -euo pipefail

curl -sS -X POST http://localhost:8081/__admin/scenarios/reset >/dev/null
echo "Mock scenarios reset"
