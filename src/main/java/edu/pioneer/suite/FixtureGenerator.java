package edu.pioneer.suite;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*
 * One-time generator for the big fixtures (SDS 7.5). The generated files are committed.
 * Run from the project root: java -cp target/classes edu.pioneer.suite.FixtureGenerator
 */
public final class FixtureGenerator {

    private FixtureGenerator() {
    }

    public static void main(String[] args) {
        writeB5Large();
        writeC8ManyDupes();
    }

    /* B5-large: 100,000 IDs fully reversed give n(n - 1)/2 = 4,999,950,000 inversions. */
    static void writeB5Large() {
        int n = 100_000;
        String planned = IntStream.rangeClosed(1, n).mapToObj(i -> "D" + i).collect(Collectors.joining(","));
        String actual = IntStream.rangeClosed(1, n).mapToObj(i -> "D" + (n + 1 - i)).collect(Collectors.joining(","));
        write("B5-large", "planned," + planned + "\nactual," + actual, "inversions," + (long) n * (n - 1) / 2);
    }

    /* C8-many-dupes: 100,000 values in 0-9 (seed 413), k = 50,000; expected from a full sort. */
    static void writeC8ManyDupes() {
        int n = 100_000;
        int k = 50_000;
        Random random = new Random(413);
        int[] times = new int[n];
        for (int i = 0; i < n; i++) {
            times[i] = random.nextInt(10);
        }
        int[] sorted = times.clone();
        Arrays.sort(sorted);
        String row = Arrays.stream(times).mapToObj(Integer::toString).collect(Collectors.joining(","));
        write("C8-many-dupes", "k," + k + "\ntimes," + row, "value," + sorted[k - 1]);
    }

    static void write(String id, String input, String expected) {
        try {
            Files.writeString(SuiteRunner.INPUTS.resolve(id + ".csv"), input + "\n");
            Files.writeString(SuiteRunner.EXPECTED.resolve(id + ".csv"), expected + "\n");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
