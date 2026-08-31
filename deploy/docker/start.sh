#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_NAME="${PROJECT_NAME:-bytedesk}"

# ============================================================
# 微语 Docker Compose 组合启动脚本（纯关键字驱动）
#
# 用法：./start.sh [关键字...]
# 关键字任意顺序、任意组合；不传参数等价于 ./start.sh all
#
# 数据库（最多选 1 个，默认 mysql）：
#   mysql | postgresql(别名 pg) | oracle | kingbase(别名 kingbase9)
# 消息队列（最多选 1 个，默认 artemis）：
#   artemis | rabbitmq
# 核心中间件（默认自动包含，显式写也不报错）：
#   redis | elasticsearch(别名 es)
# 组件（任意组合）：
#   freeswitch | mrcp | coturn | janus
#   searxng(别名 search) | neo4j | logstash | kibana | minio
#   prometheus | grafana | zipkin | gotenberg(文件预览转换)
# 组合关键字：
#   call   = freeswitch + mrcp
#   webrtc = coturn + janus
#   obs(别名 observability) = prometheus + grafana + zipkin
# 目标：
#   middleware = redis + elasticsearch + 所选db + 所选mq（不含 bytedesk 应用，源码本地开发）
#   all / bytedesk = middleware 全部 + bytedesk 应用（默认）
#
# 示例：
#   ./start.sh                                # 默认全栈：mysql + artemis + redis + es + bytedesk
#   ./start.sh middleware                     # 仅中间件（源码本地开发）
#   ./start.sh postgresql rabbitmq middleware # PostgreSQL + RabbitMQ 中间件
#   ./start.sh call middleware                # 呼叫中心中间件（freeswitch + mrcp）
#   ./start.sh call webrtc all obs logstash kibana
#   ./start.sh all minio searxng neo4j
#
# 说明：
# - 启用 bytedesk 应用时，脚本会按所选 db/mq 自动注入 SPRING_DATASOURCE_* / MQ
#   环境变量并写入 .env.app（docker compose 通过 --env-file 加载）；
#   .env 或 shell 环境变量中的显式配置优先级更高。
# - mysql/postgresql/oracle/kingbase 场景下会自动确保应用数据库存在（不存在则创建）。
# ============================================================

# compose 文件统一位于 ./compose/ 目录（一镜像一文件），相对路径均以该目录为基准
COMPOSE_DIR="${SCRIPT_DIR}/compose"

DB=""
MQ=""
ENABLE_FREESWITCH=false
ENABLE_MRCP=false
ENABLE_COTURN=false
ENABLE_JANUS=false
ENABLE_SEARXNG=false
ENABLE_NEO4J=false
ENABLE_LOGSTASH=false
ENABLE_KIBANA=false
ENABLE_MINIO=false
ENABLE_PROMETHEUS=false
ENABLE_GRAFANA=false
ENABLE_ZIPKIN=false
ENABLE_GOTENBERG=false
TARGET=""

usage() {
  sed -n '10,44p' "${BASH_SOURCE[0]}" | sed 's/^# \{0,1\}//'
  exit 1
}

set_db() {
  if [[ -n "${DB}" ]]; then
    echo "[ERROR] Duplicate db keyword: '${DB}' and '$1'. Pick exactly one."
    exit 1
  fi
  DB="$1"
}

set_mq() {
  if [[ -n "${MQ}" ]]; then
    echo "[ERROR] Duplicate mq keyword: '${MQ}' and '$1'. Pick exactly one."
    exit 1
  fi
  MQ="$1"
}

set_target() {
  if [[ -n "${TARGET}" ]]; then
    echo "[ERROR] Conflicting target keywords: '${TARGET}' and '$1'."
    exit 1
  fi
  TARGET="$1"
}

for arg in "$@"; do
  case "${arg}" in
    mysql) set_db mysql ;;
    postgresql|pg) set_db postgresql ;;
    oracle) set_db oracle ;;
    kingbase|kingbase9) set_db kingbase ;;
    artemis) set_mq artemis ;;
    rabbitmq) set_mq rabbitmq ;;
    freeswitch) ENABLE_FREESWITCH=true ;;
    mrcp) ENABLE_MRCP=true ;;
    coturn) ENABLE_COTURN=true ;;
    janus) ENABLE_JANUS=true ;;
    searxng|search) ENABLE_SEARXNG=true ;;
    neo4j) ENABLE_NEO4J=true ;;
    logstash) ENABLE_LOGSTASH=true ;;
    kibana) ENABLE_KIBANA=true ;;
    minio) ENABLE_MINIO=true ;;
    prometheus) ENABLE_PROMETHEUS=true ;;
    grafana) ENABLE_GRAFANA=true ;;
    zipkin) ENABLE_ZIPKIN=true ;;
    gotenberg) ENABLE_GOTENBERG=true ;;
    call)
      ENABLE_FREESWITCH=true
      ENABLE_MRCP=true
      ;;
    webrtc)
      ENABLE_COTURN=true
      ENABLE_JANUS=true
      ;;
    obs|observability)
      ENABLE_PROMETHEUS=true
      ENABLE_GRAFANA=true
      ENABLE_ZIPKIN=true
      ;;
    middleware) set_target middleware ;;
    all|bytedesk|app) set_target all ;;
    -h|--help|help) usage ;;
    *)
      echo "[ERROR] Unknown keyword: '${arg}'"
      echo "Allowed: mysql|postgresql|pg|oracle|kingbase|kingbase9 artemis|rabbitmq redis|elasticsearch|es"
      echo "        freeswitch mrcp coturn janus searxng|search neo4j logstash kibana minio prometheus grafana zipkin gotenberg"
      echo "        call webrtc obs middleware all"
      exit 1
      ;;
  esac
done

# 默认值与归一化
DB="${DB:-mysql}"
MQ="${MQ:-artemis}"
TARGET="${TARGET:-all}"

# redis + elasticsearch 为核心中间件，任何栈都自动包含
ENABLE_REDIS=true
ENABLE_ELASTICSEARCH=true

# 组合约束校验
if [[ "${ENABLE_FREESWITCH}" == true || "${ENABLE_MRCP}" == true ]]; then
  case "${DB}" in
    mysql|postgresql) ;;
    oracle|kingbase)
      echo "[ERROR] freeswitch/mrcp (call) does not support ${DB}. Allowed db: mysql|postgresql"
      exit 1
      ;;
  esac
fi

# ============================================================
# 组装 compose 文件列表（中间件部分，不含应用）
# ============================================================
middleware_files=()

add_file() {
  local file="${COMPOSE_DIR}/$1"
  if [[ ! -f "${file}" ]]; then
    echo "[ERROR] Missing compose file: ${file}"
    exit 1
  fi
  middleware_files+=( -f "${file}" )
}

add_file compose-redis.yaml
add_file compose-elasticsearch.yaml
add_file "compose-${DB}.yaml"
add_file "compose-${MQ}.yaml"
[[ "${ENABLE_FREESWITCH}" == true ]] && add_file compose-freeswitch.yaml
[[ "${ENABLE_MRCP}" == true ]] && add_file compose-mrcp.yaml
[[ "${ENABLE_COTURN}" == true ]] && add_file compose-coturn.yaml
[[ "${ENABLE_JANUS}" == true ]] && add_file compose-janus.yaml
[[ "${ENABLE_SEARXNG}" == true ]] && add_file compose-searxng.yaml
[[ "${ENABLE_NEO4J}" == true ]] && add_file compose-neo4j.yaml
[[ "${ENABLE_LOGSTASH}" == true ]] && add_file compose-logstash.yaml
[[ "${ENABLE_KIBANA}" == true ]] && add_file compose-kibana.yaml
[[ "${ENABLE_MINIO}" == true ]] && add_file compose-minio.yaml
[[ "${ENABLE_PROMETHEUS}" == true ]] && add_file compose-prometheus.yaml
[[ "${ENABLE_GRAFANA}" == true ]] && add_file compose-grafana.yaml
[[ "${ENABLE_ZIPKIN}" == true ]] && add_file compose-zipkin.yaml
[[ "${ENABLE_GOTENBERG}" == true ]] && add_file compose-gotenberg.yaml

APP_FILE="${COMPOSE_DIR}/compose-bytedesk.yaml"
if [[ "${TARGET}" == "all" && ! -f "${APP_FILE}" ]]; then
  echo "[ERROR] Missing compose file: ${APP_FILE}"
  exit 1
fi

ENV_FILE_ARGS=()
if [[ -f "${SCRIPT_DIR}/.env" ]]; then
  ENV_FILE_ARGS+=( --env-file "${SCRIPT_DIR}/.env" )
fi
APP_ENV_FILE="${SCRIPT_DIR}/.env.app"
if [[ -f "${APP_ENV_FILE}" ]]; then
  ENV_FILE_ARGS+=( --env-file "${APP_ENV_FILE}" )
fi

# ============================================================
# .env 读取辅助
# ============================================================
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

# key 是否在 .env 中显式定义（存在 key= 行即算，无论值是否为空）
env_key_defined() {
  local key="$1"
  [[ -f "${SCRIPT_DIR}/.env" ]] || return 1
  grep -Eq "^[[:space:]]*${key}=" "${SCRIPT_DIR}/.env"
}

# ============================================================
# 应用环境注入：优先级 shell 环境变量 > .env 显式配置 > 脚本注入
# 注入结果写入 .env.app，供 docker compose 与 watchdog 后续使用
# ============================================================
app_env_lines=()

inject_env() {
  local key="$1"
  local value="$2"

  if [[ -n "${!key:-}" ]]; then
    # 已在 shell 环境中显式设置：记录到 .env.app，保证 watchdog/后续 compose 可复现
    app_env_lines+=( "${key}=${!key}" )
    return
  fi

  if env_key_defined "${key}"; then
    # .env 中显式配置：docker compose 通过 --env-file 直接读取，无需注入
    return
  fi

  export "${key}=${value}"
  app_env_lines+=( "${key}=${value}" )
}

inject_app_env() {
  local mysql_port mysql_database mysql_root_user
  local pg_port pg_db pg_user
  local kb_port kb_database kb_user
  mysql_port="$(get_env_value MYSQL_PORT 3306)"
  mysql_database="$(get_env_value MYSQL_DATABASE bytedesk)"
  mysql_root_user="$(get_env_value MYSQL_ROOT_USER root)"
  pg_port="$(get_env_value POSTGRES_PORT 5432)"
  pg_db="$(get_env_value POSTGRES_DB bytedesk)"
  pg_user="$(get_env_value POSTGRES_USER postgres)"
  kb_port="$(get_env_value KINGBASE_PORT 54321)"
  kb_database="$(get_env_value KINGBASE_DATABASE bytedesk)"
  kb_user="$(get_env_value KINGBASE_DB_USER root)"

  case "${DB}" in
    mysql)
      inject_env SPRING_DATASOURCE_URL "jdbc:mysql://bytedesk-mysql:${mysql_port}/${mysql_database}?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true"
      inject_env SPRING_DATASOURCE_USERNAME "${mysql_root_user}"
      inject_env SPRING_DATASOURCE_PASSWORD "$(get_env_value MYSQL_ROOT_PASSWORD '')"
      ;;
    postgresql)
      inject_env SPRING_DATASOURCE_URL "jdbc:postgresql://bytedesk-postgresql:${pg_port}/${pg_db}"
      inject_env SPRING_DATASOURCE_USERNAME "${pg_user}"
      inject_env SPRING_DATASOURCE_PASSWORD "$(get_env_value POSTGRES_PASSWORD '')"
      inject_env SPRING_DATASOURCE_DRIVER_CLASS_NAME "org.postgresql.Driver"
      ;;
    oracle)
      inject_env SPRING_DATASOURCE_URL "jdbc:oracle:thin:@bytedesk-oracle:1521/FREEPDB1"
      inject_env SPRING_DATASOURCE_USERNAME "$(get_env_value ORACLE_APP_USER bytedesk)"
      inject_env SPRING_DATASOURCE_PASSWORD "$(get_env_value ORACLE_APP_USER_PASSWORD '')"
      inject_env SPRING_DATASOURCE_DRIVER_CLASS_NAME "oracle.jdbc.OracleDriver"
      ;;
    kingbase)
      inject_env SPRING_DATASOURCE_URL "jdbc:kingbase8://bytedesk-kingbase:${kb_port}/${kb_database}?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true&options=-c%20default_tablespace=sys_default"
      inject_env SPRING_DATASOURCE_USERNAME "${kb_user}"
      inject_env SPRING_DATASOURCE_PASSWORD "$(get_env_value KINGBASE_DB_PASSWORD '')"
      inject_env SPRING_DATASOURCE_DRIVER_CLASS_NAME "com.kingbase8.Driver"
      inject_env SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT "org.hibernate.dialect.PostgreSQLDialect"
      inject_env SPRING_QUARTZ_JDBC_PLATFORM "postgres"
      inject_env SPRING_BATCH_JDBC_PLATFORM "postgres"
      inject_env SPRING_BATCH_DATABASE_TYPE "POSTGRES"
      inject_env FLOWABLE_DATABASE_TYPE "postgres"
      inject_env SPRING_LIQUIBASE_ENABLED "false"
      ;;
  esac

  case "${MQ}" in
    artemis)
      inject_env BYTEDESK_MQ_TYPE "artemis"
      inject_env BYTEDESK_MQ_RABBITMQ_ENABLED "false"
      inject_env SPRING_ARTEMIS_MODE "embedded"
      inject_env SPRING_ARTEMIS_BROKER_URL "tcp://bytedesk-artemis:61616"
      inject_env SPRING_ARTEMIS_USER "$(get_env_value ARTEMIS_USER admin)"
      inject_env SPRING_ARTEMIS_PASSWORD "$(get_env_value ARTEMIS_PASSWORD '')"
      inject_env SPRING_JMS_CACHE_ENABLED "true"
      ;;
    rabbitmq)
      inject_env BYTEDESK_MQ_TYPE "rabbitmq"
      inject_env BYTEDESK_MQ_RABBITMQ_ENABLED "true"
      inject_env SPRING_RABBITMQ_HOST "bytedesk-rabbitmq"
      inject_env SPRING_RABBITMQ_PORT "5672"
      inject_env SPRING_RABBITMQ_USERNAME "$(get_env_value RABBITMQ_DEFAULT_USER admin)"
      inject_env SPRING_RABBITMQ_PASSWORD "$(get_env_value RABBITMQ_DEFAULT_PASS '')"
      inject_env SPRING_RABBITMQ_VIRTUAL_HOST "/"
      inject_env SPRING_RABBITMQ_CONNECTION_TIMEOUT "5000"
      inject_env SPRING_ARTEMIS_MODE "embedded"
      ;;
  esac
}

write_app_env_file() {
  if [[ ${#app_env_lines[@]} -eq 0 ]]; then
    return
  fi
  {
    echo "# Auto-generated by start.sh at $(date '+%Y-%m-%d %H:%M:%S') — do not edit."
    echo "# bytedesk 应用数据源/MQ 连接注入（db=${DB}, mq=${MQ}）；显式配置请使用 .env 或环境变量"
    printf '%s\n' "${app_env_lines[@]}"
  } > "${APP_ENV_FILE}"
}

# ============================================================
# 数据库自动建库（exec 目标服务：bytedesk-<db>）
# ============================================================
ensure_kingbase_database() {
  local db_user db_password db_name
  db_user="$(get_env_value "KINGBASE_DB_USER" "root")"
  db_password="$(get_env_value "KINGBASE_DB_PASSWORD" "")"
  db_name="$(get_env_value "KINGBASE_DATABASE" "bytedesk")"

  if [[ -z "${db_password}" ]]; then
    echo "[WARN] KINGBASE_DB_PASSWORD is empty, skip auto database ensure for kingbase"
    return
  fi

  local exists_check="SELECT COUNT(1) FROM sys_database WHERE datname='${db_name}';"
  local create_sql="CREATE DATABASE ${db_name} WITH ENCODING 'UTF8' TEMPLATE template1 OWNER ${db_user};"

  local tries=30
  local count_output=""
  for ((i=1; i<=tries; i++)); do
    count_output="$(docker compose \
      "${ENV_FILE_ARGS[@]}" \
      -p "${PROJECT_NAME}" \
      "${middleware_files[@]}" \
      exec -T bytedesk-kingbase sh -lc "printf '%s\\n' '${db_password}' | /home/kingbase/install/kingbase/bin/ksql -h 127.0.0.1 -p ${KINGBASE_PORT:-54321} -U ${db_user} -d kingbase -W -t -A -c \"${exists_check}\"" 2>/dev/null || true)"

    count_output="$(echo "${count_output}" | tr -d '[:space:]')"
    if [[ "${count_output}" == "1" ]]; then
      return
    fi

    if [[ "${count_output}" == "0" ]]; then
      if docker compose \
        "${ENV_FILE_ARGS[@]}" \
        -p "${PROJECT_NAME}" \
        "${middleware_files[@]}" \
        exec -T bytedesk-kingbase sh -lc "printf '%s\\n' '${db_password}' | /home/kingbase/install/kingbase/bin/ksql -h 127.0.0.1 -p ${KINGBASE_PORT:-54321} -U ${db_user} -d kingbase -W -c \"${create_sql}\"" >/dev/null 2>&1; then
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
  local create_sql="CREATE DATABASE IF NOT EXISTS ${db_name} CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

  local tries=30
  local query_output=""
  for ((i=1; i<=tries; i++)); do
    query_output="$(docker compose \
      "${ENV_FILE_ARGS[@]}" \
      -p "${PROJECT_NAME}" \
      "${middleware_files[@]}" \
      exec -T bytedesk-mysql sh -lc "mysql -h 127.0.0.1 -P ${MYSQL_PORT:-3306} -u${db_user} -p'${db_password}' -Nse \"${check_sql}\"" 2>/dev/null || true)"

    query_output="$(echo "${query_output}" | tr -d '[:space:]')"
    if [[ "${query_output}" == "${db_name}" ]]; then
      return
    fi

    if docker compose \
      "${ENV_FILE_ARGS[@]}" \
      -p "${PROJECT_NAME}" \
      "${middleware_files[@]}" \
      exec -T bytedesk-mysql sh -lc "mysql -h 127.0.0.1 -P ${MYSQL_PORT:-3306} -u${db_user} -p'${db_password}' -Nse \"${create_sql}\"" >/dev/null 2>&1; then
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
      "${ENV_FILE_ARGS[@]}" \
      -p "${PROJECT_NAME}" \
      "${middleware_files[@]}" \
      exec -T bytedesk-postgresql sh -lc "PGPASSWORD='${db_password}' psql -h 127.0.0.1 -p ${POSTGRES_PORT:-5432} -U ${db_user} -d postgres -t -A -c \"${check_sql}\"" 2>/dev/null || true)"

    query_output="$(echo "${query_output}" | tr -d '[:space:]')"
    if [[ "${query_output}" == "1" ]]; then
      return
    fi

    if docker compose \
      "${ENV_FILE_ARGS[@]}" \
      -p "${PROJECT_NAME}" \
      "${middleware_files[@]}" \
      exec -T bytedesk-postgresql sh -lc "PGPASSWORD='${db_password}' psql -h 127.0.0.1 -p ${POSTGRES_PORT:-5432} -U ${db_user} -d postgres -v ON_ERROR_STOP=1 -c \"${create_sql}\"" >/dev/null 2>&1; then
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
      "${ENV_FILE_ARGS[@]}" \
      -p "${PROJECT_NAME}" \
      "${middleware_files[@]}" \
      exec -T bytedesk-oracle sh -lc "printf \"SET HEADING OFF FEEDBACK OFF VERIFY OFF ECHO OFF PAGES 0\\nSELECT COUNT(1) FROM v\\$pdbs WHERE name='${db_name_upper}';\\nEXIT;\\n\" | sqlplus -s sys/'${sys_password}'@//127.0.0.1:1521/FREE as sysdba" 2>/dev/null || true)"

    query_output="$(echo "${query_output}" | tr -d '[:space:]')"
    if [[ "${query_output}" == "1" ]]; then
      return
    fi

    if docker compose \
      "${ENV_FILE_ARGS[@]}" \
      -p "${PROJECT_NAME}" \
      "${middleware_files[@]}" \
      exec -T bytedesk-oracle sh -lc "printf \"WHENEVER SQLERROR EXIT 1\\nCREATE PLUGGABLE DATABASE ${db_name_upper} ADMIN USER ${app_user_upper} IDENTIFIED BY \"${app_user_password}\" FILE_NAME_CONVERT=('FREEPDB1','${db_name_upper}');\\nALTER PLUGGABLE DATABASE ${db_name_upper} OPEN;\\nEXIT;\\n\" | sqlplus -s sys/'${sys_password}'@//127.0.0.1:1521/FREE as sysdba" >/dev/null 2>&1; then
      echo "[INFO] Oracle PDB '${db_name_upper}' created"
      return
    fi

    sleep 2
  done

  echo "[WARN] Failed to auto-ensure Oracle PDB '${db_name_upper}' after ${tries} retries"
}

# ============================================================
# 启动
# ============================================================
components_summary=""
[[ "${ENABLE_FREESWITCH}" == true ]] && components_summary="${components_summary} freeswitch"
[[ "${ENABLE_MRCP}" == true ]] && components_summary="${components_summary} mrcp"
[[ "${ENABLE_COTURN}" == true ]] && components_summary="${components_summary} coturn"
[[ "${ENABLE_JANUS}" == true ]] && components_summary="${components_summary} janus"
[[ "${ENABLE_SEARXNG}" == true ]] && components_summary="${components_summary} searxng"
[[ "${ENABLE_LOGSTASH}" == true ]] && components_summary="${components_summary} logstash"
[[ "${ENABLE_KIBANA}" == true ]] && components_summary="${components_summary} kibana"
[[ "${ENABLE_MINIO}" == true ]] && components_summary="${components_summary} minio"
[[ "${ENABLE_PROMETHEUS}" == true ]] && components_summary="${components_summary} prometheus"
[[ "${ENABLE_GRAFANA}" == true ]] && components_summary="${components_summary} grafana"
[[ "${ENABLE_ZIPKIN}" == true ]] && components_summary="${components_summary} zipkin"
[[ "${ENABLE_GOTENBERG}" == true ]] && components_summary="${components_summary} gotenberg"

echo "[INFO] Starting stack: db=${DB}, mq=${MQ}, target=${TARGET}, project=${PROJECT_NAME},${components_summary:- no extra components}"

# 1) 先启动中间件（redis/es/db/mq + 所选组件），保证应用启动顺序
docker compose \
  "${ENV_FILE_ARGS[@]}" \
  -p "${PROJECT_NAME}" \
  "${middleware_files[@]}" \
  up -d

# 2) 再启动 bytedesk 应用（注入数据源/MQ 环境并写入 .env.app）
if [[ "${TARGET}" == "all" ]]; then
  inject_app_env
  write_app_env_file
  docker compose \
    "${ENV_FILE_ARGS[@]}" \
    -p "${PROJECT_NAME}" \
    -f "${APP_FILE}" \
    up -d
fi

# 3) 自动建库
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
  kingbase)
    ensure_kingbase_database
    ;;
esac

echo "[INFO] Done."
