package edu.pioneer.suite;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/* JUnit entry: one dynamic test per suite case (SDS 7.1). */
class SuiteTest {

    @TestFactory
    Stream<DynamicTest> suiteCases() {
        return Arrays.stream(Task.values())
                .flatMap(task -> SuiteRunner.run(task).stream())
                .map(result -> DynamicTest.dynamicTest(result.id() + " " + result.description(),
                        () -> assertTrue(result.passed(),
                                "expected: " + result.expected() + System.lineSeparator()
                                        + "actual:   " + result.actual())));
    }
}
