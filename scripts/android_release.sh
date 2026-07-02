#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

SIGNING_ENV_DEFAULT="$HOME/Documents/driver_license_test/android/signing.env"

if [[ -f "$SIGNING_ENV_DEFAULT" ]]; then
  # shellcheck source=/dev/null
  source "$SIGNING_ENV_DEFAULT"
fi

cd "$PROJECT_ROOT"

echo "Building Android release (AAB + APK)..."
echo "- Project: $PROJECT_ROOT"
echo "- versionName: $(grep 'versionName = ' androidApp/build.gradle.kts | awk -F'\"' '{print $2}')"
echo "- versionCode: ${CI_BUILD_NUMBER:-local}"
echo ""

./gradlew :androidApp:bundleRelease :androidApp:assembleRelease --stacktrace

echo ""
echo "Outputs:"
echo "- AAB: $PROJECT_ROOT/androidApp/build/outputs/bundle/release/"
echo "- APK: $PROJECT_ROOT/androidApp/build/outputs/apk/release/"
