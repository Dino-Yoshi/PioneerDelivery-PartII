package edu.pioneer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class AppTest {

    private final ByteArrayOutputStream captured = new ByteArrayOutputStream();

    private int run(String input) {
        return App.run(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(captured, true, StandardCharsets.UTF_8));
    }

    private String output() {
        return captured.toString(StandardCharsets.UTF_8);
    }

    private static int count(String text, String part) {
        return text.split(java.util.regex.Pattern.quote(part), -1).length - 1;
    }

    @Test
    void zeroExitsWithStatusZero() {
        assertEquals(0, run("0\n"));
        assertEquals(1, count(output(), "Pioneer Delivery Part II — Test Suite"));
    }

    @Test
    void endOfInputExitsWithStatusZero() {
        assertEquals(0, run(""));
    }

    @Test
    void unknownChoiceIsReportedAndMenuReturns() {
        assertEquals(0, run("9\nhello\n0\n"));
        assertTrue(output().contains("Unknown choice: 9"));
        assertTrue(output().contains("Unknown choice: hello"));
        assertEquals(3, count(output(), "Pioneer Delivery Part II — Test Suite"));
    }

    @Test
    void taskOptionsPrintTheirSummaryAndShowTheMenuAgain() {
        assertEquals(0, run("2\n3\n4\n0\n"));
        String out = output();
        assertTrue(out.contains("Task A: 0 passed, 0 failed"));
        assertTrue(out.contains("Task B: 11 passed, 0 failed"));
        assertTrue(out.contains("Task C: 0 passed, 0 failed"));
        assertEquals(4, count(out, "Pioneer Delivery Part II — Test Suite"));
    }

    @Test
    void fullSuitePrintsEachSectionAndATotal() {
        assertEquals(0, run("1\n0\n"));
        String out = output();
        assertTrue(out.contains("== Task A: Closest Delivery Locations =="));
        assertTrue(out.contains("== Task B: Counting Dispatch-Order Inversions =="));
        assertTrue(out.contains("== Task C: Selecting a Delivery-Time Threshold =="));
        assertTrue(out.contains(", 0 failed") && out.contains("Total: "));
    }
}
