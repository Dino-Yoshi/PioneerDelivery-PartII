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

# 6. Create the ZIP from raw/. Python's zipfile stores Unix permissions (jar does not),
#    so run.sh and mvnw stay executable after unzip.
python3 -I - submission/raw "$ZIP" <<'PY'
import os, sys, zipfile
raw, out = sys.argv[1], sys.argv[2]
with zipfile.ZipFile(out, "w", zipfile.ZIP_DEFLATED) as z:
    for root, dirs, files in os.walk(raw):
        dirs.sort()
        for name in sorted(files):
            path = os.path.join(root, name)
            z.write(path, os.path.relpath(path, raw))
PY

# 7. Extract with unzip, as a grader would, compare the file lists, and run the tests there.
TMP=$(mktemp -d)
trap 'rm -rf "$TMP"' EXIT
unzip -q "$ZIP" -d "$TMP"
if ! diff <(cd submission/raw && find . -type f | sort) <(cd "$TMP" && find . -type f | sort); then
    echo "ERROR: extracted ZIP does not match submission/raw/." >&2
    exit 1
fi
for script in run.sh mvnw; do
    if [ ! -x "$TMP/$script" ]; then
        echo "ERROR: $script is not executable after unzip." >&2
        exit 1
    fi
done
(cd "$TMP" && ./mvnw -q test)

echo "OK: $ZIP built and verified."
