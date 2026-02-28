#!/usr/bin/env bash
set -euo pipefail

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$BASE_DIR"

DB_FLAVOR="${BYTEDESK_DB:-mysql}"
if [ "${1:-}" = "--db" ]; then
  DB_FLAVOR="${2:-}"
  shift 2
fi

case "$DB_FLAVOR" in
  mysql)
    DB_COMPOSE_FILE="compose-mysql.yaml"
    ;;
  postgresql|postgres)
    DB_COMPOSE_FILE="compose-postgresql.yaml"
    ;;
  oracle)
    DB_COMPOSE_FILE="compose-oracle.yaml"
    ;;
  *)
    echo "Unsupported db flavor: $DB_FLAVOR"
    echo "Use --db mysql|postgresql|oracle (or set BYTEDESK_DB=mysql|postgresql|oracle)"
    exit 1
    ;;
esac

if [ "$#" -eq 0 ]; then
  set -- up -d
fi

docker compose -p bytedesk -f compose.common.yaml -f "$DB_COMPOSE_FILE" -f compose-rabbitmq.yaml "$@"
