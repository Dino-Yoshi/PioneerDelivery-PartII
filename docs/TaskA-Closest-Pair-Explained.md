# Pioneer Delivery — Task A Explained: Closest Delivery Locations

- **Project:** Pioneer Delivery Company's Algorithm Portfolio — Part II: Divide and Conquer

- **Version:** 0.1

- **Date:** October 20, 2026

- **Status:** Study aid; companion to SDS section 4

- **Design authority:** [SDS-Pioneer-Delivery-PartII-v0.1.md](SDS-Pioneer-Delivery-PartII-v0.1.md)

- **Figures:** [TaskA-Closest-Pair-Walkthrough.pdf](figures/task-a/TaskA-Closest-Pair-Walkthrough.pdf) (slides exported to PDF); PowerPoint: [Task A Closest Pair Walkthrough.pptx](figures/task-a/Task%20A%20Closest%20Pair%20Walkthrough.pptx); editable original: [slide deck](https://claude.ai/artifact/KgiTAndkpo6KZSZ8bf93ap) (private to the owner until shared)

## Revision History

| Version | Date | Changes |
| - | - | - |
| 0.1 | October 20, 2026 | First version: illustrated walkthrough of test A2, splitting levels, and why the shortcuts are safe and fast. Later the same day: the embedded SVG figures were replaced by references to the walkthrough slides, saved as PDF and PowerPoint in `figures/task-a/`. |

## Table of Contents

- [1. The Problem](#1-the-problem)

- [2. The Idea in One Line](#2-the-idea-in-one-line)

- [3. Walkthrough: Test A2](#3-walkthrough-test-a2)

- [4. More Points: Splitting Again and Again](#4-more-points-splitting-again-and-again)

- [5. Why Skipping Points Is Safe](#5-why-skipping-points-is-safe)

- [6. Why It Is Fast](#6-why-it-is-fast)

- [7. The Steps in Order](#7-the-steps-in-order)

## 1. The Problem

Given delivery stops on a map, find the two that are closest together.

The obvious way is to measure every pair. With 6 points that is 15 pairs, which is fine. With 100,000 points it is about 5 billion pairs, which is not. Divide and conquer finds the same answer while skipping almost all of those pairs.

## 2. The Idea in One Line

**Sort once, split down, solve the tiny groups, then choose, strip, and return at each level going up.**

The rest of this document shows what each of those words means, using test A2.

## 3. Walkthrough: Test A2

Expected answer: **C, D with d² = 1.**

### 3.1 The points

*Figure: slide 2 of the [walkthrough slides](figures/task-a/TaskA-Closest-Pair-Walkthrough.pdf).*

### 3.2 Sort once and split

Sort the points by x: `A, F, C, D, B, E`. Put the first half on the left and the rest on the right. The dividing line goes through the last left point, C, at x = 4. C belongs to the left half because of its position in the sorted list, even though it sits on the line.

The points are also sorted by y once, now: `A, B, E, C, D, F`. That order is used in step 3.6.

*Figure: slide 4 of the [walkthrough slides](figures/task-a/TaskA-Closest-Pair-Walkthrough.pdf).*

### 3.3 Solve each half

Each half has 3 points, which is small enough to measure every pair.

- Left: A–F ≈ 9.2, A–C ≈ 6.4, F–C ≈ **4.5**.

- Right: D–B ≈ **3.6**, D–E ≈ 7.6, B–E = 5.

*Figure: slide 5 of the [walkthrough slides](figures/task-a/TaskA-Closest-Pair-Walkthrough.pdf).*

### 3.4 Choose d

Take the closer of the two answers: D–B, about 3.6. This is **d**, the best distance so far.

But the real answer, C–D, is not either half's answer. C is on the left and D is on the right, so neither half could see that pair. The next steps look for pairs like this that cross the line.

### 3.5 The x strip

A crossing pair closer than d must have both points within d of the line. A point farther away than that is already more than d from anything on the other side.

So keep only points within d (3.6) of the line: x from about 0.4 to 7.6. A and E are too far and drop out. F, C, D, and B stay.

*Figure: slide 6 of the [walkthrough slides](figures/task-a/TaskA-Closest-Pair-Walkthrough.pdf).*

### 3.6 The y strip

Go through the x strip from bottom to top: **B, C, D, F**. This order comes for free from the y-sorted list in step 3.2.

For each point, look only at the points above it, and stop at the first one that is d or more higher. Everything after that point in the list is even higher, so it can be skipped.

**B's turn.** B's y strip runs from B's height up to d above it. C and D are inside it, so they are checked: B–C ≈ 4.2, B–D ≈ 3.6, neither closer. F is 7 above B, so B stops.

*Figure: slide 7 of the [walkthrough slides](figures/task-a/TaskA-Closest-Pair-Walkthrough.pdf).*

**C's turn.** D is inside C's y strip: C–D = **1**, a new best. Now d = 1, so the y strip shrinks to 1 above C. F is 4 above C, so C stops.

*Figure: slide 8 of the [walkthrough slides](figures/task-a/TaskA-Closest-Pair-Walkthrough.pdf).*

**D's turn.** F is 4 above D, more than d = 1, so D stops. **F** has nothing above it.

### 3.7 The answer

*Figure: slide 9 of the [walkthrough slides](figures/task-a/TaskA-Closest-Pair-Walkthrough.pdf).*

**C, D with d² = 1**, which matches the expected result.

## 4. More Points: Splitting Again and Again

A2 has only 6 points, so it splits once. Larger inputs keep splitting until every group has 3 or fewer points. Each split has its own dividing line.

The figure below has 12 points. Split 1 makes two groups of 6. Each of those is split again (split 2), making four groups of 3, shown in four colors. Groups of 3 are measured directly.

*Figure: slide 10 of the [walkthrough slides](figures/task-a/TaskA-Closest-Pair-Walkthrough.pdf).*

The answers then come back up:

1. Each group of 3 reports its closest pair.

2. At each **split 2** line, the two neighboring groups choose d, check their x strip and y strip, and report the best pair of 6 points.

3. At **split 1**, the two halves choose d, check the x strip and y strip around split 1, and report the best pair of all 12.

Every split gets its own strip check. A pair that crosses a line is caught at the split that first separated its two points.

## 5. Why Skipping Points Is Safe

Both strips rest on one fact:

> **The straight-line distance between two points is at least their left-right gap, and at least their up-down gap.**

- **x strip:** a point more than d from the line is more than d from every point on the other side. It cannot be part of a closer crossing pair.

- **y strip:** a point d or more above another is at least d away from it. Neither can it be part of a closer pair.

The points that are skipped are not ignored for good. Pairs among them were already checked inside their own halves, at lower levels. They just cannot form a closer pair across *this* line.

## 6. Why It Is Fast

- **Each y strip check is tiny.** Neither half has a pair closer than d, so points on the same side cannot crowd together. Only a few of them fit in the space a single y strip covers. The standard proof shows a point never checks more than **7** others.

- **Each level is about n work.** Splitting the lists and scanning the strips touches each point about once per level.

- **There are about log₂ n levels**, because each split halves the groups. 100,000 points need about 17 levels.

- **Sorting happens once**, at the start. Each split divides the sorted lists without re-sorting them.

Total: about **n log n** steps. For 100,000 points that is a few million steps, compared with about 5 billion for measuring every pair.

## 7. The Steps in Order

1. **Once:** sort the points by x and by y.

2. **Going down:** split the x-sorted points in half at a dividing line, and split the y-sorted list the same way while keeping its order. Repeat until each group has 3 or fewer points.

3. **Bottom:** measure every pair in each tiny group.

4. **Going up, at each split:**

   1. **Choose:** take the closer of the two halves' answers. Its distance is d.

   2. **x strip:** keep only points within d of this split's line.

   3. **y strip:** in bottom-to-top order, each strip point checks the points above it, stopping at the first one that is d or more higher. If a closer pair turns up, d shrinks.

   4. **Return:** pass the best pair up to the next level.

5. **Top:** the final combine gives the answer.

The code compares squared distances (13 instead of about 3.6), which gives the same answers without square roots or decimals. "4 above is too far" becomes "4² = 16 is not less than 13".
