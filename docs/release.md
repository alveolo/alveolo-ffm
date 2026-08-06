# Release Process

Alveolo FFM publishes through the Sonatype Central Portal using the
`central-publishing-maven-plugin`. The release profile builds the artifacts
required by Maven Central: the main jars, source jars, Javadoc jars, POMs,
checksums, and GPG signatures.

## One-Time Setup

1. Create or use a Central Portal account at <https://central.sonatype.com/>.
2. Verify a Central namespace that allows the current group id,
   `org.alveolo.ffm`. If the project will not control the `alveolo.org`
   domain, change the group id before the first release.
3. Generate a Central Portal user token and store it as GitHub repository
   secrets:
   - `CENTRAL_USERNAME`: the token username.
   - `CENTRAL_PASSWORD`: the token password.
4. Create a GPG key for release signing, publish its public key, and store the
   private key material as GitHub repository secrets:
   - `MAVEN_GPG_PRIVATE_KEY`: output of
     `gpg --armor --export-secret-keys <key-id>`.
   - `MAVEN_GPG_PASSPHRASE`: the private key passphrase.
5. If `main` is protected, allow the release workflow to push release commits
   and tags, or perform the version bumps manually and use the workflow only as
   a dry-run/publish executor.

## Dry Run From GitHub Actions

Run the `Release` workflow manually with:

- `release_version`: the candidate release, for example `0.1.0`.
- `next_snapshot_version`: the next development version, for example
  `0.1.1-SNAPSHOT`.
- `publish`: `false`.

This updates the Maven version inside the temporary workflow checkout and runs:

```sh
mvn -B -ntp -Pcentral-release -Dcentral.skipPublishing=true deploy
```

The dry run uses the configured GPG key, verifies tests, source jars, Javadoc
jars, and the signed Central bundle path without uploading to Maven Central.

## Publish From GitHub Actions

Run the `Release` workflow manually from `main` with:

- `release_version`: the Maven Central version, without `-SNAPSHOT`.
- `next_snapshot_version`: the next development version, ending in `-SNAPSHOT`.
- `publish`: `true`.

The workflow:

1. sets the Maven reactor to `release_version`;
2. locally commits the release POMs and tags them as `v<release_version>`;
3. runs `mvn -B -ntp -Pcentral-release deploy`;
4. uploads, validates, publishes, and waits for Maven Central publication;
5. bumps the reactor to `next_snapshot_version`;
6. pushes the release commit, snapshot commit, and release tag to GitHub.

Maven Central artifacts are immutable after publication, so use the dry run for
format and Javadoc checks before publishing a new version.

## Local Release Check

For a local packaging check, use a throwaway branch and run:

```sh
mvn -B -ntp versions:set -DnewVersion=0.1.0 \
  -DprocessAllModules=true -DgenerateBackupPoms=false
mvn -B -ntp -Pcentral-release -Dgpg.skip=true verify
```

This confirms compilation, tests, source jars, and Javadoc jars. For a signed
local check, omit `-Dgpg.skip=true` and make sure `gpg` can sign
non-interactively with the same key used in GitHub Actions. To also exercise
the Central deploy integration without uploading, use `deploy` with
`-Dcentral.skipPublishing=true` and a `central` server entry in Maven settings.
