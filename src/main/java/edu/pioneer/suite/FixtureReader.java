package edu.pioneer.suite;

import edu.pioneer.closestpair.Location;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Reads the CSV input and expected files (SDS 7.2).
 * A malformed value throws IllegalArgumentException; an unreadable file throws UncheckedIOException.
 */
public final class FixtureReader {

    public record DispatchInput(List<String> planned, List<String> actual) {
    }

    public record SelectionInput(int[] times, int k) {
    }

    private FixtureReader() {
    }

    /* Task A: a header row, then id,x,y per row. */
    public static List<Location> readLocations(Path file) {
        List<String> lines = readLines(file);
        if (lines.isEmpty() || !lines.get(0).trim().equals("id,x,y")) {
            throw new IllegalArgumentException("expected header id,x,y in " + file);
        }
        List<Location> locations = new ArrayList<>();
        for (String line : lines.subList(1, lines.size())) {
            String[] fields = split(line);
            if (fields.length != 3) {
                throw new IllegalArgumentException("expected id,x,y but found '" + line + "'");
            }
            locations.add(new Location(fields[0], parseInt(fields[1]), parseInt(fields[2])));
        }
        return locations;
    }

    /* Task B: a planned row and an actual row. */
    public static DispatchInput readDispatch(Path file) {
        List<String> lines = readLines(file);
        if (lines.size() != 2) {
            throw new IllegalArgumentException("expected a planned row and an actual row in " + file);
        }
        return new DispatchInput(labeledValues(lines.get(0), "planned"), labeledValues(lines.get(1), "actual"));
    }

    /* Task C: a k row and a times row. */
    public static SelectionInput readSelection(Path file) {
        List<String> lines = readLines(file);
        if (lines.size() != 2) {
            throw new IllegalArgumentException("expected a k row and a times row in " + file);
        }
        List<String> kValues = labeledValues(lines.get(0), "k");
        if (kValues.size() != 1) {
            throw new IllegalArgumentException("expected exactly one value in the k row");
        }
        int[] times = labeledValues(lines.get(1), "times").stream().mapToInt(FixtureReader::parseInt).toArray();
        return new SelectionInput(times, parseInt(kValues.get(0)));
    }

    /* Expected files: squared_distance and pair rows (Task A), inversions or value (B, C), or error. */
    public static Outcome readExpected(Path file) {
        List<String> lines = readLines(file);
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("empty expected file " + file);
        }
        String[] first = split(lines.get(0));
        String label = first[0];
        switch (label) {
            case "squared_distance" -> {
                if (first.length != 2) {
                    throw new IllegalArgumentException("expected squared_distance,<value>");
                }
                List<List<String>> pairs = new ArrayList<>();
                for (String line : lines.subList(1, lines.size())) {
                    String[] fields = split(line);
                    if (!fields[0].equals("pair") || fields.length != 3) {
                        throw new IllegalArgumentException("expected pair,<id>,<id> but found '" + line + "'");
                    }
                    pairs.add(sortedPair(fields[1], fields[2]));
                }
                if (pairs.isEmpty()) {
                    throw new IllegalArgumentException("expected at least one pair row in " + file);
                }
                return new Outcome.PairOutcome(parseLong(first[1]), pairs);
            }
            case "inversions", "value" -> {
                if (first.length != 2) {
                    throw new IllegalArgumentException("expected " + label + ",<value>");
                }
                return new Outcome.NumberOutcome(label, parseLong(first[1]));
            }
            case "error" -> {
                return new Outcome.ErrorOutcome(lines.get(0).substring("error".length()).replaceFirst("^,", "").trim());
            }
            default -> throw new IllegalArgumentException("unknown expected label '" + label + "'");
        }
    }

    static List<String> sortedPair(String first, String second) {
        return first.compareTo(second) <= 0 ? List.of(first, second) : List.of(second, first);
    }

    private static List<String> labeledValues(String line, String label) {
        String[] fields = split(line);
        if (!fields[0].equals(label)) {
            throw new IllegalArgumentException("expected a row starting with '" + label + "' but found '" + line + "'");
        }
        return Arrays.asList(fields).subList(1, fields.length);
    }

    private static String[] split(String line) {
        String[] fields = line.split(",", -1);
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }
        return fields;
    }

    private static List<String> readLines(Path file) {
        try {
            return Files.readAllLines(file).stream().filter(line -> !line.isBlank()).toList();
        } catch (IOException e) {
            throw new UncheckedIOException("cannot read " + file, e);
        }
    }

    private static int parseInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + text + "' is not an integer");
        }
    }

    private static long parseLong(String text) {
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + text + "' is not an integer");
        }
    }
}
