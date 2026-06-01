#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# Create google-services.json
cat > "${PROJECT_ROOT}/app/google-services.json" <<'EOF'
{
  "project_info": {
    "project_number": "824625580730",
    "project_id": "language-this",
    "storage_bucket": "language-this.firebasestorage.app"
  },
  "client": [
    {
      "client_info": {
        "mobilesdk_app_id": "1:824625580730:android:4e96e01fce9aa61a211083",
        "android_client_info": {
          "package_name": "in.comprehensible"
        }
      },
      "oauth_client": [],
      "api_key": [
        {
          "current_key": "AIzaSyBuO8XwuuIlxgwtuLhAngdjqWC8RvmG3-g"
        }
      ],
      "services": {
        "appinvite_service": {
          "other_platform_oauth_client": []
        }
      }
    }
  ],
  "configuration_version": "1"
}
EOF

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
