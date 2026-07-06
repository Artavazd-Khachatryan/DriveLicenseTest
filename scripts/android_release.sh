#!/usr/bin/env bash
# Local signed release build (testing/sideload only).
# For Google Play uploads use GitHub Actions → "Android Release (manual)".
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

SIGNING_ENV_DEFAULT="$HOME/Documents/driver_license_test/android/signing.env"

if [[ -f "$SIGNING_ENV_DEFAULT" ]]; then
  # shellcheck source=/dev/null
  source "$SIGNING_ENV_DEFAULT"
fi

cd "$PROJECT_ROOT"

if [[ -n "${CI_BUILD_NUMBER:-}" ]]; then
  echo "Using CI_BUILD_NUMBER=$CI_BUILD_NUMBER"
else
  echo "Local build — versionCode from git commit count (not for Play Store upload)."
  echo "Upload to Play via GitHub Actions → Android Release (manual)."
fi

VERSION="$("$PROJECT_ROOT/scripts/print_app_version.sh")"

echo "Building Android release (AAB + APK)..."
echo "- Project: $PROJECT_ROOT"
echo "- versionName: $VERSION"
echo ""

./gradlew :androidApp:bundleRelease :androidApp:assembleRelease --stacktrace

echo ""
echo "Outputs:"
echo "- AAB: $PROJECT_ROOT/androidApp/build/outputs/bundle/release/"
echo "- APK: $PROJECT_ROOT/androidApp/build/outputs/apk/release/"
