package edu.pioneer.suite;

import java.io.PrintStream;
import java.util.List;

public final class ResultPrinter {

    private ResultPrinter() {
    }

    /* Prints one task's section and returns its results' failure count. */
    public static int printTask(PrintStream out, Task task, List<CheckResult> results) {
        out.println("== Task " + task + ": " + task.title() + " ==");
        for (CheckResult result : results) {
            out.println("[" + (result.passed() ? "PASS" : "FAIL") + "] " + result.id() + "  " + result.description());
            out.println("       expected: " + result.expected());
            out.println("       actual:   " + result.actual());
        }
        int failed = countFailed(results);
        out.println("Task " + task + ": " + (results.size() - failed) + " passed, " + failed + " failed");
        return failed;
    }

    /* Runs and prints the given tasks; with more than one, ends with an overall total. */
    public static void printTasks(PrintStream out, List<Task> tasks) {
        int passed = 0;
        int failed = 0;
        for (Task task : tasks) {
            List<CheckResult> results = SuiteRunner.run(task);
            int taskFailed = printTask(out, task, results);
            failed += taskFailed;
            passed += results.size() - taskFailed;
            out.println();
        }
        if (tasks.size() > 1) {
            out.println("Total: " + passed + " passed, " + failed + " failed");
            out.println();
        }
    }

    private static int countFailed(List<CheckResult> results) {
        return (int) results.stream().filter(result -> !result.passed()).count();
    }
}
