#!/usr/bin/env bash
# Serialize Gradle invocations across the parallel Claude Code Stop hooks.
#
# The Stop hooks in .claude/settings.json run in parallel, and several of them
# invoke Gradle. Two Gradle builds running at once in the same checkout race on
# the shared configuration cache (.gradle/configuration-cache/) and on task
# output directories under build/, which surfaces as EOFException and
# NoSuchFileException. This wrapper takes an exclusive lock so the builds queue
# and run one at a time. The hook agents themselves still run concurrently;
# only the actual Gradle execution is serialized.
#
# Use it exactly like ./gradlew, e.g.:
#   ./scripts/gradlew-lock.sh detekt
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
gradlew="${repo_root}/gradlew"
lock_file="${repo_root}/.gradle/hook-build.lock"

mkdir -p "$(dirname "${lock_file}")"

if command -v flock >/dev/null 2>&1; then
  # -x: exclusive lock. flock holds it for the lifetime of the gradle process
  # and releases it automatically on exit, so queued builds proceed in turn.
  exec flock -x "${lock_file}" "${gradlew}" "$@"
else
  # flock is unavailable (e.g. on macOS); fall back to running Gradle directly
  # so behaviour is no worse than calling ./gradlew without the lock.
  echo "gradlew-lock: 'flock' not found; running Gradle without serialization." >&2
  exec "${gradlew}" "$@"
fi
