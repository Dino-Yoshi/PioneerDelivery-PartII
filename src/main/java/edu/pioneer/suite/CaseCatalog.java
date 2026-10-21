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
            case B -> List.of();
            case C -> List.of();
        };
    }
}
