#!/usr/bin/env bash
# 快速切换数据库容器：只替换 DB 容器，其余中间件（redis/es/freeswitch/...）保持运行不动。
# 用法：./switch-db.sh <mysql|postgresql|pg|oracle|kingbase|kingbase9>
# 说明：
#   - 数据卷独立，切换后各库数据仍然保留；
#   - 首次切换某库会拉镜像/初始化，之后切换只需几秒；
#   - 应用侧数据源请同步切换：BYTEDESK_DATASOURCE_ACTIVE=<库> 或改 application-local.properties 的 bytedesk.datasource.active。

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_NAME="${PROJECT_NAME:-bytedesk}"
COMPOSE_DIR="${SCRIPT_DIR}/compose"
ENV_FILE="${SCRIPT_DIR}/.env"

usage() {
  sed -n '2,7p' "${BASH_SOURCE[0]}" | sed 's/^# \{0,1\}//'
  exit 1
}

DB="${1:-}"
case "${DB}" in
  mysql)                 container="mysql-bytedesk";       file="compose-mysql.yaml" ;;
  postgresql|pg)         container="postgresql-bytedesk";  file="compose-postgresql.yaml" ;;
  oracle)                container="oracle-bytedesk";      file="compose-oracle.yaml" ;;
  kingbase|kingbase9)    container="kingbase9-bytedesk";   file="compose-kingbase.yaml" ;;
  -h|--help|help) usage ;;
  *) usage ;;
esac

# 1) 停止其它数据库容器（目标库之外的库），保留其余中间件
for other in mysql-bytedesk postgresql-bytedesk oracle-bytedesk kingbase9-bytedesk; do
  if [[ "${other}" == "${container}" ]]; then
    continue
  fi
  if docker ps -q --filter "name=^/${other}$" | grep -q .; then
    echo "[INFO] 停止旧数据库容器: ${other}"
    docker rm -f "${other}" >/dev/null
  fi
done

# 2) 启动目标数据库（已在运行则跳过）
if docker ps -q --filter "name=^/${container}$" | grep -q .; then
  echo "[INFO] ${container} 已在运行，跳过启动"
else
  echo "[INFO] 启动 ${container} ..."
  docker compose -p "${PROJECT_NAME}" --env-file "${ENV_FILE}" -f "${COMPOSE_DIR}/${file}" up -d
fi

# 3) 等待就绪（基于容器健康检查）
wait_healthy() {
  local c="$1"
  local tries=60
  for ((i=1; i<=tries; i++)); do
    local st
    st="$(docker inspect --format '{{.State.Health.Status}}' "${c}" 2>/dev/null || echo 'none')"
    if [[ "${st}" == "healthy" ]]; then
      return 0
    fi
    if [[ "${st}" == "unhealthy" ]]; then
      echo "[WARN] ${c} 健康检查失败 (unhealthy)"
      return 1
    fi
    sleep 2
  done
  echo "[WARN] 等待 ${c} 就绪超时（首次拉镜像/初始化会较慢）"
  return 1
}
wait_healthy "${container}" || true

echo "[INFO] 完成：当前数据库 = ${DB}"
echo "[INFO] 应用侧切换：以 BYTEDESK_DATASOURCE_ACTIVE=${DB} 启动，或修改 application-local.properties 中 bytedesk.datasource.active=${DB}"
