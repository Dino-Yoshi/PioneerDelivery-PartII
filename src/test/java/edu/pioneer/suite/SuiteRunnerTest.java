package edu.pioneer.suite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class SuiteRunnerTest {

    private static final TestCase CASE = new TestCase("X1", "demo");

    @Test
    void errorCasePassesWhenTheTaskThrowsMatchingMessage() {
        CheckResult result = SuiteRunner.evaluate(CASE, new Outcome.ErrorOutcome("duplicate coordinates"), () -> {
            throw new IllegalArgumentException("duplicate coordinates (3, 4) for IDs B and E");
        });
        assertTrue(result.passed());
        assertEquals("error containing \"duplicate coordinates\"", result.expected());
        assertEquals("error: duplicate coordinates (3, 4) for IDs B and E", result.actual());
    }

    @Test
    void errorCaseFailsOnDifferentMessageOrNoError() {
        assertFalse(SuiteRunner.evaluate(CASE, new Outcome.ErrorOutcome("out of range"), () -> {
            throw new IllegalArgumentException("duplicate ID");
        }).passed());
        assertFalse(SuiteRunner.evaluate(CASE, new Outcome.ErrorOutcome("out of range"),
                () -> new Outcome.NumberOutcome("value", 1)).passed());
    }

    @Test
    void thrownErrorFailsANonErrorCase() {
        assertFalse(SuiteRunner.evaluate(CASE, new Outcome.NumberOutcome("value", 1), () -> {
            throw new IllegalArgumentException("negative time");
        }).passed());
    }

    @Test
    void pairCaseAcceptsEitherOrderAndAnyListedPair() {
        Outcome expected = new Outcome.PairOutcome(1, List.of(List.of("P", "Q"), List.of("R", "S")));
        assertTrue(SuiteRunner.matches(expected, new Outcome.PairOutcome(1, List.of(List.of("R", "S")))));
        assertFalse(SuiteRunner.matches(expected, new Outcome.PairOutcome(1, List.of(List.of("P", "R")))));
        assertFalse(SuiteRunner.matches(expected, new Outcome.PairOutcome(2, List.of(List.of("P", "Q")))));
    }

    @Test
    void numberCaseComparesValue() {
        assertTrue(SuiteRunner.matches(new Outcome.NumberOutcome("value", 16), new Outcome.NumberOutcome("value", 16)));
        assertFalse(SuiteRunner.matches(new Outcome.NumberOutcome("value", 16), new Outcome.NumberOutcome("value", 17)));
    }

    @Test
    void tasksWithoutCasesRunWithNoResults() {
        for (Task task : List.of(Task.B, Task.C)) {
            assertTrue(SuiteRunner.run(task).isEmpty());
        }
    }
}
