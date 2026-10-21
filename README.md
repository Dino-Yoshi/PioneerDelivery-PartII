# Pioneer Delivery — Part II: Divide and Conquer

CS 413 Part II of the Pioneer Delivery Algorithm Portfolio. Three divide-and-conquer algorithms, each called through a documented Java method and checked by a test suite that runs from a menu:

| Task | Question | Required algorithm | Status |
| --- | --- | --- | --- |
| A | Which two delivery locations are closest? | Divide-and-conquer closest pair, `O(n log n)` | Implemented, suite passing |
| B | How many dispatch pairs are out of planned order? | Merge-sort inversion count, `O(n log n)` | Implemented, suite passing |
| C | What is the kth-smallest delivery time? | Median-of-medians selection, `O(n)` | Implemented, suite passing |
| D | Validation and algorithm analysis | PDF report, `reports/TaskD.pdf` | Written |

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

Choose `1` to run every task's tests, or `2`–`4` to run one task on its own. `0` or Ctrl+D exits. Any other input prints `Unknown choice` and shows the menu again. The menu runs the fixture files in `tests/`; it does not run tasks on user-supplied data.

Each case prints as `[PASS]` or `[FAIL]` with its expected and actual result, followed by a total per task. The output of a full run is recorded in `tests/results/suite-output.txt` in the submission.

## Input and output

Each task is one Java method. These methods are the input interface: each copies its input, never modifies the caller's data, and throws `IllegalArgumentException` at the first invalid input.

```java
ClosestPair ClosestPairFinder.findClosestPair(List<Location> locations)         // edu.pioneer.closestpair
long        InversionCounter.countInversions(List<String> planned, List<String> actual)  // edu.pioneer.inversions
int         KthSmallestSelector.selectKthSmallest(int[] times, int k)           // edu.pioneer.selection
```

### Task A: closest pair

- **Input:** at least two `Location(String id, int x, int y)` records with distinct IDs and distinct coordinates, each coordinate within ±1,000,000,000.
- **Output:** `ClosestPair(String firstId, String secondId, long squaredDistance)`. The distance is squared, so it stays an exact integer. On a tie, any closest pair is correct.
- **Errors:** `at least two locations`, `duplicate ID`, `duplicate coordinates`, `out of range`.

### Task B: dispatch-order inversions

- **Input:** the planned and actual orders as lists of the same IDs, each non-empty, with no repeats.
- **Output:** the number of pairs delivered in the opposite order to the plan, as a `long`. A fully reversed list of 100,000 IDs gives 4,999,950,000, which does not fit in an `int`.
- **How:** each ID in `actual` is replaced by its position in `planned`, and the inversions in that number list are counted during a merge sort.
- **Errors:** `at least one`, `different lengths`, `duplicate ID`, `not in planned`.

### Task C: kth-smallest delivery time

- **Input:** a non-empty array of non-negative times and `k` from 1 to the array length (1 = smallest).
- **Output:** the kth smallest time. Duplicates count separately: in `[4, 4, 9]`, the 1st and 2nd smallest are both 4.
- **Errors:** `at least one`, `negative`, `out of range`.

**How `k` is adjusted.** Each round picks a median-of-medians pivot and splits the values into smaller, same, and larger than it, with sizes `s` and `e` for the first two. `k` is a rank inside the current group, so it changes when values below it are thrown away:

- `k ≤ s`: keep **smaller**. Nothing below it was discarded, so `k` stays the same.
- `s < k ≤ s + e`: the answer is the pivot.
- `k > s + e`: keep **larger**. The `s + e` values below it were discarded, so the new `k` is `k − s − e`.

Example: for `[18, 4, 29, 11, 4, 16, 35, 9, 22]` and `k = 5`, the pivot is 11, with `s = 3` and `e = 1`. Since 5 > 4, the search continues in `[18, 29, 16, 35, 22]` with `k = 5 − 3 − 1 = 1`, and the answer is 16.

### Fixture files

Each test case has `tests/inputs/<ID>.csv` and `tests/expected/<ID>.csv`, for example `tests/inputs/A3.csv`. Non-integer numbers in an input file are rejected with `not an integer`.

Input files:

```text
Task A: header, then one location per row     Task B: two labeled rows     Task C: two labeled rows
id,x,y                                        planned,A,B,C,D,E            k,5
A,0,0                                         actual,A,C,D,B,E             times,18,4,29,11,4,16,35,9,22
B,3,4
```

Expected files:

```text
Task A                  Task B           Task C        Invalid input (any task)
squared_distance,25     inversions,2     value,16      error,duplicate coordinates
pair,A,B
```

- **Task A** passes when the squared distance matches and the returned pair, in either order, is one of the listed `pair` rows. A case with ties lists every closest pair.
- **Error cases** pass when an `IllegalArgumentException` is thrown whose message contains the given words.

## Tests

```bash
./mvnw test          # Windows: mvnw.cmd test
```

This runs the same cases as the menu (`SuiteTest`, one test per case) plus unit tests for each task, the fixture reader, the case checker, and the menu. The suite covers:

- the required cases from the assignment (`A1`–`A4`, `B1`–`B4`, `C1`–`C5`);
- extra edge cases, such as tied distances, shared x-coordinates, ±1,000,000,000 coordinates, a 100,000-ID reversed order, and 100,000 heavily duplicated values;
- every invalid-input condition listed above;
- one random reference check per task (`A-ref`, `B-ref`, `C-ref`): 500 small inputs from the fixed seed 413, each compared with a simple method (all-pairs brute force, two nested loops, and a full sort). These simple methods are for testing only.

## Benchmark

```bash
./mvnw -q -DskipTests package
java -cp target/classes edu.pioneer.Benchmark
```

Prints one table per task (size, median milliseconds, ratio to the previous size) for sizes 100 to 100,000, using inputs from the fixed seed 413. It is not part of the tests. The results and their analysis are in `reports/TaskD.pdf`.

## Development environment

- **Language:** Java, compiled with `--release 25`. JDK 25 or newer is required.
- **Build:** Maven 3.9.11 through the included Maven Wrapper (`./mvnw`); a separate Maven install is not needed.
- **Tests:** JUnit 5 (5.11.4) through Maven Surefire.
- **Runtime dependencies:** none.

Check the JDK with:

```bash
java -version
```

To build `target/pioneer-delivery-part2.jar` with tests:

```bash
./mvnw package       # Windows: mvnw.cmd package
```

## Project structure

```text
.
├── pom.xml                  Maven build (JDK 25 release, JUnit 5, executable jar)
├── mvnw, mvnw.cmd, .mvn/    Maven Wrapper
├── run.sh, run.cmd          Build and start the menu
├── src/main/java/edu/pioneer/
│   ├── App.java             Looping menu (entry point)
│   ├── Benchmark.java       Task D timings (separate entry point)
│   ├── closestpair/         Task A
│   ├── inversions/          Task B
│   ├── selection/           Task C
│   └── suite/               Test suite: case list, fixture reader, runner, reference checks, printer
├── src/test/java/edu/pioneer/   JUnit tests, including SuiteTest
├── tests/inputs/            Input fixture for each case
├── tests/expected/          Expected result for each case
└── reports/TaskD.pdf        Task D report
```

In the repository only, not in the submission ZIP: `docs/` (SRS, SDS, Task D source and walkthroughs) and `package-submission.sh`, which builds and checks the ZIP.
