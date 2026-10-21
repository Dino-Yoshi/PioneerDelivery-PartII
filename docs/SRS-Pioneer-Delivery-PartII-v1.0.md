# Pioneer Delivery — Software Requirements Specification, Part II

- **Project:** Pioneer Delivery Company's Algorithm Portfolio — Part II: Divide and Conquer

- **Course:** CS 413 — Analysis of Algorithms

- **Version:** 1.0

- **Date:** October 21, 2026

- **Status:** Final, as submitted

- **Requirements authority:** `CS413_PA-part2-2026Fall.pdf`

- **Supersedes:** SRS Part II v0.2

## Revision History

| Version | Date | Changes |
| - | - | - |
| 0.1 | October 19, 2026 | First draft from the assignment PDF and rough notes. AI-assistance question recorded as unresolved. Later the same day, updated with the instructor's answer: AI assistance and built-in data structures are allowed. |
| 0.2 | October 19, 2026 | Task D wording simplified, and a plain-language writing-style requirement added (7.1). New team CLI and test-suite requirements (3.4): one-command start; a looping menu with suite, Task A, B, and C test runs and an exit option; descriptive PASS/FAIL output; tests also runnable through Maven; no user-supplied datasets. Input defined as a documented Java method plus CSV fixtures (3.2). Submission contents listed literally, plus the untracked `submission/raw/` and ZIP layout (9). Deferred scope updated (10). Revision history and table of contents added. Accepted as the team baseline. |
| 0.3 | October 19, 2026 | Task A coordinate limit of ±1,000,000,000 added (4.1), with the reason: the assignment gives no range, and some limit is unavoidable because the squared distance must fit in a fixed-size Java `long`. The limit is added to Task A's invalid-input list. October 20: Task D sources added (7): links from the report's Design and Complexity sections to the SDS breakdowns and the figures. |
| 1.0 | October 21, 2026 | Final version for submission. No requirement changes since 0.3; links updated to the final SDS, and the file renamed from `-v0.3.md`. |


## Table of Contents

- [1. Purpose and Scope](#1-purpose-and-scope)

- [2. Accepted Implementation Baseline](#2-accepted-implementation-baseline)

- [3. General Requirements](#3-general-requirements)

  - [3.1 Instructor Requirements](#31-instructor-requirements)

  - [3.2 Input Handling](#32-input-handling)

  - [3.3 Resolved Clarification: "implement the algorithms yourself"](#33-resolved-clarification-implement-the-algorithms-yourself)

  - [3.4 CLI and Test-suite requirements (team)](#34-cli-and-test-suite-requirements-team)

- [4. Task A Requirements — Closest Delivery Locations](#4-task-a-requirements--closest-delivery-locations)

- [5. Task B Requirements — Counting Dispatch-order Inversions](#5-task-b-requirements--counting-dispatch-order-inversions)

- [6. Task C Requirements — Selecting a Delivery-time Threshold](#6-task-c-requirements--selecting-a-delivery-time-threshold)

- [7. Task D Requirements — Validation and Algorithm Analysis](#7-task-d-requirements--validation-and-algorithm-analysis)

- [8. Verification Requirements](#8-verification-requirements)

- [9. Submission Requirements](#9-submission-requirements)

- [10. Deferred Scope](#10-deferred-scope)

## 1. Purpose and Scope

Pioneer Delivery wants three operational questions answered with divide-and-conquer algorithms:

- **Task A:** find the two delivery locations closest to each other.

- **Task B:** count how many pairs of deliveries were dispatched in the opposite order from the plan.

- **Task C:** find the kth-smallest delivery-time estimate without sorting the full dataset.

Task D is a separate 1–2 page PDF report covering design, complexity, correctness testing, and performance evidence for Tasks A–C. It is not a fourth algorithm.

Part II is independent of Part I. It does not reuse Part I's scheduling problem, data model, GUI, or input-validation behavior.

## 2. Accepted Implementation Baseline

| Decision | Accepted choice |
| - | - |
| Language/runtime | JDK 25 |
| Build | Maven |
| Interface | Command-line only; no JavaFX or other GUI |
| Test data | CSV fixture files |
| Task independence | Tasks A, B, and C each run and test independently |
| Version control | Git; separate local Part II project and repository |
| Inversion count type | Java `long` |
| AI assistance | Permitted by the instructor (section 3.3) |


These are team decisions, not additional instructor requirements unless explicitly stated below. Exact CSV schemas and CLI syntax are not yet accepted; see section 10.

## 3. General Requirements

### 3.1 Instructor Requirements

- Each of Tasks A–C shall be executable and testable independently.

- Each core algorithm shall be implemented in project code. Built-in data structures may be used. Library sorting may be used only where a task permits it. A library routine shall not replace a task's core algorithm.

- Each task shall handle the input assumptions stated in its section.

- Invalid input shall produce a clear error message or a documented exception. Invalid input includes at least the conditions listed under each task.

- Input and output formats shall be documented in the README.

- Each task shall include the required test cases and at least one additional test of the team's own.

### 3.2 Input Handling

The assignment allows input through files, command-line arguments, or a documented program interface. Part II uses:

- a documented Java method for each task as its input interface;

- CSV fixture files as the stored test inputs.

The CLI does not accept user-supplied datasets (section 3.4.3).

- Each task reads its own input. There is no shared dataset across tasks.

- When an input violates a task's assumptions, the task shall report an error and shall not report an algorithmic result for that input.

- A fixture file that cannot be opened or read is an error.

Part I's skip-and-log handling of invalid rows, `.csv` extension check, empty-file-as-valid rule, and processing report do not apply to Part II unless they are adopted later as a design decision.

### 3.3 Resolved Clarification: "implement the algorithms yourself"

The team asked the instructor whether this instruction prohibits AI-generated implementations. The instructor answered that:

- AI assistance is allowed for this project.

- Built-in data structures are allowed.

"Implement the algorithms yourself" therefore means each core algorithm is implemented in project code rather than delegated to a library routine. Sorting remains limited to the uses each task permits.

### 3.4 CLI and Test-suite requirements (team)

These requirements come from the team, not the assignment.

#### 3.4.1 Easy Run

- After cloning, a user with only JDK 25 or newer installed shall be able to build and start the CLI with one documented command. A separate Maven installation shall not be required.

- The README shall give that command first, before any other build detail.

- On the first run, the Maven Wrapper may need network access to download Maven and dependencies. The README shall say so.

#### 3.4.2 Menu and Test Suite

- The CLI shall be a looping menu with these options:

  - run the full suite;

  - run Task A's tests only;

  - run Task B's tests only;

  - run Task C's tests only;

  - exit.

- After an option finishes, the CLI shall show the menu again.

- Choosing exit shall end the program normally with status 0. End of input (for example, Ctrl+D) shall also end the program normally.

- An unrecognized choice shall show a short message and the menu again. It shall not end the program.

- The same tests shall also run without the CLI through `./mvnw test`, and both routes shall use the same test cases and fixture files. `./mvnw test` shall fail when any test fails.

- For each test, the output shall show:

  - test ID, such as `A3`;

  - a one-line description of what is being checked;

  - the expected result;

  - the actual result;

  - `PASS` or `FAIL`.

- After each run, the output shall show a summary of passed and failed counts.

- The suite shall include the required tests, the additional tests, the invalid-input checks, and the reference-method checks from section 8.

#### 3.4.3 No User-supplied Datasets

The assignment accepts a documented program interface as the input method, so the CLI does not need to read user-supplied data. The CLI shall not offer options that run a task on a user-supplied file or values. The README shall document each task's Java method and the CSV fixture format.

## 4. Task A Requirements — Closest Delivery Locations

### 4.1 Input and Output

The input is a set of `n >= 2` locations:

| Field | Requirement |
| - | - |
| `id` | Location identifier; unique within one input |
| `x` | Integer from −1,000,000,000 to 1,000,000,000 |
| `y` | Integer from −1,000,000,000 to 1,000,000,000 |


No two locations shall have identical `(x, y)` coordinates.

**Coordinate limit.** The assignment allows any integer coordinate but gives no range. A limit is still necessary, because every number is stored in a fixed amount of memory. The squared distance is stored in a Java `long`, whose largest value is about 9.2 × 10¹⁸. With coordinates within ±10⁹, the largest possible `d²` is (2 × 10⁹)² + (2 × 10⁹)² = 8 × 10¹⁸, which fits. Without a limit, `d²` could exceed the `long` range and silently give a wrong answer.

The result shall show the original IDs of a closest pair and their squared Euclidean distance `d² = (xi − xj)² + (yi − yj)²`. The Euclidean distance may also be shown. When several pairs share the minimum distance, any one of them is acceptable.

Invalid input includes fewer than two locations, duplicate IDs, duplicate coordinate pairs, non-integer coordinates, and coordinates outside ±1,000,000,000.

### 4.2 Algorithm Requirements

- Use the divide-and-conquer closest-pair algorithm: divide the points into two halves by x-coordinate, solve each half recursively, and check for a closer pair crossing the dividing line.

- When x-coordinates are tied at the dividing line, assign points to halves by point identity, not by an x-coordinate threshold alone.

- Maintain y-sorted information through the recursion. Do not re-sort each subproblem by y-coordinate.

- Use brute-force comparison only for small base cases.

- In the combine step, examine only the vertical strip around the dividing line and use the constant-neighbor geometric bound from class.

- Run in `O(n log n)` worst-case time, including initial sorting.

- Return the original location IDs, not only the distance.

### 4.3 Required Tests

| Test | Locations `(id, x, y)` | Expected result |
| - | :-: | - |
| A1 | `A(0, 0), B(3, 4)` | `A, B; d² = 25` |
| A2 | `A(0, 0), B(7, 2), C(4, 5), D(5, 5), E(12, 2), F(2, 9)` | `C, D; d² = 1` |
| A3 | `A(−3, 1), B(−3, 11), C(1, 1), D(2, 15)` | `A, C; d² = 16` |
| A4 | `A(−5, −5), B(−5, −2), C(6, 8)` | `A, B; d² = 9` |


A3 shall be verified specifically, because its closest pair lies in different halves of the recursive division.

Additional required tests:

- a dataset with multiple tied closest pairs, where any minimizing pair is accepted and `d²` equals the minimum;

- at least one more team-defined case, such as several points sharing one x-coordinate across the dividing line.

## 5. Task B Requirements — Counting Dispatch-order Inversions

### 5.1 Input and Output

The input is two sequences, `planned` and `actual`, each containing the same `n >= 1` distinct delivery IDs exactly once.

An inversion is a pair of IDs whose relative order in `actual` is the reverse of their relative order in `planned`.

The result shall show the total number of inversions as a nonnegative integer.

Invalid input includes an empty sequence, sequences of different lengths, a duplicate ID within a sequence, and an ID that appears in only one sequence.

Example: `planned = [D1, D2, D3, D4, D5]` and `actual = [D1, D3, D4, D2, D5]` give the ranks `[1, 3, 4, 2, 5]` and 2 inversions, `(D2, D3)` and `(D2, D4)`.

### 5.2 Algorithm Requirements

- Convert `actual` into an array of planned-order ranks.

- Count inversions with a merge-sort-based algorithm: recursively count inversions in the left and right halves, then count cross-half inversions during the merge.

- Run in `O(n log n)` time. A nested-loop `O(n²)` method shall not be the submitted algorithm.

- Store counts up to `n(n − 1)/2` in a Java `long`.

### 5.3 Required Tests

| Test | Planned order | Actual order | Inversions |
| - | - | - | -: |
| B1 | `[A, B, C, D, E]` | `[A, C, D, B, E]` | 2 |
| B2 | `[A, B, C, D]` | `[A, B, C, D]` | 0 |
| B3 | `[A, B, C, D]` | `[D, C, B, A]` | 6 |
| B4 | `[A]` | `[A]` | 0 |


Large-count check: with `n = 100,000`, `planned = [1, 2, …, n]`, and `actual` reversed, the result shall be exactly `4,999,950,000`. This value exceeds the Java `int` range.

At least one additional team-defined case is required.

## 6. Task C Requirements — Selecting a Delivery-time Threshold

### 6.1 Input and Output

The input is an array `T` of `n >= 1` nonnegative integer delivery-time estimates and an integer rank `k` with `1 <= k <= n`.

The result shall show the kth-smallest value in `T`. Ranks are 1-based: `k = 1` is the minimum and `k = n` is the maximum. Repeated values count as separate positions.

Invalid input includes an empty array, a negative or non-integer time, and `k` outside `1..n`.

### 6.2 Algorithm Requirements

- Use deterministic median-of-medians selection. Divide the values into groups of at most five, sort each small group as needed, and recursively select the median of the group medians as the pivot.

- Partition the current values into three groups: less than, equal to, and greater than the pivot.

- Recurse only into the group containing rank `k`, adjusting `k` for the discarded groups. If `k` falls within the equal group, return the pivot.

- Handle duplicate values correctly.

- Run in `O(n)` worst-case time. Sorting the entire input or using randomized quickselect does not satisfy this task. Sorting small groups and small base cases is permitted.

- Explain the adjustment of `k` after partitioning in the README and Task D report.

### 6.3 Required Tests

| Test | Delivery times `T` | k | Expected value |
| - | :-: | -: | -: |
| C1 | `[18, 4, 29, 11, 4, 16, 35, 9, 22]` | 5 | 16 |
| C2 | `[18, 4, 29, 11, 4, 16, 35, 9, 22]` | 2 | 4 |
| C3 | `[18, 4, 29, 11, 4, 16, 35, 9, 22]` | 9 | 35 |
| C4 | `[7, 7, 7, 7, 7, 7]` | 4 | 7 |
| C5 | `[8, 3]` | 1 | 3 |


Additional required tests:

- already sorted input;

- reverse-sorted input;

- a larger input with many duplicate values.

## 7. Task D Requirements — Validation and Algorithm Analysis

Task D is a PDF report of about 1–2 pages. For each of Tasks A–C it shall cover:

| Section | Required content |
| - | - |
| Design | How the problem is split, how the smaller problems are solved, and how their answers are combined. For Task C, which part is thrown away and why. |
| Complexity | Running time and extra memory, with a short reason for each |
| Correctness testing | How results were checked against a simple method on small inputs, plus at least one extra edge case per task |
| Performance | Rough running times for at least three growing input sizes, such as `n = 100, 1,000, 5,000`, and whether the growth matches the expected complexity |


The complexity section shall also explain:

- why Task A's combine step does not compare every pair of points in the strip;

- why Task C needs a good pivot, and how median-of-medians guarantees one.

Timing results depend on the machine and need not match a fixed benchmark.

Task D has no design of its own; it is a report. Its Design and Complexity sections shall draw on the SDS breakdowns and figures below, so the report and the code describe the same algorithms.

| Task | Step-by-step breakdown (SDS) | Complexity (SDS) | Visual walkthrough |
| - | - | - | - |
| A | [4.1 Approach](SDS-Pioneer-Delivery-PartII-v1.0.md#41-approach), [4.2 Worked Example](SDS-Pioneer-Delivery-PartII-v1.0.md#42-worked-example-test-a3) | [4.4](SDS-Pioneer-Delivery-PartII-v1.0.md#44-complexity) | [TaskA-Closest-Pair-Explained.md](TaskA-Closest-Pair-Explained.md), [figures/task-a/TaskA-Closest-Pair-Walkthrough.pdf](figures/task-a/TaskA-Closest-Pair-Walkthrough.pdf), [slides](figures/task-a/Task%20A%20Closest%20Pair%20Walkthrough.pptx) |
| B | [5.1 Approach](SDS-Pioneer-Delivery-PartII-v1.0.md#51-approach), [5.2 Worked Example](SDS-Pioneer-Delivery-PartII-v1.0.md#52-worked-example-test-b1) | [5.4](SDS-Pioneer-Delivery-PartII-v1.0.md#54-complexity) | [figures/task-b slides](figures/task-b/Task%20B%20Counting%20Flipped%20Pairs.pptx) |
| C | [6.1 Approach](SDS-Pioneer-Delivery-PartII-v1.0.md#61-approach), [6.2 Worked Example](SDS-Pioneer-Delivery-PartII-v1.0.md#62-worked-example-test-c1) | [6.4](SDS-Pioneer-Delivery-PartII-v1.0.md#64-complexity) | [figures/task-c slides](figures/task-c/Task%20C%20Selecting%20the%20kth%20Smallest.pptx) |

The performance numbers shall come from the benchmark ([SDS 9](SDS-Pioneer-Delivery-PartII-v1.0.md#9-benchmark-design)), and the correctness evidence from the test suite ([SDS 7](SDS-Pioneer-Delivery-PartII-v1.0.md#7-test-suite-design)).

### 7.1 Writing Style (team requirement)

- Use plain language. Use a technical term only when it is needed, and define it the first time it appears.

- Keep each explanation as short as it can be while staying accurate. Do not trade correctness for simplicity.

- Prefer a short example or one-line reason over a long derivation.

## 8. Verification Requirements

The implementation shall test every required case in sections 4–6 and the additional cases they list. Small-input results shall also be checked against reference methods:

| Task | Required algorithm | Reference method, testing only |
| - | - | - |
| A | Divide-and-conquer closest pair | All-pairs brute force |
| B | Merge-sort inversion count | Two nested loops |
| C | Median-of-medians selection | Full sort, then index `k − 1` |


Reference methods are test oracles. They shall not be the submitted algorithm.

Tests shall also verify that:

- each invalid-input condition listed in sections 4.1, 5.1, and 6.1 produces a clear error or documented exception;

- Task A returns original IDs, and its `d²` matches the brute-force minimum;

- Task B returns `4,999,950,000` for the large-count check;

- Task C's returned value equals the sorted oracle's value at index `k − 1`.

No test results, timings, or implementation completion are claimed until executable code exists.

## 9. Submission Requirements

### 9.1 Required Contents (instructor)

The final submission is one ZIP file uploaded to Canvas, containing exactly:

- source code for Tasks A–C and the code needed to run them and reproduce the tests, including the build files (`pom.xml`, Maven Wrapper) and the CLI and test sources;

- test inputs and outputs for the required and additional cases: the fixture files, the expected results, and the recorded output of a final suite run;

- a README listing the programming language, dependencies, input/output formats, and exact commands to run each algorithm and reproduce the tests;

- the Task D report as a PDF.

The ZIP shall not include items the assignment does not ask for, such as the SRS, SDS, `.git`, build output in `target/`, or the assignment PDF.

Automated tests are encouraged but not required. A GitHub repository is recommended but does not replace the Canvas upload.

### 9.2 Local Packaging Layout (team)

- Submission files shall be assembled in a `submission/` directory at the project root. Git shall not track this directory.

- `submission/raw/` shall hold exactly the files that go into the ZIP, laid out as they appear after extraction.

- `submission/` shall also hold the ZIP file built from `submission/raw/`.

- Extracting the ZIP shall produce the same files as `submission/raw/`.

- Building and running the tests from an extracted copy shall succeed using the README commands.

## 10. Deferred Scope

The following are design decisions, not accepted requirements:

- the exact start command or script, menu wording, and how choices are entered;

- CSV fixture schemas for each task, including headers, delimiters, and how Task B's two sequences and Task C's `k` are stored;

- the format of expected results and recorded suite output;

- whether invalid input stops at the first error or reports all errors, plus the exception types used;

- Java packages, class names, and method signatures;

- test-output layout beyond section 3.4.2;

- base-case size thresholds for Tasks A and C;

- the timing method for Task D and how its input data is generated;

- the ZIP filename and how it is built.

These decisions belong to the SDS.

