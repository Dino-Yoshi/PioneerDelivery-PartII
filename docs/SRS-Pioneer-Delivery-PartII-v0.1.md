# Pioneer Delivery — Software Requirements Specification, Part II

- **Project:** Pioneer Delivery Company's Algorithm Portfolio — Part II: Divide and Conquer
- **Course:** CS 413 — Analysis of Algorithms
- **Version:** 0.1
- **Date:** October 19, 2026
- **Status:** Draft for review; not yet accepted for implementation planning
- **Requirements authority:** `CS413_PA-part2-2026Fall.pdf`
- **Supersedes:** Rough Part II requirements notes (`shitassSRS.odt`)

## 1. Purpose and scope

Pioneer Delivery wants three operational questions answered with divide-and-conquer algorithms:

- **Task A:** find the two delivery locations closest to each other.
- **Task B:** count how many pairs of deliveries were dispatched in the opposite order from the plan.
- **Task C:** find the kth-smallest delivery-time estimate without sorting the full dataset.

Task D is a separate 1–2 page PDF report covering design, complexity, correctness testing, and performance evidence for Tasks A–C. It is not a fourth algorithm.

Part II is independent of Part I. It does not reuse Part I's scheduling problem, data model, GUI, or input-validation behavior.

## 2. Accepted implementation baseline

| Decision | Accepted choice |
|---|---|
| Language/runtime | JDK 25 |
| Build | Maven |
| Interface | Command-line only; no JavaFX or other GUI |
| Input | CSV files |
| Task independence | Tasks A, B, and C each run and test independently |
| Version control | Git; separate local Part II project and repository |
| Inversion count type | Java `long` |

These are team decisions, not additional instructor requirements unless explicitly stated below. Exact CSV schemas and CLI syntax are not yet accepted; see section 10.

## 3. General requirements

### 3.1 Instructor requirements

- Each of Tasks A–C shall be executable and testable independently.
- Each core algorithm shall be implemented by the team. Standard-library data structures and sorting may be used only where a task permits it, and a library routine shall not replace a task's core algorithm.
- Each task shall handle the input assumptions stated in its section.
- Invalid input shall produce a clear error message or a documented exception. Invalid input includes at least the conditions listed under each task.
- Input and output formats shall be documented in the README.
- Each task shall include the required test cases and at least one additional test of the team's own.

### 3.2 Input handling

- Each task reads its own input. There is no shared dataset across tasks.
- When an input violates a task's assumptions, the task shall report an error and shall not report an algorithmic result for that input.
- A file-open or read failure is an error.

Part I's skip-and-log handling of invalid rows, `.csv` extension check, empty-file-as-valid rule, and processing report do not apply to Part II unless they are adopted later as a design decision.

### 3.3 Unresolved clarification: "implement the algorithms yourself"

The team has asked the instructor which interpretation applies:

| Option | Interpretation |
|---|---|
| A | Algorithms must be implemented from scratch instead of delegated to library routines. AI assistance is allowed. |
| B | AI-generated implementations are not permitted, even when they reproduce the algorithm from scratch. |

As of this version, there is no answer. This SRS neither grants nor restricts AI assistance. Algorithm implementation work shall not begin until the question is resolved and the team authorizes it.

## 4. Task A requirements — closest delivery locations

### 4.1 Input and output

The input is a set of `n >= 2` locations:

| Field | Requirement |
|---|---|
| `id` | Location identifier; unique within one input |
| `x` | Integer |
| `y` | Integer |

No two locations shall have identical `(x, y)` coordinates.

The result shall show the original IDs of a closest pair and their squared Euclidean distance `d² = (xi − xj)² + (yi − yj)²`. The Euclidean distance may also be shown. When several pairs share the minimum distance, any one of them is acceptable.

Invalid input includes fewer than two locations, duplicate IDs, duplicate coordinate pairs, and non-integer coordinates.

### 4.2 Algorithm requirements

- Use the divide-and-conquer closest-pair algorithm: divide the points into two halves by x-coordinate, solve each half recursively, and check for a closer pair crossing the dividing line.
- When x-coordinates are tied at the dividing line, assign points to halves by point identity, not by an x-coordinate threshold alone.
- Maintain y-sorted information through the recursion. Do not re-sort each subproblem by y-coordinate.
- Use brute-force comparison only for small base cases.
- In the combine step, examine only the vertical strip around the dividing line and use the constant-neighbor geometric bound from class.
- Run in `O(n log n)` worst-case time, including initial sorting.
- Return the original location IDs, not only the distance.

### 4.3 Required tests

| Test | Locations `(id, x, y)` | Expected result |
|---|---|---|
| A1 | `A(0, 0), B(3, 4)` | `A, B; d² = 25` |
| A2 | `A(0, 0), B(7, 2), C(4, 5), D(5, 5), E(12, 2), F(2, 9)` | `C, D; d² = 1` |
| A3 | `A(−3, 1), B(−3, 11), C(1, 1), D(2, 15)` | `A, C; d² = 16` |
| A4 | `A(−5, −5), B(−5, −2), C(6, 8)` | `A, B; d² = 9` |

A3 shall be verified specifically, because its closest pair lies in different halves of the recursive division.

Additional required tests:

- a dataset with multiple tied closest pairs, where any minimizing pair is accepted and `d²` equals the minimum;
- at least one more team-defined case, such as several points sharing one x-coordinate across the dividing line.

## 5. Task B requirements — counting dispatch-order inversions

### 5.1 Input and output

The input is two sequences, `planned` and `actual`, each containing the same `n >= 1` distinct delivery IDs exactly once.

An inversion is a pair of IDs whose relative order in `actual` is the reverse of their relative order in `planned`.

The result shall show the total number of inversions as a nonnegative integer.

Invalid input includes an empty sequence, sequences of different lengths, a duplicate ID within a sequence, and an ID that appears in only one sequence.

Example: `planned = [D1, D2, D3, D4, D5]` and `actual = [D1, D3, D4, D2, D5]` give the ranks `[1, 3, 4, 2, 5]` and 2 inversions, `(D2, D3)` and `(D2, D4)`.

### 5.2 Algorithm requirements

- Convert `actual` into an array of planned-order ranks.
- Count inversions with a merge-sort-based algorithm: recursively count inversions in the left and right halves, then count cross-half inversions during the merge.
- Run in `O(n log n)` time. A nested-loop `O(n²)` method shall not be the submitted algorithm.
- Store counts up to `n(n − 1)/2` in a Java `long`.

### 5.3 Required tests

| Test | Planned order | Actual order | Inversions |
|---|---|---|---:|
| B1 | `[A, B, C, D, E]` | `[A, C, D, B, E]` | 2 |
| B2 | `[A, B, C, D]` | `[A, B, C, D]` | 0 |
| B3 | `[A, B, C, D]` | `[D, C, B, A]` | 6 |
| B4 | `[A]` | `[A]` | 0 |

Large-count check: with `n = 100,000`, `planned = [1, 2, …, n]`, and `actual` reversed, the result shall be exactly `4,999,950,000`. This value exceeds the Java `int` range.

At least one additional team-defined case is required.

## 6. Task C requirements — selecting a delivery-time threshold

### 6.1 Input and output

The input is an array `T` of `n >= 1` nonnegative integer delivery-time estimates and an integer rank `k` with `1 <= k <= n`.

The result shall show the kth-smallest value in `T`. Ranks are 1-based: `k = 1` is the minimum and `k = n` is the maximum. Repeated values count as separate positions.

Invalid input includes an empty array, a negative or non-integer time, and `k` outside `1..n`.

### 6.2 Algorithm requirements

- Use deterministic median-of-medians selection. Divide the values into groups of at most five, sort each small group as needed, and recursively select the median of the group medians as the pivot.
- Partition the current values into three groups: less than, equal to, and greater than the pivot.
- Recurse only into the group containing rank `k`, adjusting `k` for the discarded groups. If `k` falls within the equal group, return the pivot.
- Handle duplicate values correctly.
- Run in `O(n)` worst-case time. Sorting the entire input or using randomized quickselect does not satisfy this task. Sorting small groups and small base cases is permitted.
- Explain the adjustment of `k` after partitioning in the README and Task D report.

### 6.3 Required tests

| Test | Delivery times `T` | k | Expected value |
|---|---|---:|---:|
| C1 | `[18, 4, 29, 11, 4, 16, 35, 9, 22]` | 5 | 16 |
| C2 | `[18, 4, 29, 11, 4, 16, 35, 9, 22]` | 2 | 4 |
| C3 | `[18, 4, 29, 11, 4, 16, 35, 9, 22]` | 9 | 35 |
| C4 | `[7, 7, 7, 7, 7, 7]` | 4 | 7 |
| C5 | `[8, 3]` | 1 | 3 |

Additional required tests:

- already sorted input;
- reverse-sorted input;
- a larger input with many duplicate values.

## 7. Task D requirements — validation and algorithm analysis

Task D is a PDF report of approximately 1–2 pages. For each of Tasks A–C, it shall include:

| Section | Required content |
|---|---|
| Algorithm design | How the algorithm divides the problem, solves subproblems, and combines results; for Task C, how it discards the unneeded partition |
| Complexity | Asymptotic running time and auxiliary space, with justification |
| Correctness testing | Comparison against a simple reference method on small inputs, plus at least one additional edge case per task |
| Performance evidence | Approximate execution times on at least three increasing input sizes, such as `n = 100, 1,000, 5,000`, compared with the expected growth |

The complexity section shall also explain why Task A's combine step does not examine all pairs in the strip and the role of a good deterministic pivot in Task C.

Timing results are machine-dependent and need not match a fixed benchmark.

## 8. Verification requirements

The implementation shall test every required case in sections 4–6 and the additional cases they list. Small-input results shall also be checked against reference methods:

| Task | Required algorithm | Reference method, testing only |
|---|---|---|
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

## 9. Submission requirements

The final submission is one ZIP file uploaded to Canvas, containing:

- source code for Tasks A–C and anything needed to run them;
- test inputs and outputs for the required and additional cases;
- a README listing the programming language, dependencies, input/output formats, and exact commands to run each algorithm and reproduce the tests;
- the Task D report as a PDF.

Automated tests are encouraged but not required. A GitHub repository is recommended but does not replace the Canvas upload.

## 10. Deferred scope

The following are design decisions, not accepted requirements:

- exact CLI command syntax;
- CSV schemas for each task, including headers, delimiters, and how Task B's two sequences and Task C's `k` are supplied;
- whether invalid input stops at the first error or reports all errors, plus the exception types and exit codes used;
- Java packages, class names, and method signatures;
- output text layout, including whether Task A also shows the Euclidean distance;
- base-case size thresholds for Tasks A and C;
- the timing method for Task D and how input data is generated;
- the serialization format of fixtures in `tests/inputs` and `tests/expected`.

The SDS will be written after this SRS is accepted.
