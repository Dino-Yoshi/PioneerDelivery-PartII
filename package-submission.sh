#!/usr/bin/env bash
# Builds the Canvas submission ZIP and checks it (SDS 10, SRS 9). Local tool; not part of the ZIP.
set -euo pipefail
cd "$(dirname "$0")"

ZIP=submission/pioneer-delivery-part2.zip

# 1. Clean build; this also runs the tests.
./mvnw -q clean package

# 2. The Task D report must exist.
if [ ! -f reports/TaskD.pdf ]; then
    echo "ERROR: reports/TaskD.pdf is missing. Export the Task D report there and run again." >&2
    exit 1
fi

# 3. Recreate submission/raw/.
rm -rf submission/raw "$ZIP"
mkdir -p submission/raw/reports

# 4. Copy in the listed contents.
cp -r pom.xml mvnw mvnw.cmd .mvn run.sh run.cmd src tests README.md submission/raw/
cp reports/TaskD.pdf submission/raw/reports/

# 5. Record the suite output.
mkdir -p submission/raw/tests/results
printf '1\n0\n' | java -jar target/pioneer-delivery-part2.jar > submission/raw/tests/results/suite-output.txt

# 6. Create the ZIP from raw/.
jar --create --no-manifest --file "$ZIP" -C submission/raw .

# 7. Extract to a temporary folder, compare the file lists, and run the tests there.
TMP=$(mktemp -d)
trap 'rm -rf "$TMP"' EXIT
jar --extract --file "$PWD/$ZIP" --dir "$TMP"
if ! diff <(cd submission/raw && find . -type f | sort) <(cd "$TMP" && find . -type f | sort); then
    echo "ERROR: extracted ZIP does not match submission/raw/." >&2
    exit 1
fi
# jar does not store permissions, so restore the wrapper's executable bit as unzip users would.
chmod +x "$TMP/mvnw" "$TMP/run.sh"
(cd "$TMP" && ./mvnw -q test)

echo "OK: $ZIP built and verified."
