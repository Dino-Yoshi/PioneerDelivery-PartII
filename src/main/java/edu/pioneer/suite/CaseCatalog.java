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
            case B -> List.of();
            case C -> List.of();
        };
    }
}
