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
            case A -> List.of();
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
