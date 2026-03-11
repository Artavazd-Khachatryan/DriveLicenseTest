# Reference: KMP GitHub CI

## Discover iOS schemes

```bash
xcodebuild -list -project iosApp/iosApp.xcodeproj
```

If you have a workspace:

```bash
xcodebuild -list -workspace iosApp/iosApp.xcworkspace
```

## Discover Gradle tasks (KMP)

```bash
./gradlew tasks --all
./gradlew :composeApp:tasks --all
```

Common iOS framework tasks:
- `:composeApp:linkDebugFrameworkIosSimulatorArm64`
- `:composeApp:linkDebugFrameworkIosX64`
- `:composeApp:linkDebugFrameworkIosArm64`

## Example xcodebuild invocation

```bash
xcodebuild \
  -project iosApp/iosApp.xcodeproj \
  -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 15' \
  clean build test
```

## Notes

- Prefer building the KMP framework via Gradle first so Xcode has generated artifacts ready.
- If you hit code signing issues, ensure CI builds for simulator and does not attempt device signing.

