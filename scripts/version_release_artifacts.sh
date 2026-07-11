#!/usr/bin/env bash
# Copies release AAB/APK with the build version appended to the filename.
# Usage: ./scripts/version_release_artifacts.sh [X.Y.Z.build]
# If omitted, resolves version via print_app_version.sh.
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
VERSION="${1:-$("$PROJECT_ROOT/scripts/print_app_version.sh")}"

copy_with_version() {
  local dir="$1"
  local ext="$2"
  local src="$dir/androidApp-release.${ext}"
  local dst="$dir/androidApp-release-${VERSION}.${ext}"

  if [[ ! -f "$src" ]]; then
    echo "Release artifact not found: $src" >&2
    return 1
  fi

  cp "$src" "$dst"
  echo "$dst"
}

AAB="$(copy_with_version "$PROJECT_ROOT/androidApp/build/outputs/bundle/release" aab)"
APK="$(copy_with_version "$PROJECT_ROOT/androidApp/build/outputs/apk/release" apk)"

echo "Versioned artifacts:"
echo "- AAB: $AAB"
echo "- APK: $APK"
