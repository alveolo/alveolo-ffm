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
   and tags, or prepare the version commits and release tag manually. Publication
   itself does not push Git changes.

## Operations

The manual `Release` workflow has three operations:

| Operation | Inputs | Effect |
| --- | --- | --- |
| `dry-run` (default) | `release_version` | Builds a signed release bundle from the selected ref without pushing or publishing. |
| `prepare` | `release_version`, `next_snapshot_version` | Validates the release build, then pushes the release tag and next development version together. Run from `main`. |
| `publish` | `release_version` | Checks out the existing `v<release_version>` tag and publishes it. Run the workflow from `main`; the build uses the tag, not the current branch contents. |

`next_snapshot_version` is only used by `prepare`. For example, prepare
`0.1.0` with next version `0.1.1-SNAPSHOT`, then publish `0.1.0` in a separate run.

## Dry Run

Choose `operation: dry-run` and a candidate `release_version`, such as `0.1.0`.
The workflow updates the Maven version inside its temporary checkout and runs:

```sh
mvn -B -ntp -Pcentral-release -Dcentral.skipPublishing=true deploy
```

This uses the configured GPG key and checks tests, source jars, Javadoc jars,
and the signed Central bundle path without uploading. Preparation performs this
same build before creating release commits.

## Prepare

Run from `main` with `operation: prepare`, `release_version: 0.1.0`, and
`next_snapshot_version: 0.1.1-SNAPSHOT`. The workflow:

1. rejects an existing `v0.1.0` tag;
2. sets the reactor to `0.1.0` and builds the signed bundle without publishing;
3. commits the release POMs and creates the annotated `v0.1.0` tag;
4. sets the reactor to `0.1.1-SNAPSHOT` and commits that development version;
5. atomically pushes the commits to `main` and the release tag to GitHub.

The atomic push updates both refs or neither. If `main` advances during the
build or a repository rule rejects the push, no release tag is pushed by that
attempt. Nothing has been published to Central, so resolve the push issue and
start a fresh preparation run from the updated `main`.

Preparation finishes with `main` ready for continued development. The release
tag remains on the release version, independently of when publication succeeds.

## Publish

Run from `main` with `operation: publish` and `release_version: 0.1.0`.
The workflow checks out `v0.1.0` in detached HEAD mode, checks that its reactor
POMs use `0.1.0`, and runs:

```sh
mvn -B -ntp -Pcentral-release deploy
```

This builds, uploads, validates, publishes, and waits for Central publication.
It does not change versions, create tags, or push commits. The same operation
can therefore be used to retry publication from the prepared source revision.

## Recovering a Failed Run

- **Preparation failed:** check whether the release tag exists on GitHub. If it
  does not, fix the reported issue and start a fresh `prepare` run. If it does,
  check the tag and `main` before proceeding; the push may have succeeded even
  if the runner did not receive its response. Do not recreate or move the tag.
- **Publication failed or timed out:** first inspect the deployment in the
  [Central Portal](https://central.sonatype.com/publishing/deployments), using
  the deployment ID from the Maven log when available. A failed workflow does
  not prove that publication failed.
- **Central says published:** publication is complete. Do not upload that
  version again, even if GitHub reports a failed run.
- **Central is still processing:** wait for a terminal result. If a validated
  deployment is awaiting publication, complete that deployment in the Portal.
- **Central confirms failure, or no upload occurred:** resolve the cause and
  drop any failed deployment if necessary, then run `publish` again for the
  same tag. If the cause requires changing released source or POMs, prepare a
  new version instead of moving the existing tag.

Maven Central artifacts are [immutable after publication](https://central.sonatype.org/faq/can-i-change-a-component/).
Retries reuse the source tag; they do not overwrite published artifacts.
No automated retry or rollback is attempted across GitHub and Central.

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
