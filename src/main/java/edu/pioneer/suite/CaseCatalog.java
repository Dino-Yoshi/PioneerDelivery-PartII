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
                    new TestCase("B4", "A single delivery has no inversions"));
            case C -> List.of();
        };
    }
}
