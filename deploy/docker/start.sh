#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_NAME="${PROJECT_NAME:-bytedesk}"

DB="${1:-mysql}"
MQ="${2:-artemis}"
SCENARIO="${3:-standard}"
TARGET="${4:-all}"

case "${DB}" in
  mysql|postgresql|oracle) ;;
  pg) DB="postgresql" ;;
  *)
    echo "[ERROR] Unsupported db: ${DB}. Allowed: mysql|postgresql|oracle|pg"
    exit 1
    ;;
esac

case "${SCENARIO}" in
  standard|noai|call) ;;
  *)
    echo "[ERROR] Unsupported scenario: ${SCENARIO}. Allowed: standard|noai|call"
    exit 1
    ;;
esac

case "${MQ}" in
  artemis|rabbitmq) ;;
  *)
    echo "[ERROR] Unsupported mq: ${MQ}. Allowed: artemis|rabbitmq"
    exit 1
    ;;
esac

case "${TARGET}" in
  all|middleware) ;;
  *)
    echo "[ERROR] Unsupported target: ${TARGET}. Allowed: all|middleware"
    exit 1
    ;;
esac

BASE_FILE="${SCRIPT_DIR}/compose-base.yaml"
DB_FILE="${SCRIPT_DIR}/compose-db-${DB}.yaml"
MQ_FILE="${SCRIPT_DIR}/compose-mq-${MQ}.yaml"
SCENARIO_FILE="${SCRIPT_DIR}/compose-scenario-${SCENARIO}.yaml"
APP_FILE="${SCRIPT_DIR}/compose-app-bytedesk.yaml"
APP_MQ_FILE="${SCRIPT_DIR}/compose-app-mq-${MQ}.yaml"
CALL_DB_FILE=""

if [[ "${SCENARIO}" == "call" ]]; then
  case "${DB}" in
    mysql|postgresql)
      CALL_DB_FILE="${SCRIPT_DIR}/compose-call-db-${DB}.yaml"
      ;;
    oracle)
      echo "[ERROR] call scenario does not support oracle. Allowed db for call: mysql|postgresql"
      exit 1
      ;;
  esac
fi

for file in "${BASE_FILE}" "${DB_FILE}" "${MQ_FILE}" "${SCENARIO_FILE}"; do
  if [[ ! -f "${file}" ]]; then
    echo "[ERROR] Missing compose file: ${file}"
    exit 1
  fi
done

for file in "${APP_FILE}" "${APP_MQ_FILE}"; do
  if [[ ! -f "${file}" ]]; then
    echo "[ERROR] Missing compose file: ${file}"
    exit 1
  fi
done

if [[ -n "${CALL_DB_FILE}" && ! -f "${CALL_DB_FILE}" ]]; then
  echo "[ERROR] Missing compose file: ${CALL_DB_FILE}"
  exit 1
fi

compose_files=(
  -f "${BASE_FILE}"
  -f "${DB_FILE}"
  -f "${MQ_FILE}"
  -f "${SCENARIO_FILE}"
)

if [[ -n "${CALL_DB_FILE}" ]]; then
  compose_files+=( -f "${CALL_DB_FILE}" )
fi

compose_files+=(
  -f "${APP_FILE}"
  -f "${APP_MQ_FILE}"
)

echo "[INFO] Starting stack: db=${DB}, mq=${MQ}, scenario=${SCENARIO}, target=${TARGET}, project=${PROJECT_NAME}"

if [[ "${TARGET}" == "all" ]]; then
  docker compose \
    --env-file "${SCRIPT_DIR}/.env" \
    -p "${PROJECT_NAME}" \
    "${compose_files[@]}" \
    up -d
else
  middleware_services=()
  while IFS= read -r service; do
    middleware_services+=("${service}")
  done < <(
    docker compose \
      --env-file "${SCRIPT_DIR}/.env" \
      -p "${PROJECT_NAME}" \
      "${compose_files[@]}" \
      config --services | grep -v '^bytedesk$'
  )

  if [[ ${#middleware_services[@]} -eq 0 ]]; then
    echo "[ERROR] No middleware services resolved"
    exit 1
  fi

  docker compose \
    --env-file "${SCRIPT_DIR}/.env" \
    -p "${PROJECT_NAME}" \
    "${compose_files[@]}" \
    up -d "${middleware_services[@]}"
fi

echo "[INFO] Done."
