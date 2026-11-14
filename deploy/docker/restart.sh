#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_NAME="${PROJECT_NAME:-bytedesk}"

# ============================================================
# 微语 Docker 容器重启脚本（纯关键字驱动，直达容器名，等价 docker restart）
#
# 用法：./restart.sh [关键字...]
# 关键字任意顺序、任意组合；不传参数等价于 ./restart.sh all（重启项目内全部容器）
#
# 数据库（最多选 1 个，默认 mysql）：
#   mysql | postgresql(别名 pg) | oracle | kingbase(别名 kingbase9)
# 消息队列（最多选 1 个，默认 artemis）：
#   artemis | rabbitmq
# 核心中间件：
#   redis | elasticsearch(别名 es)
# 组件（任意组合）：
#   freeswitch | mrcp | coturn | janus
#   searxng(别名 search) | neo4j | logstash | kibana | minio | rustfs
#   prometheus | grafana | zipkin | otelcol(别名 opentelemetry) | gotenberg | james
# 组合关键字：
#   call   = freeswitch + mrcp
#   webrtc = coturn + janus
#   obs(别名 observability) = prometheus + grafana + zipkin + otelcol
# 应用/范围：
#   app(别名 bytedesk) = 仅重启 bytedesk 应用容器
#   middleware = redis + elasticsearch + 所选db + 所选mq
#   all = 重启项目内全部容器（默认；按依赖顺序：db → redis/es → mq → 组件 → 应用最后）
#
# 示例：
#   ./restart.sh                       # 重启项目内全部容器（应用最后重启）
#   ./restart.sh freeswitch            # 仅重启 FreeSWITCH
#   ./restart.sh call                  # 重启 freeswitch + mrcp
#   ./restart.sh app                   # 仅重启 bytedesk 应用容器
#   ./restart.sh postgresql rabbitmq middleware
#
# 说明：
# - restart 保留容器原有配置（等价 docker restart）；修改 .env 后需重新执行
#   ./start.sh（compose up -d 会按需重建容器）才会应用新配置。
# - 容器不存在（未启动过或已 down）时跳过并提示。
# - 可通过 PROJECT_NAME 环境变量指定 compose 项目名（默认 bytedesk）。
# ============================================================

usage() {
  sed -n '9,44p' "${BASH_SOURCE[0]}" | sed 's/^# \{0,1\}//'
  exit 1
}

# ============================================================
# .env 读取辅助（与 start.sh 保持一致）
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

# MRCP 容器名可由 .env 覆盖
MRCP_CONTAINER="$(get_env_value MRCP_CONTAINER_NAME mrcp-server-bytedesk)"

# 按依赖顺序排列的已知容器清单（db → redis/es → mq → 组件 → 应用），
# 未知容器（如自定义 compose 追加的）排在最后
CANONICAL_ORDER="mysql-bytedesk postgresql-bytedesk oracle-bytedesk kingbase9-bytedesk redis-bytedesk elasticsearch-bytedesk artemis-bytedesk rabbitmq-bytedesk searxng-bytedesk neo4j-bytedesk minio-bytedesk rustfs-bytedesk gotenberg-bytedesk logstash-bytedesk james-bytedesk ${MRCP_CONTAINER} freeswitch-bytedesk coturn-bytedesk janus-bytedesk prometheus-bytedesk grafana-bytedesk zipkin-bytedesk otelcol-bytedesk kibana-bytedesk bytedesk"

DB=""
MQ=""
SCOPE=""
TARGETS=""

add_targets() {
  local t
  for t in "$@"; do
    case " ${TARGETS} " in
      *" ${t} "*) ;; # 已存在，去重
      *) TARGETS="${TARGETS:+${TARGETS} }${t}" ;;
    esac
  done
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

set_scope() {
  if [[ -n "${SCOPE}" && "${SCOPE}" != "$1" ]]; then
    echo "[ERROR] Conflicting scope keywords: '${SCOPE}' and '$1'."
    exit 1
  fi
  SCOPE="$1"
}

for arg in "$@"; do
  case "${arg}" in
    mysql) set_db mysql; add_targets mysql-bytedesk ;;
    postgresql|pg) set_db postgresql; add_targets postgresql-bytedesk ;;
    oracle) set_db oracle; add_targets oracle-bytedesk ;;
    kingbase|kingbase9) set_db kingbase; add_targets kingbase9-bytedesk ;;
    artemis) set_mq artemis; add_targets artemis-bytedesk ;;
    rabbitmq) set_mq rabbitmq; add_targets rabbitmq-bytedesk ;;
    redis) add_targets redis-bytedesk ;;
    elasticsearch|es) add_targets elasticsearch-bytedesk ;;
    freeswitch) add_targets freeswitch-bytedesk ;;
    mrcp) add_targets "${MRCP_CONTAINER}" ;;
    coturn) add_targets coturn-bytedesk ;;
    janus) add_targets janus-bytedesk ;;
    searxng|search) add_targets searxng-bytedesk ;;
    neo4j) add_targets neo4j-bytedesk ;;
    logstash) add_targets logstash-bytedesk ;;
    kibana) add_targets kibana-bytedesk ;;
    minio) add_targets minio-bytedesk ;;
    rustfs) add_targets rustfs-bytedesk ;;
    prometheus) add_targets prometheus-bytedesk ;;
    grafana) add_targets grafana-bytedesk ;;
    zipkin) add_targets zipkin-bytedesk ;;
    otelcol|opentelemetry) add_targets otelcol-bytedesk ;;
    gotenberg) add_targets gotenberg-bytedesk ;;
    james) add_targets james-bytedesk ;;
    call)
      add_targets freeswitch-bytedesk "${MRCP_CONTAINER}"
      ;;
    webrtc)
      add_targets coturn-bytedesk janus-bytedesk
      ;;
    obs|observability)
      add_targets prometheus-bytedesk grafana-bytedesk zipkin-bytedesk otelcol-bytedesk
      ;;
    app|bytedesk) add_targets bytedesk ;;
    middleware) set_scope middleware ;;
    all) set_scope all ;;
    -h|--help|help) usage ;;
    *)
      echo "[ERROR] Unknown keyword: '${arg}'"
      echo "Allowed: mysql|postgresql|pg|oracle|kingbase|kingbase9 artemis|rabbitmq redis|elasticsearch|es"
      echo "        freeswitch mrcp coturn janus searxng|search neo4j logstash kibana minio rustfs prometheus grafana zipkin otelcol gotenberg"
      echo "        james call webrtc obs app middleware all"
      exit 1
      ;;
  esac
done

# ============================================================
# 范围展开
# ============================================================

# 不传任何参数 = all
if [[ -z "${SCOPE}" && -z "${TARGETS}" ]]; then
  SCOPE="all"
fi

# middleware = redis + elasticsearch + 所选 db（默认 mysql）+ 所选 mq（默认 artemis）
if [[ "${SCOPE}" == "middleware" ]]; then
  DB="${DB:-mysql}"
  MQ="${MQ:-artemis}"
  case "${DB}" in
    mysql) add_targets mysql-bytedesk ;;
    postgresql) add_targets postgresql-bytedesk ;;
    oracle) add_targets oracle-bytedesk ;;
    kingbase) add_targets kingbase9-bytedesk ;;
  esac
  case "${MQ}" in
    artemis) add_targets artemis-bytedesk ;;
    rabbitmq) add_targets rabbitmq-bytedesk ;;
  esac
  add_targets redis-bytedesk elasticsearch-bytedesk
fi

# all = 枚举项目内全部容器（含已停止的，一并重启）
if [[ "${SCOPE}" == "all" ]]; then
  project_containers="$(docker ps -a --filter "label=com.docker.compose.project=${PROJECT_NAME}" --format '{{.Names}}' 2>/dev/null || true)"
  if [[ -z "${project_containers}" ]]; then
    echo "[ERROR] 项目 '${PROJECT_NAME}' 下没有任何容器（未启动或已 down），请先执行 ./start.sh"
    exit 1
  fi
  # shellcheck disable=SC2086
  add_targets ${project_containers}
fi

if [[ -z "${TARGETS}" ]]; then
  echo "[ERROR] 未找到任何目标容器。"
  exit 1
fi

# ============================================================
# 按依赖顺序排序（已知容器按 CANONICAL_ORDER，未知容器排最后）
# ============================================================
ordered_targets=""
for c in ${CANONICAL_ORDER}; do
  case " ${TARGETS} " in
    *" ${c} "*) ordered_targets="${ordered_targets:+${ordered_targets} }${c}" ;;
  esac
done
for c in ${TARGETS}; do
  case " ${CANONICAL_ORDER} " in
    *" ${c} "*) ;; # 已排序
    *) ordered_targets="${ordered_targets:+${ordered_targets} }${c}" ;;
  esac
done

# ============================================================
# 执行重启
# ============================================================
echo "[INFO] Restarting: ${ordered_targets} (project=${PROJECT_NAME})"

restarted=0
skipped=0
failed=0
for c in ${ordered_targets}; do
  if ! docker container inspect "${c}" >/dev/null 2>&1; then
    echo "[SKIP] ${c} 不存在（未启动或已 down），如需启动请执行 ./start.sh"
    skipped=$((skipped + 1))
    continue
  fi
  echo "[INFO] Restarting ${c} ..."
  if docker restart "${c}" >/dev/null; then
    restarted=$((restarted + 1))
  else
    echo "[ERROR] 重启 ${c} 失败"
    failed=$((failed + 1))
  fi
done

if [[ "${restarted}" -eq 0 ]]; then
  echo "[ERROR] 没有容器被重启（成功 0，跳过 ${skipped}，失败 ${failed}）。"
  exit 1
fi

echo "[INFO] 重启完成（成功 ${restarted}，跳过 ${skipped}，失败 ${failed}），当前状态："
for c in ${ordered_targets}; do
  if docker container inspect "${c}" >/dev/null 2>&1; then
    status="$(docker inspect --format '{{.State.Status}}' "${c}" 2>/dev/null || echo unknown)"
    health="$(docker inspect --format '{{if .State.Health}}{{.State.Health.Status}}{{else}}none{{end}}' "${c}" 2>/dev/null || echo '')"
    echo "  ${c}: status=${status}${health:+, health=${health}}"
  fi
done

if [[ "${failed}" -gt 0 ]]; then
  exit 1
fi

echo "[INFO] Done."
