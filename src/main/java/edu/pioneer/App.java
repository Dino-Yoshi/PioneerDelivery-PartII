package edu.pioneer;

import java.io.PrintStream;

/*
 * Command-line entry point for Part II.
 * The task command names are a design proposal, not an accepted interface.
 * Tasks A-C are not implemented yet, so every task command reports that and fails.
 */
public final class App {

    static final int EXIT_OK = 0;
    static final int EXIT_FAILURE = 1;
    static final int EXIT_USAGE = 2;

    private App() {
    }

    public static void main(String[] args) {
        System.exit(run(args, System.out, System.err));
    }

    /*
     * Runs one command and returns the process exit code.
     * Output streams are parameters so tests can capture them.
     */
    static int run(String[] args, PrintStream out, PrintStream err) {
        if (args.length == 0 || args[0].equals("help") || args[0].equals("--help")) {
            printUsage(out);
            return EXIT_OK;
        }

        String command = args[0];
        switch (command) {
            case "task-a", "task-b", "task-c" -> {
                err.println("Error: " + command + " is not implemented yet.");
                return EXIT_FAILURE;
            }
            default -> {
                err.println("Error: unknown command '" + command + "'.");
                printUsage(err);
                return EXIT_USAGE;
            }
        }
    }

    private static void printUsage(PrintStream stream) {
        stream.println("Pioneer Delivery Part II: Divide and Conquer");
        stream.println();
        stream.println("Usage: java -jar pioneer-delivery-part2.jar <command> [arguments]");
        stream.println();
        stream.println("Commands (not implemented yet):");
        stream.println("  task-a <locations.csv>      Closest delivery locations");
        stream.println("  task-b <dispatch.csv>       Dispatch-order inversion count");
        stream.println("  task-c <times.csv> <k>      kth-smallest delivery time");
        stream.println("  help                        Show this message");
    }
}
