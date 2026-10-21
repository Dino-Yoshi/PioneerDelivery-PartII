#!/usr/bin/env bash
# Builds the jar (tests skipped, so a failing case shows as FAIL in the menu) and starts the menu.
set -e
cd "$(dirname "$0")"
./mvnw -q -DskipTests package
exec java -jar target/pioneer-delivery-part2.jar
