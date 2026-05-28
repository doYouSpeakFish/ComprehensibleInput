#!/usr/bin/env bash
set -euo pipefail

# Deploy a Docker Swarm stack and fail fast with an automatic rollback
# when the backend service does not become healthy within the configured timeout.
#
# Requirements:
# - docker swarm initialized on target host
# - curl available on target host
# - docker CLI access to services and tasks

STACK_NAME="${STACK_NAME:-app}"
COMPOSE_FILE="${COMPOSE_FILE:-docker-compose-prod.yml}"
BACKEND_SERVICE="${BACKEND_SERVICE:-backend}"
POSTGRES_SERVICE="${POSTGRES_SERVICE:-postgres}"
HEALTH_PATH="${HEALTH_PATH:-/health}"
BACKEND_PORT="${BACKEND_PORT:-8080}"
MAX_WAIT_SECONDS="${MAX_WAIT_SECONDS:-300}"
CHECK_INTERVAL_SECONDS="${CHECK_INTERVAL_SECONDS:-5}"
ROLLBACK_ON_FAILURE="${ROLLBACK_ON_FAILURE:-true}"

FULL_BACKEND_SERVICE="${STACK_NAME}_${BACKEND_SERVICE}"
FULL_POSTGRES_SERVICE="${STACK_NAME}_${POSTGRES_SERVICE}"

log() {
  printf '[deploy-check] %s\n' "$*"
}

service_update_state() {
  docker service inspect "$1" --format '{{.UpdateStatus.State}}' 2>/dev/null || true
}

service_update_message() {
  docker service inspect "$1" --format '{{.UpdateStatus.Message}}' 2>/dev/null || true
}

service_running_replicas() {
  docker service ls --format '{{.Name}} {{.Replicas}}' | awk -v n="$1" '$1==n {print $2}'
}

wait_for_postgres_running() {
  local deadline=$((SECONDS + MAX_WAIT_SECONDS))

  log "Waiting for postgres service tasks to start: ${FULL_POSTGRES_SERVICE}"
  while (( SECONDS < deadline )); do
    local replicas
    replicas="$(service_running_replicas "$FULL_POSTGRES_SERVICE")"

    # Format is e.g. 1/1
    if [[ "$replicas" =~ ^([0-9]+)/([0-9]+)$ ]]; then
      local running="${BASH_REMATCH[1]}"
      local desired="${BASH_REMATCH[2]}"
      if [[ "$desired" -gt 0 && "$running" -ge 1 ]]; then
        log "Postgres service has running replicas (${replicas})."
        return 0
      fi
    fi

    sleep "$CHECK_INTERVAL_SECONDS"
  done

  log "Timed out waiting for postgres tasks to start."
  return 1
}

wait_for_backend_ready() {
  local deadline=$((SECONDS + MAX_WAIT_SECONDS))

  log "Waiting for backend health endpoint: http://localhost:${BACKEND_PORT}${HEALTH_PATH}"
  while (( SECONDS < deadline )); do
    local update_state
    update_state="$(service_update_state "$FULL_BACKEND_SERVICE")"
    if [[ "$update_state" == "rollback_completed" || "$update_state" == "rollback_paused" ]]; then
      log "Backend service entered rollback state: ${update_state}"
      return 1
    fi

    if curl --silent --show-error --fail "http://localhost:${BACKEND_PORT}${HEALTH_PATH}" >/dev/null; then
      log "Backend health endpoint is responding successfully."
      return 0
    fi

    sleep "$CHECK_INTERVAL_SECONDS"
  done

  log "Timed out waiting for backend health endpoint to become healthy."
  return 1
}

rollback_backend() {
  log "Triggering rollback for service ${FULL_BACKEND_SERVICE}"
  docker service rollback "$FULL_BACKEND_SERVICE"

  local message
  message="$(service_update_message "$FULL_BACKEND_SERVICE")"
  if [[ -n "$message" ]]; then
    log "Rollback status: ${message}"
  fi
}

main() {
  log "Deploying stack ${STACK_NAME} with compose file ${COMPOSE_FILE}"
  docker stack deploy --with-registry-auth --compose-file "$COMPOSE_FILE" "$STACK_NAME"

  if ! wait_for_postgres_running; then
    log "Deployment failed before backend validation (postgres did not start)."
    exit 1
  fi

  if wait_for_backend_ready; then
    log "Deployment validation succeeded."
    return 0
  fi

  if [[ "$ROLLBACK_ON_FAILURE" == "true" ]]; then
    rollback_backend
  fi

  log "Deployment validation failed. Failing pipeline."
  exit 1
}

main "$@"
