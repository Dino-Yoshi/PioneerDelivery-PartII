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
            case C -> List.of();
        };
    }
}
