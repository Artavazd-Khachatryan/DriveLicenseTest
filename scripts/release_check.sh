#!/usr/bin/env bash
# Pre-release regression suite. Stops on the first failure.
#
# Usage: scripts/release_check.sh [--skip-content] [--no-device] [--ios]
#   --skip-content  skip the PDF-based content checks (steps 1 and 2)
#   --no-device     never run the instrumented tests, even if a device is attached
#   --ios           also run the iOS simulator unit tests
#
# Steps 1-2 need the official exam PDFs, cached in /tmp/official-exam-2026
# (override with OFFICIAL_EXAM_CACHE). The fetch from roadpolice.am can time
# out; pre-download them instead:
#   for p in abc dt; do mkdir -p /tmp/official-exam-2026/$p; for n in 1 2 3 4 5 6 7 8 9 10; do
#     curl -fL --max-time 120 -o /tmp/official-exam-2026/$p/$n.pdf https://roadpolice.am/exam/$p/hy/$n.pdf
#   done; done
set -euo pipefail

cd "$(dirname "$0")/.."

SKIP_CONTENT=0
NO_DEVICE=0
IOS=0
for arg in "$@"; do
  case "$arg" in
    --skip-content) SKIP_CONTENT=1 ;;
    --no-device) NO_DEVICE=1 ;;
    --ios) IOS=1 ;;
    -h|--help) sed -n 2,18p "$0"; exit 0 ;;
    *) echo "Unknown option: $arg" >&2; exit 2 ;;
  esac
done

DB="database/src/commonMain/resources/license_test_questions.db"
REFRESH="database/src/commonMain/kotlin/com/drive/license/test/database/ContentRefresh.kt"
SUMMARY=()

step() { echo; echo "==> $*"; }
done_step() { SUMMARY+=("PASS  $1"); }
skip_step() { SUMMARY+=("SKIP  $1"); }

if [ "$SKIP_CONTENT" -eq 0 ]; then
  step "1/6 verify_image_refs.py"
  python3 scripts/verify_image_refs.py
  done_step "image references"

  step "2/6 verify_questions.py (official PDFs)"
  python3 scripts/verify_questions.py
  done_step "questions vs official PDFs"
else
  skip_step "image references (--skip-content)"
  skip_step "questions vs official PDFs (--skip-content)"
fi

step "3/6 content_version stamp"
code_version=$(grep -E 'CONTENT_VERSION *= *[0-9]+' "$REFRESH" | sed -E 's/.*= *([0-9]+).*/\1/' | head -1)
db_version=$(sqlite3 "$DB" "select value from Metadata where key='content_version'")
echo "ContentRefresh.CONTENT_VERSION=$code_version, bundled DB content_version=$db_version"
if [ -z "$code_version" ] || [ "$code_version" != "$db_version" ]; then
  echo "FAIL: content_version mismatch" >&2
  exit 1
fi
done_step "content_version $code_version matches bundled DB"

step "4/6 unit/host tests"
GRADLE_TESTS=(
  :database:testAndroidHostTest
  :domain:testAndroidHostTest
  :ui:testAndroidHostTest
  :composeApp:testAndroidHostTest
  :androidApp:testDebugUnitTest
)
if [ "$IOS" -eq 1 ]; then
  GRADLE_TESTS+=(:domain:iosSimulatorArm64Test :ui:iosSimulatorArm64Test :composeApp:iosSimulatorArm64Test)
fi
./gradlew "${GRADLE_TESTS[@]}"
done_step "unit/host tests (${GRADLE_TESTS[*]})"

step "5/6 assembleDebug"
./gradlew :androidApp:assembleDebug
done_step "assembleDebug"

step "6/6 instrumented smoke tests"
if [ "$NO_DEVICE" -eq 1 ]; then
  skip_step "instrumented tests (--no-device)"
elif adb devices 2>/dev/null | awk 'NR>1 && $2=="device"' | grep -q .; then
  ./gradlew :androidApp:connectedDebugAndroidTest
  done_step "instrumented tests (connectedDebugAndroidTest)"
else
  echo "No device attached, skipping."
  skip_step "instrumented tests (no device)"
fi

echo
echo "================ release_check summary ================"
printf '%s\n' "${SUMMARY[@]}"
echo "PASS: release check complete"
