# Comprehensible Input

Children learn language by listening to comprehensible input. They don't learn by memorizing
vocabulary lists or grammar rules. They don't learn by doing grammar drills or by taking
grammar tests. They learn by listening to comprehensible input.

The goal of this app is to provide sources of comprehensible input for language learning.

## Internal testing releases

Merges into `main` automatically trigger a GitHub Actions workflow that builds and publishes
an internal testing release to Google Play using fastlane. The lane increments the version code
based on the most recent build already published to the internal track, sets the version name to
`YEAR.DAY_OF_YEAR.RELEASE_INDEX`, and uploads the release bundle.

Examples:

- The first release on January 1st 2025 becomes `2025.0.0`.
- The third release on February 10th 2026 becomes `2026.40.2`.

### Required secrets and variables

Configure these values in the repository settings so the deployment workflow can sign and upload
builds:

- `SUPPLY_JSON_KEY_DATA`: the base64-safe JSON credentials for the Google Play service account
  authorized for internal testing uploads.
- `ANDROID_KEYSTORE_BASE64`: base64-encoded Android signing keystore (optional; omit to use the
  debug keystore). When provided, also set:
  - `ANDROID_KEYSTORE_PASSWORD`
  - `ANDROID_KEY_PASSWORD`
  - `ANDROID_KEY_ALIAS`
- Repository variable `GOOGLE_PLAY_PACKAGE_NAME`: overrides the default package name
  (`in.comprehensible`) if you publish under a different application ID.

### Running the deployment lane locally

1. Install dependencies with `bundle install` (once) and ensure the Android SDK is available using
   `./scripts/setup-android-sdk.sh`.
2. Export the same credentials used in CI:

   ```bash
   export SUPPLY_JSON_KEY_DATA="$(cat path/to/your-service-account.json)"
   export GOOGLE_PLAY_PACKAGE_NAME=in.comprehensible
   export GOOGLE_PLAY_TRACK=internal
   ```

3. Run the lane:

   ```bash
   bundle exec fastlane android internal_release
   ```
