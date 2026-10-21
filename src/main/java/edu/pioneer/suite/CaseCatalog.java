package edu.pioneer.suite;

import java.util.List;

/*
 * The cases for each task, in display order.
 * Each case has tests/inputs/<ID>.csv and tests/expected/<ID>.csv.
 */
public final class CaseCatalog {

    private CaseCatalog() {
    }

    public static List<TestCase> cases(Task task) {
        return switch (task) {
            case A -> List.of(
                    new TestCase("A1", "Two points"),
                    new TestCase("A2", "Six points, closest pair C and D"),
                    new TestCase("A3", "Closest pair lies in different halves of the split"),
                    new TestCase("A4", "Closest pair has the same x-coordinate"),
                    new TestCase("A5-tie", "Several pairs share the minimum distance; any one is accepted"),
                    new TestCase("A6-shared-x", "Several points share the dividing x-coordinate"),
                    new TestCase("A7-large-coords", "Coordinates at ±1,000,000,000 give the exact d² with no overflow"),
                    new TestCase("A8-one-point", "A single location is rejected"),
                    new TestCase("A9-dupe-id", "Repeated IDs are rejected"),
                    new TestCase("A10-dupe-coords", "Repeated coordinates are rejected"),
                    new TestCase("A11-out-of-range", "A coordinate outside ±1,000,000,000 is rejected"),
                    new TestCase("A12-non-integer", "A non-integer coordinate is rejected"));
            case B -> List.of(
                    new TestCase("B1", "Two inversions in a mostly ordered dispatch"),
                    new TestCase("B2", "Identical order has no inversions"),
                    new TestCase("B3", "Fully reversed order has the most inversions"),
                    new TestCase("B4", "A single delivery has no inversions"),
                    new TestCase("B5-large", "100,000 reversed IDs give a count beyond int range"),
                    new TestCase("B6-one-move", "Last planned ID dispatched first gives n - 1 inversions"),
                    new TestCase("B7-empty", "Empty sequences are rejected"),
                    new TestCase("B8-length-mismatch", "Sequences of different lengths are rejected"),
                    new TestCase("B9-dupe-id", "A repeated ID is rejected"),
                    new TestCase("B10-unknown-id", "An ID missing from planned is rejected"));
            case C -> List.of(
                    new TestCase("C1", "Fifth smallest of nine times, answer in the larger group"),
                    new TestCase("C2", "Second smallest with a repeated minimum"),
                    new TestCase("C3", "Ninth smallest is the maximum"),
                    new TestCase("C4", "All values equal"),
                    new TestCase("C5", "Smallest of two values"),
                    new TestCase("C6-sorted", "Already sorted input"),
                    new TestCase("C7-reverse", "Reverse-sorted input"),
                    new TestCase("C8-many-dupes", "100,000 values from 0-9 with heavy duplication"),
                    new TestCase("C9-empty", "An empty array is rejected"),
                    new TestCase("C10-k-zero", "k = 0 is rejected"),
                    new TestCase("C11-k-too-big", "k above n is rejected"),
                    new TestCase("C12-negative", "A negative time is rejected"));
        };
    }
}
