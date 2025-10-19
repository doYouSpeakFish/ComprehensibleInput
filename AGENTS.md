# ComprehensibleInput Agent Notes

## Android SDK setup
- Run `./scripts/setup-android-sdk.sh` before building or running tests on a new machine.
  - The script downloads the Android command line tools, installs the required SDK components (platform 36, build tools 35.0.0, and platform tools), and fetches the Robolectric `android-all-instrumented:14-robolectric-10818077-i7` dependency so tests can run offline.
  - The script writes `local.properties` with the detected SDK path if the file does not already exist. The file is ignored by Git; remove it before committing if you prefer to keep your workspace clean.
  - After running the script, export the SDK location for the current shell if Gradle cannot locate it automatically:
    ```bash
    export ANDROID_SDK_ROOT="$HOME/android-sdk"
    export PATH="$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$PATH"
    ```

## Testing
- Invoke `./gradlew :app:testDebugUnitTest --tests "input.comprehensible.*" --console=plain` to execute the integration test suite used in CI. Tests should be written so non-programmers can read them comfortably: prefer descriptive test names, use `GIVEN/WHEN/THEN` comments, and avoid referencing internal implementation details such as view models or data sources.
- Run `./gradlew detekt` before sending changes to ensure the codebase passes static analysis.
- Run `./gradlew koverXmlReport --console=plain` to verify that the coverage report stays healthy.
- New production code must either reach 100% line coverage or be explicitly excluded from coverage **only** when it represents a boundary that is faked in tests (for example, a platform API wrapper). Any excluded code that contains meaningful logic must have that logic extracted into a class that is, or becomes, unit tested.
- Unit tests are only acceptable for the extracted logic mentioned above; all other behavior should be covered through user-facing integration tests.
