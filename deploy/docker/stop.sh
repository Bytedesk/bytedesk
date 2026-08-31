#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_NAME="${PROJECT_NAME:-bytedesk}"

# ============================================================
# 微语 Docker Compose 组合停止脚本（纯关键字驱动）
#
# 用法：./stop.sh [stop|down] [关键字...]
# 动作 stop/down 可在任意位置（stop = 停止容器；down = 删除容器保留数据卷；默认 stop）
# 其余关键字与 start.sh 完全一致，需与启动时传入的关键字相同。
#
# 示例：
#   ./stop.sh                      # 停止默认栈（等价 ./stop.sh stop all）
#   ./stop.sh down                 # 删除默认栈容器（保留数据卷）
#   ./stop.sh middleware down      # 仅删除中间件容器
#   ./stop.sh call webrtc down     # 删除呼叫中心 + WebRTC 栈
#   ./stop.sh all obs logstash kibana down
# ============================================================

# compose 文件统一位于 ./compose/ 目录（一镜像一文件），与 start.sh 保持一致
COMPOSE_DIR="${SCRIPT_DIR}/compose"

MODE=""

set_mode() {
  if [[ -n "${MODE}" ]]; then
    echo "[ERROR] Conflicting actions: '${MODE}' and '$1'."
    exit 1
  fi
  MODE="$1"
}

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
    stop|down) set_mode "${arg}" ;;
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
    -h|--help|help)
      sed -n '10,22p' "${BASH_SOURCE[0]}" | sed 's/^# \{0,1\}//'
      exit 0
      ;;
    *)
      echo "[ERROR] Unknown keyword: '${arg}'"
      echo "Allowed: stop|down mysql|postgresql|pg|oracle|kingbase|kingbase9 artemis|rabbitmq"
      echo "        freeswitch mrcp coturn janus searxng|search neo4j logstash kibana minio prometheus grafana zipkin gotenberg"
      echo "        call webrtc obs middleware all"
      exit 1
      ;;
  esac
done

# 默认值与归一化（与 start.sh 一致）
MODE="${MODE:-stop}"
DB="${DB:-mysql}"
MQ="${MQ:-artemis}"
TARGET="${TARGET:-all}"

# ============================================================
# 组装 compose 文件列表（与 start.sh 相同规则）
# ============================================================
compose_files=()

add_file() {
  local file="${COMPOSE_DIR}/$1"
  if [[ ! -f "${file}" ]]; then
    echo "[ERROR] Missing compose file: ${file}"
    exit 1
  fi
  compose_files+=( -f "${file}" )
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

# TARGET=all（默认）时包含应用文件，保证 down 能一并删除应用容器
APP_FILE="${COMPOSE_DIR}/compose-bytedesk.yaml"
if [[ "${TARGET}" == "all" ]]; then
  if [[ ! -f "${APP_FILE}" ]]; then
    echo "[ERROR] Missing compose file: ${APP_FILE}"
    exit 1
  fi
  compose_files+=( -f "${APP_FILE}" )
fi

ENV_FILE_ARGS=()
if [[ -f "${SCRIPT_DIR}/.env" ]]; then
  ENV_FILE_ARGS+=( --env-file "${SCRIPT_DIR}/.env" )
fi
if [[ -f "${SCRIPT_DIR}/.env.app" ]]; then
  ENV_FILE_ARGS+=( --env-file "${SCRIPT_DIR}/.env.app" )
fi

components_summary=""
[[ "${ENABLE_FREESWITCH}" == true ]] && components_summary="${components_summary} freeswitch"
[[ "${ENABLE_MRCP}" == true ]] && components_summary="${components_summary} mrcp"
[[ "${ENABLE_COTURN}" == true ]] && components_summary="${components_summary} coturn"
[[ "${ENABLE_JANUS}" == true ]] && components_summary="${components_summary} janus"
[[ "${ENABLE_SEARXNG}" == true ]] && components_summary="${components_summary} searxng"
[[ "${ENABLE_NEO4J}" == true ]] && components_summary="${components_summary} neo4j"
[[ "${ENABLE_LOGSTASH}" == true ]] && components_summary="${components_summary} logstash"
[[ "${ENABLE_KIBANA}" == true ]] && components_summary="${components_summary} kibana"
[[ "${ENABLE_MINIO}" == true ]] && components_summary="${components_summary} minio"
[[ "${ENABLE_PROMETHEUS}" == true ]] && components_summary="${components_summary} prometheus"
[[ "${ENABLE_GRAFANA}" == true ]] && components_summary="${components_summary} grafana"
[[ "${ENABLE_ZIPKIN}" == true ]] && components_summary="${components_summary} zipkin"
[[ "${ENABLE_GOTENBERG}" == true ]] && components_summary="${components_summary} gotenberg"

echo "[INFO] ${MODE} stack: db=${DB}, mq=${MQ}, target=${TARGET}, project=${PROJECT_NAME},${components_summary:- no extra components}"

if [[ "${MODE}" == "stop" ]]; then
  docker compose \
    "${ENV_FILE_ARGS[@]}" \
    -p "${PROJECT_NAME}" \
    "${compose_files[@]}" \
    stop
else
  docker compose \
    "${ENV_FILE_ARGS[@]}" \
    -p "${PROJECT_NAME}" \
    "${compose_files[@]}" \
    down
fi

echo "[INFO] Done."
