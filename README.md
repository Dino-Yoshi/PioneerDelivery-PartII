# Pioneer Delivery — Part II: Divide and Conquer

CS 413 Part II of the Pioneer Delivery Algorithm Portfolio. It will provide three independently runnable command-line algorithms:

| Task | Question | Required algorithm | Status |
| --- | --- | --- | --- |
| A | Which two delivery locations are closest? | Divide-and-conquer closest pair, `O(n log n)` | Not implemented |
| B | How many dispatch pairs are out of planned order? | Merge-sort inversion count, `O(n log n)` | Implemented |
| C | What is the kth-smallest delivery time? | Median-of-medians selection, `O(n)` | Implemented |
| D | Validation and algorithm analysis | 1–2 page PDF report | Not written |

Requirements are in [docs/SRS-Pioneer-Delivery-PartII-v0.3.md](docs/SRS-Pioneer-Delivery-PartII-v0.3.md), the accepted requirements baseline.

## Run

```bash
./run.sh        # Linux/macOS
run.cmd         # Windows
```

The script builds the jar (tests skipped) and starts a looping menu. The first run needs network access, because the Maven Wrapper downloads Maven and dependencies.

```text
Pioneer Delivery Part II — Test Suite
  1  Run all tests
  2  Task A: Closest Delivery Locations
  3  Task B: Counting Dispatch-Order Inversions
  4  Task C: Selecting a Delivery-Time Threshold
  0  Exit
```

Choose `1`–`4` to run tests and see the results, `0` or Ctrl+D to exit. Any other input prints `Unknown choice` and shows the menu again. The menu does not run tasks on user-supplied data.

## Current state

The menu, fixture reader and suite runner are in place with no test cases yet, so every option reports `0 passed, 0 failed`. No algorithm is implemented.

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

`./mvnw test` runs the same cases as the menu (`SuiteTest`, one test per case) plus unit tests for the fixture reader, the case checker and the menu.

## Project structure

```text
.
├── pom.xml                 Maven build (JDK 25 release, JUnit 5, executable jar)
├── mvnw, mvnw.cmd, .mvn/   Maven Wrapper
├── docs/                   SRS (and later the SDS)
├── run.sh, run.cmd        Build and start the menu
├── src/main/java/edu/pioneer/App.java      Looping menu (entry point)
├── src/main/java/edu/pioneer/suite/        Shared test suite (runner, fixture reader, printer)
├── src/test/java/edu/pioneer/              Menu, fixture reader, runner tests and SuiteTest
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
