#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# Run the existing SDK installer
"${PROJECT_ROOT}/scripts/setup-android-sdk.sh"

# Write local.properties to the project root (the installer uses relative CWD,
# which is wrong in the Claude environment)
SDK_ROOT="${ANDROID_SDK_ROOT:-$HOME/android-sdk}"
printf 'sdk.dir=%s\n' "${SDK_ROOT}" > "${PROJECT_ROOT}/local.properties"

# Persist SDK env vars into ~/.bashrc so every shell Claude spawns picks them up
BASHRC="$HOME/.bashrc"
if ! grep -q 'ANDROID_SDK_ROOT' "${BASHRC}" 2>/dev/null; then
  cat >> "${BASHRC}" <<EOF

# Android SDK (added by claude-setup.sh)
export ANDROID_SDK_ROOT="${SDK_ROOT}"
export PATH="\$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:\$ANDROID_SDK_ROOT/platform-tools:\$PATH"
EOF
  echo "Android SDK env vars added to ${BASHRC}."
else
  echo "Android SDK env vars already present in ${BASHRC}."
fi
