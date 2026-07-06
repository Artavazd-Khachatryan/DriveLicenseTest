#!/usr/bin/env bash
# Prints the resolved app version (X.Y.Z.build) without building.
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROPS_FILE="$PROJECT_ROOT/gradle/version.properties"

major=$(grep '^APP_VERSION_MAJOR=' "$PROPS_FILE" | cut -d= -f2)
minor=$(grep '^APP_VERSION_MINOR=' "$PROPS_FILE" | cut -d= -f2)
patch=$(grep '^APP_VERSION_PATCH=' "$PROPS_FILE" | cut -d= -f2)

if [[ -n "${CI_BUILD_NUMBER:-}" ]]; then
  build_number="$CI_BUILD_NUMBER"
else
  build_number="$(git -C "$PROJECT_ROOT" rev-list --count HEAD)"
fi

echo "${major}.${minor}.${patch}.${build_number}"
