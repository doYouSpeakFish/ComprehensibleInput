#!/usr/bin/env bash
set -euo pipefail

SDK_ROOT="${ANDROID_SDK_ROOT:-$HOME/android-sdk}"
CMDLINE_TOOLS_VERSION="11076708"
CMDLINE_TOOLS_ZIP="commandlinetools-linux-${CMDLINE_TOOLS_VERSION}_latest.zip"
CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/${CMDLINE_TOOLS_ZIP}"
CMDLINE_TOOLS_DIR="${SDK_ROOT}/cmdline-tools"
CMDLINE_TOOLS_LATEST_DIR="${CMDLINE_TOOLS_DIR}/latest"

mkdir -p "${SDK_ROOT}"

if [[ ! -x "${CMDLINE_TOOLS_LATEST_DIR}/bin/sdkmanager" ]]; then
  echo "Downloading Android command line tools..."
  tmpdir="$(mktemp -d)"
  trap 'rm -rf "${tmpdir}"' EXIT
  curl -fsSL "${CMDLINE_TOOLS_URL}" -o "${tmpdir}/${CMDLINE_TOOLS_ZIP}"
  mkdir -p "${CMDLINE_TOOLS_DIR}"
  rm -rf "${CMDLINE_TOOLS_LATEST_DIR}"
  unzip -q "${tmpdir}/${CMDLINE_TOOLS_ZIP}" -d "${tmpdir}"
  mv "${tmpdir}/cmdline-tools" "${CMDLINE_TOOLS_LATEST_DIR}"
  rm -rf "${tmpdir}"
  trap - EXIT
else
  echo "Android command line tools already installed at ${CMDLINE_TOOLS_LATEST_DIR}".
fi

SDK_MANAGER="${CMDLINE_TOOLS_LATEST_DIR}/bin/sdkmanager"

if [[ ! -x "${SDK_MANAGER}" ]]; then
  echo "sdkmanager not found after installation" >&2
  exit 1
fi

export ANDROID_SDK_ROOT="${SDK_ROOT}"
export PATH="${CMDLINE_TOOLS_LATEST_DIR}/bin:${PATH}"

PACKAGES=(
  "platform-tools"
  "platforms;android-36"
  "sources;android-36"
  "build-tools;35.0.0"
)

set +o pipefail
yes | "${SDK_MANAGER}" --sdk_root="${SDK_ROOT}" --licenses >/dev/null
yes | "${SDK_MANAGER}" --sdk_root="${SDK_ROOT}" --install "${PACKAGES[@]}"
set -o pipefail

ROBOLECTRIC_COORDINATE="android-all-instrumented"
ROBOLECTRIC_VERSION="14-robolectric-10818077-i7"
ROBOLECTRIC_REPO_DIR="${HOME}/.m2/repository/org/robolectric/${ROBOLECTRIC_COORDINATE}/${ROBOLECTRIC_VERSION}"
ROBOLECTRIC_BASE_URL="https://repo1.maven.org/maven2/org/robolectric/${ROBOLECTRIC_COORDINATE}/${ROBOLECTRIC_VERSION}"

mkdir -p "${ROBOLECTRIC_REPO_DIR}"

for extension in jar pom; do
  artifact_path="${ROBOLECTRIC_REPO_DIR}/${ROBOLECTRIC_COORDINATE}-${ROBOLECTRIC_VERSION}.${extension}"
  if [[ ! -f "${artifact_path}" ]]; then
    echo "Downloading Robolectric dependency (${extension})..."
    curl -fsSL "${ROBOLECTRIC_BASE_URL}/${ROBOLECTRIC_COORDINATE}-${ROBOLECTRIC_VERSION}.${extension}" -o "${artifact_path}"
  fi
done

if [[ ! -f "local.properties" ]]; then
  printf 'sdk.dir=%s\n' "${SDK_ROOT}" > local.properties
  echo "Created local.properties pointing to ${SDK_ROOT}."
else
  echo "local.properties already exists; not overwriting."
fi

echo "Android SDK installation completed at ${SDK_ROOT}."
echo "Add the following to your shell configuration to use the SDK tools:"
echo "  export ANDROID_SDK_ROOT=\"${SDK_ROOT}\""
echo "  export PATH=\"\$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:\$ANDROID_SDK_ROOT/platform-tools:\$PATH\""

