package edu.pioneer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/*
 * Checks the placeholder CLI only. No algorithm behavior is tested here.
 */
class AppTest {

    private final ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
    private final PrintStream out = new PrintStream(outBuffer, true, StandardCharsets.UTF_8);
    private final PrintStream err = new PrintStream(errBuffer, true, StandardCharsets.UTF_8);

    @Test
    void noArgumentsPrintsUsage() {
        int exitCode = App.run(new String[0], out, err);

        assertEquals(App.EXIT_OK, exitCode);
        assertTrue(outBuffer.toString(StandardCharsets.UTF_8).contains("Usage:"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"task-a", "task-b", "task-c"})
    void taskCommandsReportNotImplemented(String command) {
        int exitCode = App.run(new String[] {command}, out, err);

        assertEquals(App.EXIT_FAILURE, exitCode);
        assertTrue(errBuffer.toString(StandardCharsets.UTF_8).contains("not implemented"));
        assertEquals("", outBuffer.toString(StandardCharsets.UTF_8));
    }

    @Test
    void unknownCommandIsUsageError() {
        int exitCode = App.run(new String[] {"task-z"}, out, err);

        assertEquals(App.EXIT_USAGE, exitCode);
        assertTrue(errBuffer.toString(StandardCharsets.UTF_8).contains("unknown command"));
    }
}
