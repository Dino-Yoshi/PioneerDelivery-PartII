# Pioneer Delivery — Software Design Specification, Part II

- **Project:** Pioneer Delivery Company's Algorithm Portfolio — Part II: Divide and Conquer

- **Course:** CS 413 — Analysis of Algorithms

- **Version:** 1.1

- **Date:** October 21, 2026

- **Status:** Final, matches the submitted implementation

- **Requirements authority:** [SRS-Pioneer-Delivery-PartII-v1.0.md](SRS-Pioneer-Delivery-PartII-v1.0.md)

## Revision History

| Version | Date | Changes |
| - | - | - |
| 0.1 | October 19, 2026 | First draft from the accepted SRS and the design review session. Later the same day: Tasks A–C pseudocode rewritten in plain steps with clearer names, and a worked example added for each task using an assignment test case. Plain-language step sequence added to Task A (4.1), with a link to a separate illustrated Task A walkthrough. October 20: plain-language step sequence and two-pointer merge summary added to Task B (5.1), and a step sequence with rank bands added to Task C (6.1), with a note on how k shifts. Accepted as the team baseline for implementation. October 21: updated to match the implementation as built: the `suite` module list (2), where the reference checks live (7.4), Task C counting before it copies the kept group (6.1, 6.4), and the Task D report files (1). |
| 1.0 | October 21, 2026 | Final version for submission. No design changes since the October 21 update to 0.1; links updated to the final SRS, and the file renamed from `-v0.1.md`. |
| 1.1 | October 21, 2026 | Submission packaging fix (10): the ZIP is built with Python's `zipfile`, which keeps Unix permissions, because `jar` dropped the executable bit and `./run.sh` failed after `unzip`. The check now extracts with `unzip` and confirms `run.sh` and `mvnw` are executable. File renamed from `-v1.0.md`. |


## Table of Contents

- [1. Design Goals and Baseline](#1-design-goals-and-baseline)

- [2. Module Structure](#2-module-structure)

- [3. Task Interfaces](#3-task-interfaces)

- [4. Task A Design](#4-task-a-design)

- [5. Task B Design](#5-task-b-design)

- [6. Task C Design](#6-task-c-design)

- [7. Test Suite Design](#7-test-suite-design)

- [8. CLI Design](#8-cli-design)

- [9. Benchmark Design](#9-benchmark-design)

- [10. Submission Packaging](#10-submission-packaging)

- [11. Requirements Traceability](#11-requirements-traceability)

## 1. Design Goals and Baseline

The design aims for the simplest structure a human developer can read without explanation. Each task is a single class with one public method. The test suite and CLI call those methods; the algorithms do not read files, print output, or know about the suite.

| Decision | Accepted design |
| - | - |
| Java | JDK 25 (`--release 25`) |
| Build | Maven, run through the Maven Wrapper |
| Runtime dependencies | None; JUnit 5 is test-only |
| Package layout | One package per feature |
| Invalid input | `IllegalArgumentException` at the first problem, with a specific message |
| Task A | Divide-and-conquer closest pair; brute force at `n <= 3` |
| Task B | Rank conversion plus merge-sort inversion count |
| Task C | Median-of-medians selection; sort directly at `n <= 5` |
| Test cases | One shared case list in `src/main`, used by the CLI menu and by JUnit |
| Fixtures | One input file and one expected file per case, in `tests/` |
| CLI | Looping numbered menu, started by `run.sh` / `run.cmd` |
| Timing | Separate `Benchmark` main class |
| Task D report | Written in `docs/TaskD.md`, exported through LibreOffice to `reports/TaskD.odt` and `reports/TaskD.pdf` |


## 2. Module Structure

```
edu.pioneer
├── App                         looping menu (entry point)
├── Benchmark                   Task D timing (separate entry point)
├── closestpair                 Task A
│   ├── Location                record (String id, int x, int y)
│   ├── ClosestPair             record (String firstId, String secondId, long squaredDistance)
│   └── ClosestPairFinder
├── inversions                  Task B
│   └── InversionCounter
├── selection                   Task C
│   └── KthSmallestSelector
└── suite                       shared test suite
    ├── Task                    enum A, B, C with display titles
    ├── TestCase                record (String id, String description)
    ├── CaseCatalog             the list of cases for each task
    ├── FixtureReader           reads input and expected CSV files
    ├── Outcome                 expected or actual result: a pair, a number, or an error
    ├── CheckResult             record (id, description, expected, actual, passed)
    ├── SuiteRunner             runs cases and random reference checks
    ├── ReferenceChecks         A-ref: all-pairs brute force (testing only)
    ├── InversionRefCheck       B-ref: two nested loops (testing only)
    ├── SelectionRefCheck       C-ref: full sort (testing only)
    ├── ResultPrinter           formats results and summaries
    └── FixtureGenerator        one-time generator for the two big fixtures

src/test/java/edu/pioneer
├── suite/SuiteTest             JUnit entry: one dynamic test per case
├── suite/SuiteRunnerTest, suite/FixtureReaderTest, AppTest
└── closestpair/, inversions/, selection/   unit tests for each task class
```

The reference checks live in `src/main` because the CLI runs them, but only the suite calls them. No task class calls another task class.

## 3. Task Interfaces

These three methods are the documented input interface (SRS 3.2). Each copies its input and never modifies the caller's data.

```
ClosestPair ClosestPairFinder.findClosestPair(List<Location> locations)
long        InversionCounter.countInversions(List<String> planned, List<String> actual)
int         KthSmallestSelector.selectKthSmallest(int[] times, int k)
```

Each method checks its input before running the algorithm and throws `IllegalArgumentException` at the first problem found. A `null` argument or `null` element is also rejected this way.

| Task | Condition | Message contains |
| - | - | - |
| A | Fewer than two locations | `at least two locations` |
| A | Repeated ID | `duplicate ID` |
| A | Repeated `(x, y)` | `duplicate coordinates` |
| A | Coordinate outside ±1,000,000,000 | `out of range` |
| B | Empty sequence | `at least one` |
| B | Different lengths | `different lengths` |
| B | ID repeated within a sequence | `duplicate ID` |
| B | ID in one sequence only | `not in planned` |
| C | Empty array | `at least one` |
| C | Negative time | `negative` |
| C | `k` outside `1..n` | `out of range` |


A non-integer value cannot reach these methods, because their parameters are already integers. `FixtureReader` checks this when reading a file and throws `IllegalArgumentException` with `not an integer`. Messages also name the offending value, for example `duplicate coordinates (3, 4) for IDs B and E`.

## 4. Task A Design

### 4.1 Approach

In short: **sort once, split down, solve the tiny groups, then choose, strip, and return at each level going up.**

1. **Once:** sort the points by x and by y.

2. **Going down:** split the x-sorted points in half at a dividing line, and split the y-sorted list the same way while keeping its order. Repeat until each group has 3 or fewer points.

3. **Bottom:** compare every pair in each tiny group.

4. **Going up, at each split:**

   1. **Choose:** take the closer of the two halves' answers. Its distance is `d`, the best so far.

   2. **x strip:** keep only points within `d` of this split's dividing line. Only these can form a closer pair that crosses the line.

   3. **y strip:** in bottom-to-top order, each strip point checks the points above it, stopping at the first one that is `d` or more higher. If a closer pair turns up, `d` shrinks.

   4. **Return:** pass the best pair up to the next level.

5. **Top:** the final combine gives the answer.

Sorting happens only once; every split keeps the order of the sorted lists. Points outside a strip are not removed from the problem: pairs among them were already checked inside their own halves at lower levels.

For an illustrated, step-by-step walkthrough, see [TaskA-Closest-Pair-Explained.md](TaskA-Closest-Pair-Explained.md).

All distance comparisons use squared distances, so no square roots are needed.

```
findClosestPair(locations):
    check the input (section 3)
    pointsByX = locations sorted by x (ties broken by y)
    pointsByY = locations sorted by y (ties broken by x)
    return closestPair(pointsByX, pointsByY)

closestPair(pointsByX, pointsByY):
    // Base case: few points, so compare every pair
    if there are 3 or fewer points:
        return the closest pair found by comparing every pair

    // 1. Divide: the first half of pointsByX goes left, the rest goes right
    leftByX   = first half of pointsByX
    rightByX  = second half of pointsByX
    dividingX = x of the last point in leftByX

    // Keep each side in y order without sorting again:
    // walk pointsByY once and send each point to the side it was put on above
    leftByY, rightByY = pointsByY split by side, keeping its order

    // 2. Conquer: solve each side on its own
    best = the closer of closestPair(leftByX, leftByY)
                     and closestPair(rightByX, rightByY)

    // 3. Combine: look for a closer pair that crosses the line
    return checkStrip(pointsByY, dividingX, best)

checkStrip(pointsByY, dividingX, best):
    // Only points near the line can be part of a closer crossing pair
    strip = points in pointsByY whose horizontal distance to dividingX
            is less than best's distance (still in y order)

    for each point p in strip:
        for each later point q in strip:
            stop once q is at least best's distance above p
            if p and q are closer than best: best = (p, q)

    return best
```

### 4.2 Worked Example (Test A3)

Points: `A(−3, 1)`, `B(−3, 11)`, `C(1, 1)`, `D(2, 15)`.

1. **Sort.** By x: `A, B, C, D`. By y: `A, C, B, D`.

2. **Divide.** Left is `A, B`; right is `C, D`. The dividing line is at `x = −3` (B's x).

3. **Conquer.** Left: `A–B` has `d² = 0² + 10² = 100`. Right: `C–D` has `d² = 1² + 14² = 197`. The best so far is `A–B`, 100.

4. **Combine.** Every point is within distance 10 of the line, so all four are in the strip, in y order `A, C, B, D`.

   - `A` vs `C`: `d² = 4² + 0² = 16`, closer than 100. The best is now `A–C`, 16.

   - `A` vs `B`: B is 10 above A, and `10² = 100` is not less than 16, so stop.

   - `C` vs `B` and `B` vs `D` stop the same way.

Result: `A, C; d² = 16`. The answer crosses the dividing line, which is why A3 matters.

### 4.3 Design Notes

- **Tied x-coordinates.** Points are split by their position in `pointsByX`, and the implementation records which points went left. Points with the same x as the dividing line can land on either side, but each one is placed by that record (its identity), not by comparing its x to `dividingX` (SRS 4.2).

- **No re-sorting.** `leftByY` and `rightByY` come from one pass over `pointsByY`, so they are already sorted by y.

- **Strip bound.** Neither half has a pair closer than `best`, so only a few points can be packed close together near the line. For any strip point, at most 7 later strip points can be within `best`'s distance above it. The inner loop therefore stops after a constant number of steps, instead of comparing every pair in the strip.

- **All distances are squared** `long` values, so no square roots or floating point are used. The coordinate limit keeps `d²` within `long` range (SRS 4.1).

- **Ties.** On equal distances, the first pair found is kept. Any minimizing pair is acceptable.

### 4.4 Complexity

The two initial sorts take `O(n log n)`. Each recursion level does `O(n)` work (splitting `pointsByY` and scanning the strip), and there are `O(log n)` levels, so the total is `O(n log n)`. Extra memory is `O(n)`: the lists alive at any one time along a recursion path add up to `n + n/2 + n/4 + … < 2n`.

## 5. Task B Design

### 5.1 Approach

In short: **merge sort the planned-order ranks; each merge is "merge two sorted lists into one" plus one extra line: when the next number taken comes from the right list, add how many numbers are still waiting in the left list.**

First, replace each ID with its position in the planned order. A pair delivered in the opposite order then becomes a bigger number before a smaller one. Every pair is either inside the left half, inside the right half, or crossing the divider between them; the halves are handled by recursion, and the crossing pairs are counted by the merge.

`count(piece)` returns two things: how many flipped pairs the piece contains, and the same numbers in sorted order.

1. **Base case:** if the piece has 1 number, it has no pairs. Return 0 and the piece as is.

2. **Split** the piece into a left half and a right half.

3. **Recurse:** call `count` on the left half, then on the right half. Each call returns its half's flipped-pair count and that half now sorted.

4. **Merge with two pointers** into a new, empty **merged list**:

   1. Put the left pointer at the start of the sorted left half, and the right pointer at the start of the sorted right half.

   2. Move the left pointer forward, appending each left number it passes to the end of the merged list, until it reaches a number bigger than the right pointer's number.

   3. Append the right pointer's number to the merged list. Add the count of left numbers still waiting (the one at the left pointer and everything after it) to this merge's count. Each forms a flipped pair with that right number.

   4. Move the right pointer forward, and continue the left pointer from where it stopped. Neither pointer ever moves backward.

   5. When either half runs out, append the other half's leftovers to the merged list. They add nothing to the count.

5. **Return** left count + right count + this merge's count, along with the merged list.

6. **Move up a level:** the caller receives this sorted merged list as one of its two sorted halves and does its own merge (step 4) with it. This repeats one level up each time, until the merge of the whole list returns the final count.

Why sorting the halves is safe: the pairs inside a half were already counted before it was sorted, and a crossing pair only depends on whether the left number is bigger than the right number, not on where either sits inside its half.

For an illustrated, step-by-step walkthrough, see the slides in [figures/task-b/Task B Counting Flipped Pairs.pptx](figures/task-b/Task%20B%20Counting%20Flipped%20Pairs.pptx).

```
countInversions(planned, actual):
    check the input (section 3)

    // Step 1: turn IDs into numbers (rank = position in planned, starting at 1)
    rankOf = { planned[0] → 1, planned[1] → 2, ... }
    ranks  = [ rankOf[id] for each id in actual ]

    // Step 2: count out-of-order pairs while merge sorting the ranks
    return sortAndCount(ranks)

sortAndCount(ranks):
    // Sorts ranks and returns how many inversions it contained
    if ranks has 0 or 1 values: return 0

    left  = first half of ranks
    right = second half of ranks
    count = sortAndCount(left) + sortAndCount(right)
    count = count + mergeAndCount(left, right, into ranks)
    return count

mergeAndCount(left, right, output):
    // Both halves are sorted. Repeatedly take the smaller front value.
    count = 0
    while left and right both have values:
        if front of left <= front of right:
            move front of left to output
        else:
            move front of right to output
            // That value is smaller than every value still in left,
            // and all of those came before it: one inversion each
            count = count + (number of values still in left)
    move any leftover values to output
    return count
```

The implementation works on index ranges of one array with one shared buffer instead of copying halves, but the steps are the same.

### 5.2 Worked Example (Test B1)

Planned `[A, B, C, D, E]`, actual `[A, C, D, B, E]`.

1. **Ranks.** A→1, B→2, C→3, D→4, E→5, so actual becomes `[1, 3, 4, 2, 5]`.

2. **Divide.** Left `[1, 3, 4]`, right `[2, 5]`. Both are already in order, so each half contributes 0.

3. **Merge** `[1, 3, 4]` with `[2, 5]`:

   - Take 1 from left.

   - Right's 2 is smaller than left's 3, so take 2. Left still holds `3, 4`, and both came before 2 but are bigger: **+2**.

   - Take 3, 4, then 5. No more inversions.

Result: **2**, the pairs `(C, B)` and `(D, B)`, which are `(B, C)` and `(B, D)` in planned order.

### 5.3 Design Notes

- **Cross-half count.** When a value from `right` is taken before the values still waiting in `left`, each of those left values is larger but came earlier, so each forms one inversion. They are counted all at once instead of one pair at a time.

- **Count type.** `count` is a `long`. The 100,000-element reversed case gives 4,999,950,000, which is larger than an `int` can hold.

- **Validation** builds `rankOf` from `planned`; a repeat there is a duplicate. While building `ranks`, an ID missing from `rankOf` is `not in planned`, and an ID seen twice in `actual` is a duplicate.

- **Memory.** One buffer array of size `n` is allocated once and reused by every merge.

### 5.4 Complexity

Building the map and rank array is `O(n)`. Merge sort is `O(n log n)`: `O(log n)` levels with `O(n)` merging per level. Extra memory is `O(n)` for the map, ranks, and buffer, plus `O(log n)` recursion depth.

## 6. Task C Design

### 6.1 Approach

In short: **binary search on ranks. Each round picks a pivot guaranteed to be near the middle, splits the values around it, and keeps only the group that holds rank k.** The whole array is never sorted; only groups of 5 or fewer are.

1. Split the current values into groups of 5. Sort each tiny group and take its middle value (the lower middle for an even-sized group).

2. The middle of those medians is the **pivot**. With many groups, finding it runs this same procedure on the list of medians.

3. Compare every value with the pivot, in one pass, and count how many are **smaller** and how many are the **same**. The rest are **larger**. Nothing is sorted or copied yet.

4. Use the group sizes to find which rank band k lands in (s = size of smaller, e = size of same):

   - k ≤ s → keep **smaller**; k stays the same.

   - s < k ≤ s + e → **stop: the answer is the pivot.** The kth smallest is one of the pivot's copies.

   - k > s + e → keep **larger**; new k = k − s − e, because everything below it was thrown away.

5. In a second pass, copy only the kept group, keeping the original order. Repeat with it until it has 5 or fewer values.

6. Sort that small group. The value at position k (the current k) is the answer.

The rank bands come from the group sizes alone. In sorted order, smaller would hold ranks 1 to s, same ranks s + 1 to s + e, and larger the rest, but no group is sorted to find this.

> **Key idea: k is a rank inside the current group, so it shifts when values below it are thrown away.** In C1 (k = 5), keeping "larger" throws away the 4 values below it: 3 in smaller and 1 in same. The 5th smallest overall is therefore the 1st smallest inside "larger": k = 5 − 3 − 1 = 1. Sorting "larger" gives [16, 18, 22, 29, 35], and its 1st value, 16, is the answer. Keeping "smaller" throws away nothing below, so k stays the same.

For an illustrated, step-by-step walkthrough, see the slides in [figures/task-c/Task C Selecting the kth Smallest.pptx](figures/task-c/Task%20C%20Selecting%20the%20kth%20Smallest.pptx).

```
selectKthSmallest(times, k):
    check the input (section 3)
    return select(copy of times, k)

select(values, k):
    // Returns the kth smallest value, with k counted from 1

    // Base case: 5 or fewer values, so just sort them
    if values has 5 or fewer: sort values and return the kth one

    // Step 1: choose a pivot that is guaranteed to be near the middle
    pivot = medianOfMedians(values)

    // Step 2: count the three groups around the pivot (one pass)
    s = number of values less than pivot
    e = number of values equal to pivot

    // Step 3: keep only the group that holds the kth smallest,
    // copying just that group (second pass)
    if k <= s:
        return select(values less than pivot, k)
    if k <= s + e:
        return pivot
    return select(values greater than pivot, k − s − e)

medianOfMedians(values):
    medians = empty list
    for each group of 5 values in a row (the last group may be shorter):
        sort the group and add its middle value to medians
    return select(medians, middle position of medians)
```

For a group or list of even size, "middle" means the lower of the two middle values.

### 6.2 Worked Example (Test C1)

`T = [18, 4, 29, 11, 4, 16, 35, 9, 22]`, `k = 5`.

1. **Groups of five.** `[18, 4, 29, 11, 4]` sorted is `[4, 4, 11, 18, 29]`, middle 11. `[16, 35, 9, 22]` sorted is `[9, 16, 22, 35]`, middle 16.

2. **Pivot.** The middle of the medians `[11, 16]` is 11.

3. **Split.** Smaller `[4, 4, 9]` (3 values), same `[11]` (1 value), larger `[18, 29, 16, 35, 22]` (5 values).

4. **Choose a group.** `k = 5` is more than 3 (smaller) and more than 3 + 1 = 4 (smaller and same), so the answer is in larger. The 4 values thrown away are all below it, so look for rank `5 − 3 − 1 = 1` in larger.

5. **Base case.** Larger has 5 values. Sorted, it is `[16, 18, 22, 29, 35]`; the 1st is **16**.

Result: **16**.

### 6.3 Design Notes

- **Adjusting k.** Throwing away `smaller` removes that many of the smallest values, and throwing away `same` removes the next ones. So when continuing in `larger`, `k` drops by both sizes. When continuing in `smaller`, nothing below it was removed, so `k` stays the same.

- **Duplicates.** All copies of the pivot go into `same`, so repeated values never cause an endless loop. If every value is the same, the answer is found in one step.

- **Why the pivot is good.** Half of the group medians are no bigger than the pivot. Each of those medians is itself no smaller than 2 other values in its group of five. Together, that is about 3/10 of all values that are no bigger than the pivot, and by the same reasoning about 3/10 that are no smaller. So each step keeps at most about 7/10 of the values. That guarantee is what makes the worst case linear; a random pivot only makes it linear on average.

- Sorting is used only on groups of at most five and on base cases of at most five values (SRS 6.2).

### 6.4 Complexity

`T(n) <= T(n/5) + T(7n/10) + O(n)`. Since `1/5 + 7/10 < 1`, the work shrinks geometrically, giving `O(n)` time. Extra memory is `O(n)`: each round creates only the kept group and the medians array, and their sizes shrink geometrically down the recursion. The two passes over the values in each round are still `O(n)` per round.

## 7. Test Suite Design

### 7.1 One case list, two ways to run it

`CaseCatalog` lists every case for each task, in display order. `SuiteRunner.run(Task)` runs that task's cases and its random reference check, and returns a `List<CheckResult>`. Two callers use it:

- `App` (the menu) prints the results with `ResultPrinter`.

- `SuiteTest` (JUnit) uses `@TestFactory` to turn each `CheckResult` into one dynamic test named like `A3 Closest pair lies in different halves of the split`. The test fails with the expected and actual text when `passed` is false, so `./mvnw test` fails if any case fails.

Fixture paths are relative to the project root. `run.sh`, `run.cmd`, and Maven all run from there.

### 7.2 Fixture files

Each case has `tests/inputs/<ID>.csv` and `tests/expected/<ID>.csv`.

Input files:

```
Task A (header, then one location per row)     Task B (labeled rows)       Task C (labeled rows)
id,x,y                                         planned,A,B,C,D,E           k,5
A,-3,1                                         actual,A,C,D,B,E            times,18,4,29,11,4,16,35,9,22
B,-3,11
```

Expected files use one label per line:

```
Task A                  Task A, tied case             Task B           Task C        Invalid input (any task)
squared_distance,16     squared_distance,1            inversions,2     value,16      error,duplicate coordinates
pair,A,C                pair,P,Q
                        pair,R,S
```

- **Task A** passes when `d²` matches and the returned pair, in either order, is one of the listed pairs.

- **Error cases** pass when an `IllegalArgumentException` is thrown, by `FixtureReader` or by the task method, and its message contains the given words.

### 7.3 Case list

| Task | Case IDs |
| - | - |
| A | `A1`–`A4` (required), `A5-tie`, `A6-shared-x`, `A7-large-coords`, `A8-one-point`, `A9-dupe-id`, `A10-dupe-coords`, `A11-out-of-range`, `A12-non-integer`, `A-ref` |
| B | `B1`–`B4` (required), `B5-large`, `B6-one-move`, `B7-empty`, `B8-length-mismatch`, `B9-dupe-id`, `B10-unknown-id`, `B-ref` |
| C | `C1`–`C5` (required), `C6-sorted`, `C7-reverse`, `C8-many-dupes`, `C9-empty`, `C10-k-zero`, `C11-k-too-big`, `C12-negative`, `C-ref` |


| Case | What it checks |
| - | - |
| `A5-tie` | Several pairs share the minimum distance; any one is accepted |
| `A6-shared-x` | Several points share the dividing x-coordinate |
| `A7-large-coords` | Coordinates at ±1,000,000,000 give the exact `d²` with no overflow |
| `B5-large` | 100,000 IDs fully reversed give 4,999,950,000 |
| `B6-one-move` | The last planned ID dispatched first gives `n − 1` inversions |
| `C6-sorted`, `C7-reverse` | Already sorted and reverse-sorted input |
| `C8-many-dupes` | 100,000 values from 0–9, `k = 50,000` |


The remaining cases are invalid input from SRS 4.1, 5.1, and 6.1.

### 7.4 Random reference checks

`A-ref`, `B-ref`, and `C-ref` each generate 500 small random inputs from the fixed seed `413` and compare the algorithm with a simple method. Each check is its own class: `ReferenceChecks` (A-ref), `InversionRefCheck` (B-ref), and `SelectionRefCheck` (C-ref).

| Check | Random input | Compared with |
| - | - | - |
| `A-ref` | 2–40 distinct points with coordinates in −20..20, so ties are common | All-pairs brute force: same `d²`, and the returned pair really is at that distance |
| `B-ref` | A random shuffle of 1–40 IDs | Two nested loops |
| `C-ref` | 1–40 values in 0..10, so duplicates are common, and a random valid `k` | Full sort, then index `k − 1` |


Each check shows as one line, for example `[PASS] A-ref  500 random inputs match brute force`. On a failure, it prints the first input that disagreed.

### 7.5 Big fixtures

`FixtureGenerator` writes `B5-large` and `C8-many-dupes` (input and expected) once. The files are committed, and the generator is kept so they can be rebuilt.

### 7.6 Output

```
== Task A: Closest Delivery Locations ==
[PASS] A3  Closest pair lies in different halves of the split
       expected: A, C; d² = 16
       actual:   A, C; d² = 16
[PASS] A10-dupe-coords  Repeated coordinates are rejected
       expected: error containing "duplicate coordinates"
       actual:   error: duplicate coordinates (3, 4) for IDs B and E
...
Task A: 13 passed, 0 failed
```

Task A pair IDs print in alphabetical order. The full suite prints each task's section and then an overall total.

## 8. CLI Design

### 8.1 Menu

```
Pioneer Delivery Part II — Test Suite
  1  Run all tests
  2  Task A: Closest Delivery Locations
  3  Task B: Counting Dispatch-Order Inversions
  4  Task C: Selecting a Delivery-Time Threshold
  0  Exit
Choice:
```

`App` reads one line at a time from standard input.

- `1`–`4` run the selected tests, print the results, and show the menu again.

- `0` exits with status 0. End of input also exits with status 0.

- Anything else prints `Unknown choice: <text>` and shows the menu again.

The temporary `task-a`, `task-b`, and `task-c` placeholder commands are removed.

### 8.2 Start scripts

`run.sh` and `run.cmd` change to the project directory, run `./mvnw -q -DskipTests package`, then `java -jar target/pioneer-delivery-part2.jar`. Tests are skipped during the build so that a failing case appears as `FAIL` in the menu instead of stopping the program from starting.

## 9. Benchmark Design

`Benchmark` is a separate main class:

```
./mvnw -q -DskipTests package
java -cp target/classes edu.pioneer.Benchmark
```

- **Sizes:** 100, 1,000, 5,000, 10,000, and 100,000.

- **Inputs:** generated from the fixed seed `413`.

  - Task A: distinct random points with coordinates in ±1,000,000.

  - Task B: a random shuffle of `n` IDs.

  - Task C: random values in 0..1,000,000 with `k = n / 2`.

- **Method:** five warm-up runs, then eleven timed runs per size with `System.nanoTime`. The median time is reported.

- **Output:** one table per task with size, median milliseconds, and the ratio to the previous size.

The Task D report explains the extra sizes: small sizes are noisy, and the range up to 100,000 makes `O(n)` and `O(n log n)` growth easier to tell apart.

## 10. Submission Packaging

`package-submission.sh` builds the Canvas ZIP. It is a local tool and does not go in the ZIP.

1. Run `./mvnw -q clean package`, which also runs the tests.

2. Stop if `reports/TaskD.pdf` is missing.

3. Delete and recreate `submission/raw/`.

4. Copy in `pom.xml`, `mvnw`, `mvnw.cmd`, `.mvn/`, `run.sh`, `run.cmd`, `src/`, `tests/`, `README.md`, and `reports/TaskD.pdf`.

5. Record the suite output by running `printf '1\n0\n' | java -jar target/pioneer-delivery-part2.jar` and saving it to `submission/raw/tests/results/suite-output.txt`.

6. Create `submission/pioneer-delivery-part2.zip` from `raw/` with Python's `zipfile`, which stores Unix permissions so `run.sh` and `mvnw` stay executable. (`jar` does not store them.)

7. Extract the ZIP with `unzip` into a temporary folder, check its file list matches `raw/`, check that `run.sh` and `mvnw` are executable, and run `./mvnw -q test` there.

`submission/` is ignored by Git (SRS 9.2).

## 11. Requirements Traceability

| SRS | Design |
| - | - |
| 3.1, 3.2 Instructor requirements, input handling | Sections 3 and 7.2 |
| 3.4.1 Easy run | Section 8.2 |
| 3.4.2 Menu and test suite | Sections 7 and 8.1 |
| 3.4.3 No user-supplied datasets | Section 8.1 (menu runs fixtures only) |
| 4 Task A | Sections 3 and 4 |
| 5 Task B | Sections 3 and 5 |
| 6 Task C | Sections 3 and 6 |
| 7 Task D | Section 9; report in `reports/` |
| 8 Verification | Sections 7.3 and 7.4 |
| 9 Submission | Section 10 |


