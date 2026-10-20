# Pioneer Delivery — Part II: Divide and Conquer

CS 413 Part II of the Pioneer Delivery Algorithm Portfolio. It will provide three independently runnable command-line algorithms:

| Task | Question | Required algorithm | Status |
| --- | --- | --- | --- |
| A | Which two delivery locations are closest? | Divide-and-conquer closest pair, `O(n log n)` | Not implemented |
| B | How many dispatch pairs are out of planned order? | Merge-sort inversion count, `O(n log n)` | Not implemented |
| C | What is the kth-smallest delivery time? | Median-of-medians selection, `O(n)` | Not implemented |
| D | Validation and algorithm analysis | 1–2 page PDF report | Not written |

Requirements are in [docs/SRS-Pioneer-Delivery-PartII-v0.2.md](docs/SRS-Pioneer-Delivery-PartII-v0.2.md), which is a draft under review.

## Current state

The repository contains only build and CLI scaffolding. No algorithm is implemented, and no task produces a result. Each task command prints `not implemented yet` and exits with status 1.

The command names (`task-a`, `task-b`, `task-c`) and input formats shown below are proposals and have not been accepted.

## Development environment

- **Language:** Java, compiled with `--release 25`. JDK 25 or newer is required.
- **Build:** Maven 3.9.11 through the included Maven Wrapper (`./mvnw`); a separate Maven install is not needed.
- **Tests:** JUnit 5 (5.11.4) through Maven Surefire.
- **Runtime dependencies:** none.

Check the JDK with:

```bash
java -version
```

## Build and test

```bash
# compile and run the tests
./mvnw test

# compile, test, and build target/pioneer-delivery-part2.jar
./mvnw package
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

The current tests (`src/test/java/edu/pioneer/AppTest.java`) check only the placeholder CLI: usage output, the not-implemented message for each task command, and the unknown-command error.

## Run

```bash
java -jar target/pioneer-delivery-part2.jar          # usage (exit 0)
java -jar target/pioneer-delivery-part2.jar task-a   # "not implemented yet" (exit 1)
```

Proposed final usage (not implemented):

```text
java -jar target/pioneer-delivery-part2.jar task-a <locations.csv>
java -jar target/pioneer-delivery-part2.jar task-b <dispatch.csv>
java -jar target/pioneer-delivery-part2.jar task-c <times.csv> <k>
```

## Project structure

```text
.
├── pom.xml                 Maven build (JDK 25 release, JUnit 5, executable jar)
├── mvnw, mvnw.cmd, .mvn/   Maven Wrapper
├── docs/                   SRS (and later the SDS)
├── src/main/java/edu/pioneer/App.java      CLI entry point (placeholder)
├── src/test/java/edu/pioneer/AppTest.java  CLI scaffolding tests
├── tests/inputs/           Required and additional test inputs (empty; format pending)
├── tests/expected/         Expected outputs for those inputs (empty; format pending)
└── reports/                Task D report PDF (not written)
```

## Not yet implemented

- Tasks A, B, and C algorithms and their CSV input readers.
- Required and additional test fixtures and algorithm tests.
- Reference-method (brute force, nested loops, sorting) checks.
- Task D timing measurements and PDF report.
- Final input/output format documentation.
