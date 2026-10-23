#!/usr/bin/env bash

set -e

source "$(dirname "${BASH_SOURCE[0]}")/setenv.sh"

APP_HOME="${PROJECT_PATH}/local-data/${PROJECT_NAME}"
LOG_DIR="${APP_HOME}/logs"
STARTUP_LOG="${LOG_DIR}/local-debug.log"
HOME_URL=http://127.0.0.1:8080
READINESS_URL=http://127.0.0.1:8081/public/actuator/health/readiness
APP_PID=

prepare_logging() {
  mkdir -p "${LOG_DIR}" 2>/dev/null || die "ERROR: Cannot create log directory: ${LOG_DIR}"
  if ! { printf '\n=== FreeChat local startup: %s ===\n' "$(date '+%Y-%m-%d %H:%M:%S')" >>"${STARTUP_LOG}"; } 2>/dev/null; then
    die "ERROR: Cannot write startup log: ${STARTUP_LOG}"
  fi
  printf 'Startup/application log: %s\n' "${STARTUP_LOG}"
}

check_environment() {
  command -v curl >/dev/null || die "ERROR: curl is required to check whether FreeChat is ready."

  local port
  for port in 8080 8081; do
    if (exec 3<>"/dev/tcp/127.0.0.1/${port}") 2>/dev/null; then
      die "ERROR: Port ${port} is already in use. Stop the existing service before starting FreeChat."
    fi
  done
}

start_dependencies() {
  printf 'Starting local dependencies...\n'
  bash "${SCRIPTS_PATH}/local-deps.sh" --start >>"${STARTUP_LOG}" 2>&1 \
    || die "ERROR: Failed to start local dependencies. Check ${STARTUP_LOG}."
}

stop_dependencies() {
  printf '\nStopping local dependencies...\n'
  if ! bash "${SCRIPTS_PATH}/local-deps.sh" --stop >>"${STARTUP_LOG}" 2>&1; then
    printf 'ERROR: Failed to stop local dependencies. Check %s.\n' "${STARTUP_LOG}" >&2
    return 1
  fi
  printf 'Local dependencies stopped.\n'
}

build_app() {
  printf '\nBuilding FreeChat from source...\n'
  mvn -B -ntp -f "${PROJECT_PATH}/pom.xml" -pl "${STARTER_MODULE}" -am package -DskipTests >>"${STARTUP_LOG}" 2>&1 \
    || die "ERROR: Failed to build FreeChat. Check ${STARTUP_LOG}."
}

start_app() {
  printf '\nStarting FreeChat; waiting up to 180 seconds for readiness...\n'
  java \
    -Dspring.config.location=classpath:/application.yml \
    "-DAPP_HOME=${APP_HOME}" \
    -Dspring.profiles.active=local \
    "-Dapp.homeUrl=${HOME_URL}" \
    -jar "${PROJECT_PATH}/${STARTER_MODULE}/target/${STARTER_MODULE}-${VERSION}.jar" \
    --server.address=127.0.0.1 \
    --management.server.address=127.0.0.1 >>"${STARTUP_LOG}" 2>&1 &
  APP_PID=$!
}

stop_app() {
  if [[ -n "${APP_PID}" ]] && kill -0 "${APP_PID}" 2>/dev/null; then
    kill "${APP_PID}" 2>/dev/null || true
    wait "${APP_PID}" || true
  fi
}

cleanup() {
  local status=$?
  trap - EXIT
  trap '' INT TERM
  stop_app
  if ! stop_dependencies; then
    if [[ "${status}" -eq 0 ]]; then
      status=1
    fi
  fi
  exit "${status}"
}

handle_signal() {
  printf '\nStopping FreeChat...\n'
  exit "$1"
}

wait_for_readiness() {
  local ready=false
  local deadline=$((SECONDS + 180))
  local status=0

  while kill -0 "${APP_PID}" 2>/dev/null && ((SECONDS < deadline)); do
    if curl --silent --fail --max-time 2 "${READINESS_URL}" >/dev/null 2>>"${STARTUP_LOG}"; then
      ready=true
      break
    fi
    sleep 1
  done

  if [[ "${ready}" != true ]]; then
    if ! kill -0 "${APP_PID}" 2>/dev/null; then
      wait "${APP_PID}" || status=$?
      APP_PID=
      die "ERROR: FreeChat exited before becoming ready (exit code ${status}). Check ${STARTUP_LOG}."
    fi
    die "ERROR: FreeChat did not become ready within 180 seconds. Check ${STARTUP_LOG}."
  fi
}

print_startup_links() {
  printf '\nFreeChat is ready. Open these links:\n'
  printf '  Home:     %s\n' "${HOME_URL}"
  printf '  API docs: %s/public/docs/api/swagger-ui\n' "${HOME_URL}"
  printf '  Health:   %s\n' "${READINESS_URL}"
  printf '  Log:      %s\n' "${STARTUP_LOG}"
  printf 'Press Ctrl+C to stop FreeChat and local dependency containers.\n\n'
}

wait_for_exit() {
  local status=0
  # Redirecting wait can delay signal traps in Bash 3.2.
  wait "${APP_PID}" || status=$?
  APP_PID=
  if [[ "${status}" -ne 0 ]]; then
    printf 'ERROR: FreeChat exited unexpectedly (exit code %s). Check %s.\n' "${status}" "${STARTUP_LOG}" >&2
    exit "${status}"
  fi
  printf '\nFreeChat stopped.\n'
}

main() {
  prepare_logging
  check_environment

  trap cleanup EXIT
  trap 'handle_signal 130' INT
  trap 'handle_signal 143' TERM

  start_dependencies
  build_app
  start_app
  wait_for_readiness
  print_startup_links
  wait_for_exit
}

main
