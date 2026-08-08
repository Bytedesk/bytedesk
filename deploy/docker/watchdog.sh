#!/usr/bin/env bash
# ============================================================================
# Bytedesk Docker Compose Process Watchdog
# ============================================================================
# This script monitors the bytedesk Docker container and automatically
# restarts the compose stack if the bytedesk service goes down unexpectedly.
#
# Usage:
#   ./watchdog.sh start    - Start the watchdog daemon
#   ./watchdog.sh stop     - Stop the watchdog daemon
#   ./watchdog.sh status   - Check watchdog and container status
#   ./watchdog.sh restart  - Restart the watchdog daemon
#
# Prerequisites:
#   - Docker and docker compose must be installed
#   - deploy/docker/start.sh must be present and executable
#   - The compose stack should be initially started (by start.sh or docker compose up)
#
# Configuration (override via environment variables):
#   WATCHDOG_CHECK_INTERVAL  - Seconds between health checks (default: 10)
#   WATCHDOG_STARTUP_WAIT    - Seconds to wait after restart before checking (default: 90)
#   WATCHDOG_MAX_RESTARTS    - Max restarts within the burst window (default: 5)
#   WATCHDOG_BURST_WINDOW    - Burst window in seconds (default: 300 = 5 min)
#   WATCHDOG_LOG_FILE        - Watchdog log path (default: ./watchdog.log)
#   WATCHDOG_CONTAINER_NAME  - Container name to monitor (default: bytedesk)
#   WATCHDOG_DB              - Database type for start.sh (default: mysql)
#   WATCHDOG_MQ              - MQ type for start.sh (default: artemis)
#   WATCHDOG_SCENARIO        - Scenario for start.sh (default: standard)
# ============================================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# --- Configuration -----------------------------------------------------------
CHECK_INTERVAL="${WATCHDOG_CHECK_INTERVAL:-10}"
STARTUP_WAIT="${WATCHDOG_STARTUP_WAIT:-90}"
MAX_RESTARTS="${WATCHDOG_MAX_RESTARTS:-5}"
BURST_WINDOW="${WATCHDOG_BURST_WINDOW:-300}"
LOG_FILE="${WATCHDOG_LOG_FILE:-${SCRIPT_DIR}/watchdog.log}"
PID_FILE="${SCRIPT_DIR}/watchdog.pid"
LOCK_FILE="${SCRIPT_DIR}/watchdog.lock"
RESTART_MARKER="WATCHDOG: restart attempt"

CONTAINER_NAME="${WATCHDOG_CONTAINER_NAME:-bytedesk}"
DB="${WATCHDOG_DB:-mysql}"
MQ="${WATCHDOG_MQ:-artemis}"
SCENARIO="${WATCHDOG_SCENARIO:-standard}"

mkdir -p "$(dirname "${LOG_FILE}")"

timestamp_to_epoch() {
    local ts="$1"
    if date -j -f '%Y-%m-%d %H:%M:%S' "${ts}" '+%s' >/dev/null 2>&1; then
        date -j -f '%Y-%m-%d %H:%M:%S' "${ts}" '+%s'
    elif date -d "${ts}" '+%s' >/dev/null 2>&1; then
        date -d "${ts}" '+%s'
    else
        echo 0
    fi
}

acquire_lock() {
    if command -v flock >/dev/null 2>&1; then
        exec 9>"${LOCK_FILE}"
        flock -n 9
        return $?
    fi

    if [ -d "${LOCK_FILE}.d" ] && ! watchdog_running; then
        rmdir "${LOCK_FILE}.d" 2>/dev/null || true
    fi

    if mkdir "${LOCK_FILE}.d" 2>/dev/null; then
        return 0
    fi

    return 1
}

release_lock() {
    if command -v flock >/dev/null 2>&1; then
        exec 9>&-
    fi
    rmdir "${LOCK_FILE}.d" 2>/dev/null || true
}

# --- Logging helper -----------------------------------------------------------
log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*" | tee -a "${LOG_FILE}"
}

# --- Check if watchdog is already running -------------------------------------
watchdog_running() {
    if [ -f "${PID_FILE}" ]; then
        local pid
        pid=$(cat "${PID_FILE}" 2>/dev/null || true)
        if [ -n "${pid}" ] && kill -0 "${pid}" 2>/dev/null; then
            return 0
        fi
    fi
    return 1
}

# --- Check if the bytedesk container is running -------------------------------
container_running() {
    local status
    status=$(docker inspect -f '{{.State.Status}}' "${CONTAINER_NAME}" 2>/dev/null || echo "not_found")
    if [ "${status}" = "running" ]; then
        return 0
    fi
    # Also check if container exists but is paused/restarting
    if [ "${status}" = "restarting" ]; then
        return 0
    fi
    return 1
}

# --- Get container exit code (if stopped) -------------------------------------
container_exit_code() {
    docker inspect -f '{{.State.ExitCode}}' "${CONTAINER_NAME}" 2>/dev/null || echo "unknown"
}

# --- Track restart count within the burst window ------------------------------
get_restart_count() {
    local now count
    now=$(date +%s)
    count=0
    if [ -f "${LOG_FILE}" ]; then
        while IFS= read -r line; do
            local ts
            ts=$(echo "${line}" | grep -o '^\[.*\]' | tr -d '[]' || true)
            if [ -n "${ts}" ]; then
                local epoch
                epoch=$(timestamp_to_epoch "${ts}")
                if [ $((now - epoch)) -lt "${BURST_WINDOW}" ] && echo "${line}" | grep -q "${RESTART_MARKER}"; then
                    count=$((count + 1))
                fi
            fi
        done < "${LOG_FILE}"
    fi
    echo "${count}"
}

# --- Restart the Docker Compose app stack -------------------------------------
restart_compose_app() {
    log "${RESTART_MARKER} Docker Compose app stack"
    log "WATCHDOG: restarting Docker Compose app stack (db=${DB}, mq=${MQ}, scenario=${SCENARIO})"

    cd "${SCRIPT_DIR}" || exit 1

    if [ -f "./start.sh" ]; then
        # start.sh brings up middleware + bytedesk app
        # We only need to restart the bytedesk app, not middleware
        # Use docker compose up -d to restart just the bytedesk service
        local compose_files=()
        compose_files+=("-f" "compose-base.yaml")
        compose_files+=("-f" "compose-db-${DB}.yaml")
        compose_files+=("-f" "compose-mq-${MQ}.yaml")

        case "${SCENARIO}" in
            standard)
                compose_files+=("-f" "compose-scenario-standard.yaml")
                ;;
            noai)
                compose_files+=("-f" "compose-scenario-noai.yaml")
                ;;
            call)
                compose_files+=("-f" "compose-scenario-call.yaml")
                ;;
            webrtc)
                compose_files+=("-f" "compose-scenario-webrtc.yaml")
                ;;
            call-webrtc)
                compose_files+=("-f" "compose-scenario-call.yaml")
                compose_files+=("-f" "compose-scenario-webrtc.yaml")
                ;;
        esac

        compose_files+=("-f" "compose-app-bytedesk.yaml")

        # Only restart the bytedesk service, not the middleware
        if docker compose "${compose_files[@]}" up -d --no-deps bytedesk 2>&1 | tee -a "${LOG_FILE}"; then
            log "WATCHDOG: docker compose up succeeded, waiting ${STARTUP_WAIT}s for startup"
            sleep "${STARTUP_WAIT}"
            return 0
        else
            log "WATCHDOG: ERROR - docker compose up failed"
            return 1
        fi
    else
        log "WATCHDOG: ERROR - start.sh not found in ${SCRIPT_DIR}"
        return 1
    fi
}

# --- Check burst limit --------------------------------------------------------
check_burst_limit() {
    local count
    count=$(get_restart_count)
    if [ "${count}" -ge "${MAX_RESTARTS}" ]; then
        log "WATCHDOG: FATAL - ${count} restarts in last ${BURST_WINDOW}s (limit: ${MAX_RESTARTS}). Giving up."
        log "WATCHDOG: Please investigate the root cause manually, then restart watchdog."
        return 1
    fi
    return 0
}

# --- Main watchdog loop -------------------------------------------------------
run_watchdog() {
    # Prevent duplicate watchdog
    if watchdog_running; then
        log "WATCHDOG: already running (pid=$(cat "${PID_FILE}"))"
        exit 1
    fi

    # Acquire lock
    if ! acquire_lock; then
        log "WATCHDOG: another instance is already starting"
        exit 1
    fi

    echo $$ > "${PID_FILE}"
    log "WATCHDOG: docker daemon started (pid=$$, check_interval=${CHECK_INTERVAL}s, startup_wait=${STARTUP_WAIT}s, container=${CONTAINER_NAME})"

    trap 'rm -f "${PID_FILE}"; release_lock; log "WATCHDOG: docker daemon stopped (pid=$$)"' EXIT

    while true; do
        if ! container_running; then
            local exit_code
            exit_code=$(container_exit_code)
            log "WATCHDOG: container '${CONTAINER_NAME}' is DOWN (exit_code=${exit_code})! Attempting restart..."

            # Don't restart if the container was explicitly stopped (exit code 0 when docker stop)
            # exit code 137 typically means OOMKilled or docker kill
            if [ "${exit_code}" = "0" ]; then
                log "WATCHDOG: container exited cleanly (exit_code=0). This may be an intentional stop. Skipping auto-restart."
                log "WATCHDOG: If this is unexpected, manually restart with: ./start.sh ${DB} ${MQ} ${SCENARIO} all"
                rm -f "${PID_FILE}"
                exit 0
            fi

            if ! check_burst_limit; then
                rm -f "${PID_FILE}"
                exit 1
            fi

            restart_compose_app
            sleep 5
        fi

        sleep "${CHECK_INTERVAL}"
    done
}

# --- Stop watchdog ------------------------------------------------------------
stop_watchdog() {
    if watchdog_running; then
        local pid
        pid=$(cat "${PID_FILE}")
        log "WATCHDOG: stopping docker daemon (pid=${pid})"
        kill "${pid}" 2>/dev/null || true
        sleep 1
        if kill -0 "${pid}" 2>/dev/null; then
            kill -9 "${pid}" 2>/dev/null || true
        fi
        rm -f "${PID_FILE}"
        echo "Docker watchdog stopped. (Container is NOT stopped)"
    else
        echo "Docker watchdog is not running."
    fi
}

# --- Status check -------------------------------------------------------------
status_watchdog() {
    echo "=== Docker Watchdog Status ==="
    if watchdog_running; then
        echo "Watchdog: RUNNING (pid=$(cat "${PID_FILE}"))"
    else
        echo "Watchdog: STOPPED"
    fi

    echo ""
    echo "=== Container Status ==="
    if container_running; then
        echo "Container '${CONTAINER_NAME}': RUNNING"
        local uptime status
        uptime=$(docker inspect -f '{{.State.StartedAt}}' "${CONTAINER_NAME}" 2>/dev/null || echo "unknown")
        status=$(docker inspect -f '{{.State.Status}}' "${CONTAINER_NAME}" 2>/dev/null || echo "unknown")
        echo "Status:  ${status}"
        echo "Started: ${uptime}"
        echo ""
        # Show recent container logs
        echo "--- Last 5 container log lines ---"
        docker logs --tail 5 "${CONTAINER_NAME}" 2>/dev/null || echo "(unable to fetch logs)"
    else
        local exit_code status
        status=$(docker inspect -f '{{.State.Status}}' "${CONTAINER_NAME}" 2>/dev/null || echo "not_found")
        exit_code=$(container_exit_code)
        echo "Container '${CONTAINER_NAME}': ${status} (exit_code=${exit_code})"
    fi

    echo ""
    echo "=== Restart Statistics ==="
    local count
    count=$(get_restart_count)
    echo "Restarts in last ${BURST_WINDOW}s: ${count} / ${MAX_RESTARTS} max"

    echo ""
    echo "=== Configuration ==="
    echo "Check Interval: ${CHECK_INTERVAL}s"
    echo "Startup Wait:   ${STARTUP_WAIT}s"
    echo "Max Restarts:   ${MAX_RESTARTS}"
    echo "Burst Window:   ${BURST_WINDOW}s"
    echo "Container:      ${CONTAINER_NAME}"
    echo "DB/MQ/Scenario: ${DB}/${MQ}/${SCENARIO}"
    echo "Log File:       ${LOG_FILE}"
}

# --- Main ---------------------------------------------------------------------
case "${1:-}" in
    start)
        if watchdog_running; then
            log "WATCHDOG: already running (pid=$(cat "${PID_FILE}"))"
            exit 0
        fi

        # Check if container is already running
        if ! container_running; then
            log "WATCHDOG: container '${CONTAINER_NAME}' not running, starting it first"
            restart_compose_app
            sleep 5
        fi

        nohup bash "${BASH_SOURCE[0]}" _run_daemon > /dev/null 2>&1 &
        echo "Docker watchdog started in background. Check status with: $0 status"
        ;;
    _run_daemon)
        run_watchdog
        ;;
    stop)
        stop_watchdog
        ;;
    status)
        status_watchdog
        ;;
    restart)
        stop_watchdog
        sleep 2
        bash "${BASH_SOURCE[0]}" start
        ;;
    *)
        echo "Usage: $0 {start|stop|status|restart}"
        echo ""
        echo "  start   - Start the watchdog daemon (auto-starts container if not running)"
        echo "  stop    - Stop the watchdog daemon (does NOT stop the container)"
        echo "  status  - Show watchdog and container status"
        echo "  restart - Restart the watchdog daemon"
        echo ""
        echo "Environment variables:"
        echo "  WATCHDOG_CHECK_INTERVAL  Seconds between checks (default: 10)"
        echo "  WATCHDOG_STARTUP_WAIT    Seconds to wait after restart (default: 90)"
        echo "  WATCHDOG_MAX_RESTARTS    Max restarts in burst window (default: 5)"
        echo "  WATCHDOG_BURST_WINDOW    Burst window in seconds (default: 300)"
        echo "  WATCHDOG_LOG_FILE        Log file path (default: ./watchdog.log)"
        echo "  WATCHDOG_CONTAINER_NAME  Container name to monitor (default: bytedesk)"
        echo "  WATCHDOG_DB              Database type (default: mysql)"
        echo "  WATCHDOG_MQ              MQ type (default: artemis)"
        echo "  WATCHDOG_SCENARIO        Scenario (default: standard)"
        exit 1
        ;;
esac
