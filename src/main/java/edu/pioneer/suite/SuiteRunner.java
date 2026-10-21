package edu.pioneer.suite;

import edu.pioneer.inversions.InversionCounter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/*
 * Runs a task's cases and returns one CheckResult per case.
 * Paths are relative to the project root.
 */
public final class SuiteRunner {

    static final Path INPUTS = Path.of("tests", "inputs");
    static final Path EXPECTED = Path.of("tests", "expected");

    private SuiteRunner() {
    }

    public static List<CheckResult> run(Task task) {
        List<CheckResult> results = new ArrayList<>();
        for (TestCase testCase : CaseCatalog.cases(task)) {
            Outcome expected = FixtureReader.readExpected(EXPECTED.resolve(testCase.id() + ".csv"));
            Path input = INPUTS.resolve(testCase.id() + ".csv");
            results.add(evaluate(testCase, expected, () -> execute(task, input)));
        }
        if (task == Task.B) {
            results.add(InversionRefCheck.run());
        }
        return results;
    }

    /* Reads the input file and calls the task. Each task ticket adds its branch here. */
    private static Outcome execute(Task task, Path input) {
        if (task == Task.B) {
            FixtureReader.DispatchInput dispatch = FixtureReader.readDispatch(input);
            return new Outcome.NumberOutcome("inversions",
                    InversionCounter.countInversions(dispatch.planned(), dispatch.actual()));
        }
        throw new IllegalStateException("Task " + task + " is not wired into the suite yet");
    }

    /*
     * Compares the expected outcome with what the task produces.
     * An IllegalArgumentException from the reader or the task counts as an error outcome.
     */
    static CheckResult evaluate(TestCase testCase, Outcome expected, Supplier<Outcome> task) {
        Outcome actual;
        try {
            actual = task.get();
        } catch (IllegalArgumentException e) {
            actual = new Outcome.ErrorOutcome(e.getMessage());
        }
        return new CheckResult(testCase.id(), testCase.description(),
                describeExpected(expected), describeActual(actual), matches(expected, actual));
    }

    static boolean matches(Outcome expected, Outcome actual) {
        return switch (expected) {
            case Outcome.PairOutcome e -> actual instanceof Outcome.PairOutcome a
                    && e.squaredDistance() == a.squaredDistance()
                    && e.pairs().containsAll(a.pairs());
            case Outcome.NumberOutcome e -> actual instanceof Outcome.NumberOutcome a
                    && e.label().equals(a.label()) && e.value() == a.value();
            case Outcome.ErrorOutcome e -> actual instanceof Outcome.ErrorOutcome a
                    && a.text() != null && a.text().contains(e.text());
        };
    }

    static String describeExpected(Outcome outcome) {
        return switch (outcome) {
            case Outcome.PairOutcome p -> String.join(" or ", p.pairs().stream().map(SuiteRunner::pairText).toList())
                    + "; d² = " + p.squaredDistance();
            case Outcome.NumberOutcome n -> n.label() + " = " + n.value();
            case Outcome.ErrorOutcome e -> "error containing \"" + e.text() + "\"";
        };
    }

    static String describeActual(Outcome outcome) {
        return switch (outcome) {
            case Outcome.PairOutcome p -> pairText(p.pairs().get(0)) + "; d² = " + p.squaredDistance();
            case Outcome.NumberOutcome n -> n.label() + " = " + n.value();
            case Outcome.ErrorOutcome e -> "error: " + e.text();
        };
    }

    private static String pairText(List<String> pair) {
        return String.join(", ", pair);
    }
}
