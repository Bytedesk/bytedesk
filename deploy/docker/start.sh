#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_NAME="${PROJECT_NAME:-bytedesk}"

DB="${1:-mysql}"
MQ="${2:-artemis}"
SCENARIO="${3:-standard}"
TARGET="${4:-all}"

case "${DB}" in
  mysql|postgresql|oracle|kingbase9) ;;
  pg) DB="postgresql" ;;
  kingbase) DB="kingbase9" ;;
  *)
    echo "[ERROR] Unsupported db: ${DB}. Allowed: mysql|postgresql|oracle|kingbase9|kingbase|pg"
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
    oracle|kingbase9)
      echo "[ERROR] call scenario does not support ${DB}. Allowed db for call: mysql|postgresql"
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

get_env_value() {
  local key="$1"
  local default_value="$2"
  local env_file="${SCRIPT_DIR}/.env"

  if [[ ! -f "${env_file}" ]]; then
    echo "${default_value}"
    return
  fi

  local line
  line="$(grep -E "^[[:space:]]*${key}=" "${env_file}" | tail -n 1 || true)"
  if [[ -z "${line}" ]]; then
    echo "${default_value}"
    return
  fi

  local value
  value="${line#*=}"
  value="${value%$'\r'}"
  value="${value%\"}"
  value="${value#\"}"
  value="${value%\'}"
  value="${value#\'}"

  if [[ -z "${value}" ]]; then
    echo "${default_value}"
  else
    echo "${value}"
  fi
}

ensure_kingbase_database() {
  local db_user db_password db_name
  db_user="$(get_env_value "KINGBASE_DB_USER" "root")"
  db_password="$(get_env_value "KINGBASE_DB_PASSWORD" "")"
  db_name="$(get_env_value "KINGBASE_DATABASE" "bytedesk")"

  if [[ -z "${db_password}" ]]; then
    echo "[WARN] KINGBASE_DB_PASSWORD is empty, skip auto database ensure for kingbase9"
    return
  fi

  local exists_check="SELECT COUNT(1) FROM sys_database WHERE datname='${db_name}';"
  local create_sql="CREATE DATABASE ${db_name} WITH ENCODING 'UTF8' TEMPLATE template1 OWNER ${db_user};"

  local tries=30
  local count_output=""
  for ((i=1; i<=tries; i++)); do
    count_output="$(docker compose \
      --env-file "${SCRIPT_DIR}/.env" \
      -p "${PROJECT_NAME}" \
      "${compose_files[@]}" \
      exec -T bytedesk-db sh -lc "printf '%s\\n' '${db_password}' | /home/kingbase/install/kingbase/bin/ksql -h 127.0.0.1 -p ${KINGBASE_PORT:-54321} -U ${db_user} -d kingbase -W -t -A -c \"${exists_check}\"" 2>/dev/null || true)"

    count_output="$(echo "${count_output}" | tr -d '[:space:]')"
    if [[ "${count_output}" == "1" ]]; then
      return
    fi

    if [[ "${count_output}" == "0" ]]; then
      if docker compose \
        --env-file "${SCRIPT_DIR}/.env" \
        -p "${PROJECT_NAME}" \
        "${compose_files[@]}" \
        exec -T bytedesk-db sh -lc "printf '%s\\n' '${db_password}' | /home/kingbase/install/kingbase/bin/ksql -h 127.0.0.1 -p ${KINGBASE_PORT:-54321} -U ${db_user} -d kingbase -W -c \"${create_sql}\"" >/dev/null 2>&1; then
        echo "[INFO] Kingbase database '${db_name}' created"
        return
      fi
    fi

    sleep 2
  done

  echo "[WARN] Failed to auto-ensure Kingbase database '${db_name}' after ${tries} retries"
}

ensure_mysql_database() {
  local db_user db_password db_name
  db_user="$(get_env_value "MYSQL_ROOT_USER" "root")"
  db_password="$(get_env_value "MYSQL_ROOT_PASSWORD" "")"
  db_name="$(get_env_value "MYSQL_DATABASE" "bytedesk")"

  if [[ -z "${db_password}" ]]; then
    echo "[WARN] MYSQL_ROOT_PASSWORD is empty, skip auto database ensure for mysql"
    return
  fi

  local check_sql="SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME='${db_name}' LIMIT 1;"
  local create_sql="CREATE DATABASE \`${db_name}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

  local tries=30
  local query_output=""
  for ((i=1; i<=tries; i++)); do
    query_output="$(docker compose \
      --env-file "${SCRIPT_DIR}/.env" \
      -p "${PROJECT_NAME}" \
      "${compose_files[@]}" \
      exec -T bytedesk-db sh -lc "mysql -h 127.0.0.1 -P ${MYSQL_PORT:-3306} -u${db_user} -p'${db_password}' -Nse \"${check_sql}\"" 2>/dev/null || true)"

    query_output="$(echo "${query_output}" | tr -d '[:space:]')"
    if [[ "${query_output}" == "${db_name}" ]]; then
      return
    fi

    if docker compose \
      --env-file "${SCRIPT_DIR}/.env" \
      -p "${PROJECT_NAME}" \
      "${compose_files[@]}" \
      exec -T bytedesk-db sh -lc "mysql -h 127.0.0.1 -P ${MYSQL_PORT:-3306} -u${db_user} -p'${db_password}' -Nse \"${create_sql}\"" >/dev/null 2>&1; then
      echo "[INFO] MySQL database '${db_name}' created"
      return
    fi

    sleep 2
  done

  echo "[WARN] Failed to auto-ensure MySQL database '${db_name}' after ${tries} retries"
}

ensure_postgresql_database() {
  local db_user db_password db_name
  db_user="$(get_env_value "POSTGRES_USER" "postgres")"
  db_password="$(get_env_value "POSTGRES_PASSWORD" "")"
  db_name="$(get_env_value "POSTGRES_DB" "bytedesk")"

  if [[ -z "${db_password}" ]]; then
    echo "[WARN] POSTGRES_PASSWORD is empty, skip auto database ensure for postgresql"
    return
  fi

  local check_sql="SELECT 1 FROM pg_database WHERE datname='${db_name}';"
  local create_sql="CREATE DATABASE \"${db_name}\";"

  local tries=30
  local query_output=""
  for ((i=1; i<=tries; i++)); do
    query_output="$(docker compose \
      --env-file "${SCRIPT_DIR}/.env" \
      -p "${PROJECT_NAME}" \
      "${compose_files[@]}" \
      exec -T bytedesk-db sh -lc "PGPASSWORD='${db_password}' psql -h 127.0.0.1 -p ${POSTGRES_PORT:-5432} -U ${db_user} -d postgres -t -A -c \"${check_sql}\"" 2>/dev/null || true)"

    query_output="$(echo "${query_output}" | tr -d '[:space:]')"
    if [[ "${query_output}" == "1" ]]; then
      return
    fi

    if docker compose \
      --env-file "${SCRIPT_DIR}/.env" \
      -p "${PROJECT_NAME}" \
      "${compose_files[@]}" \
      exec -T bytedesk-db sh -lc "PGPASSWORD='${db_password}' psql -h 127.0.0.1 -p ${POSTGRES_PORT:-5432} -U ${db_user} -d postgres -v ON_ERROR_STOP=1 -c \"${create_sql}\"" >/dev/null 2>&1; then
      echo "[INFO] PostgreSQL database '${db_name}' created"
      return
    fi

    sleep 2
  done

  echo "[WARN] Failed to auto-ensure PostgreSQL database '${db_name}' after ${tries} retries"
}

ensure_oracle_database() {
  local sys_password db_name app_user app_user_password
  sys_password="$(get_env_value "ORACLE_PASSWORD" "")"
  db_name="$(get_env_value "ORACLE_DATABASE" "bytedesk")"
  app_user="$(get_env_value "ORACLE_APP_USER" "bytedesk")"
  app_user_password="$(get_env_value "ORACLE_APP_USER_PASSWORD" "")"

  if [[ -z "${sys_password}" ]]; then
    echo "[WARN] ORACLE_PASSWORD is empty, skip auto database ensure for oracle"
    return
  fi

  if [[ -z "${app_user_password}" ]]; then
    echo "[WARN] ORACLE_APP_USER_PASSWORD is empty, skip auto database ensure for oracle"
    return
  fi

  local db_name_upper
  db_name_upper="$(echo "${db_name}" | tr '[:lower:]' '[:upper:]')"
  local app_user_upper
  app_user_upper="$(echo "${app_user}" | tr '[:lower:]' '[:upper:]')"

  local tries=30
  local query_output=""
  for ((i=1; i<=tries; i++)); do
    query_output="$(docker compose \
      --env-file "${SCRIPT_DIR}/.env" \
      -p "${PROJECT_NAME}" \
      "${compose_files[@]}" \
      exec -T bytedesk-db sh -lc "printf \"SET HEADING OFF FEEDBACK OFF VERIFY OFF ECHO OFF PAGES 0\\nSELECT COUNT(1) FROM v\\$pdbs WHERE name='${db_name_upper}';\\nEXIT;\\n\" | sqlplus -s sys/'${sys_password}'@//127.0.0.1:1521/FREE as sysdba" 2>/dev/null || true)"

    query_output="$(echo "${query_output}" | tr -d '[:space:]')"
    if [[ "${query_output}" == "1" ]]; then
      return
    fi

    if docker compose \
      --env-file "${SCRIPT_DIR}/.env" \
      -p "${PROJECT_NAME}" \
      "${compose_files[@]}" \
      exec -T bytedesk-db sh -lc "printf \"WHENEVER SQLERROR EXIT 1\\nCREATE PLUGGABLE DATABASE ${db_name_upper} ADMIN USER ${app_user_upper} IDENTIFIED BY \"${app_user_password}\" FILE_NAME_CONVERT=('FREEPDB1','${db_name_upper}');\\nALTER PLUGGABLE DATABASE ${db_name_upper} OPEN;\\nEXIT;\\n\" | sqlplus -s sys/'${sys_password}'@//127.0.0.1:1521/FREE as sysdba" >/dev/null 2>&1; then
      echo "[INFO] Oracle PDB '${db_name_upper}' created"
      return
    fi

    sleep 2
  done

  echo "[WARN] Failed to auto-ensure Oracle PDB '${db_name_upper}' after ${tries} retries"
}

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

case "${DB}" in
  mysql)
    ensure_mysql_database
    ;;
  postgresql)
    ensure_postgresql_database
    ;;
  oracle)
    ensure_oracle_database
    ;;
  kingbase9)
    ensure_kingbase_database
    ;;
esac

echo "[INFO] Done."
