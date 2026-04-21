#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

"${SCRIPT_DIR}/reset_mock_scenarios.sh"
"${SCRIPT_DIR}/reset_percentage_cache.sh"
"${SCRIPT_DIR}/reset_history.sh"

echo "Challenge state reset"
