package edu.pioneer;

import edu.pioneer.suite.ResultPrinter;
import edu.pioneer.suite.Task;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/*
 * Looping menu that runs the test suite (SDS 8.1).
 */
public final class App {

    private App() {
    }

    public static void main(String[] args) {
        System.exit(run(System.in, System.out));
    }

    /* Runs the menu until exit or end of input, and returns the process exit code. */
    static int run(InputStream in, PrintStream out) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        try {
            while (true) {
                printMenu(out);
                String line = reader.readLine();
                if (line == null) {
                    out.println();
                    return 0;
                }
                String choice = line.trim();
                switch (choice) {
                    case "0" -> {
                        return 0;
                    }
                    case "1" -> ResultPrinter.printTasks(out, List.of(Task.values()));
                    case "2" -> ResultPrinter.printTasks(out, List.of(Task.A));
                    case "3" -> ResultPrinter.printTasks(out, List.of(Task.B));
                    case "4" -> ResultPrinter.printTasks(out, List.of(Task.C));
                    default -> out.println("Unknown choice: " + choice);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void printMenu(PrintStream out) {
        out.println("Pioneer Delivery Part II — Test Suite");
        out.println("  1  Run all tests");
        out.println("  2  Task A: " + Task.A.title());
        out.println("  3  Task B: " + Task.B.title());
        out.println("  4  Task C: " + Task.C.title());
        out.println("  0  Exit");
        out.print("Choice: ");
        out.flush();
    }
}
