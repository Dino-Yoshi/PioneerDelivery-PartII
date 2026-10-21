package edu.pioneer;

import edu.pioneer.closestpair.ClosestPairFinder;
import edu.pioneer.closestpair.Location;
import edu.pioneer.inversions.InversionCounter;
import edu.pioneer.selection.KthSmallestSelector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.LongSupplier;

/*
 * Timing benchmark for the Task D performance section (SDS 9). Not part of the test suite.
 * Run: ./mvnw -q -DskipTests package && java -cp target/classes edu.pioneer.Benchmark
 */
public final class Benchmark {

    static final int[] SIZES = {100, 1_000, 5_000, 10_000, 100_000};
    static final long SEED = 413;
    static final int WARMUP_RUNS = 5;
    static final int TIMED_RUNS = 11;

    /* Keeps results live so the JIT cannot drop the measured calls. */
    private static long sink;

    private Benchmark() {
    }

    public static void main(String[] args) {
        System.out.println("Pioneer Delivery Part II - Benchmark");
        System.out.printf("Seed %d, %d warm-up runs, %d timed runs per size, median reported.%n",
                SEED, WARMUP_RUNS, TIMED_RUNS);

        table("Task A: Closest Delivery Locations (O(n log n))", Benchmark::taskA);
        table("Task B: Counting Dispatch-Order Inversions (O(n log n))", Benchmark::taskB);
        table("Task C: Selecting a Delivery-Time Threshold (O(n))", Benchmark::taskC);

        if (sink == 42) {
            System.out.println();
        }
    }

    private interface Workload {
        LongSupplier prepare(int n);
    }

    private static void table(String title, Workload workload) {
        System.out.println();
        System.out.println(title);
        System.out.printf("%10s  %12s  %8s%n", "n", "median ms", "ratio");
        double previous = Double.NaN;
        for (int n : SIZES) {
            double ms = medianMillis(workload.prepare(n));
            String ratio = Double.isNaN(previous) ? "-" : String.format("%.2f", ms / previous);
            System.out.printf("%,10d  %12.3f  %8s%n", n, ms, ratio);
            previous = ms;
        }
    }

    static double medianMillis(LongSupplier run) {
        for (int i = 0; i < WARMUP_RUNS; i++) {
            sink += run.getAsLong();
        }
        long[] nanos = new long[TIMED_RUNS];
        for (int i = 0; i < TIMED_RUNS; i++) {
            long start = System.nanoTime();
            sink += run.getAsLong();
            nanos[i] = System.nanoTime() - start;
        }
        Arrays.sort(nanos);
        return nanos[TIMED_RUNS / 2] / 1_000_000.0;
    }

    /* Distinct random points, coordinates in +/-1,000,000. */
    private static LongSupplier taskA(int n) {
        Random random = new Random(SEED);
        Set<Long> seen = new HashSet<>();
        List<Location> points = new ArrayList<>(n);
        while (points.size() < n) {
            int x = random.nextInt(2_000_001) - 1_000_000;
            int y = random.nextInt(2_000_001) - 1_000_000;
            if (seen.add((long) x * 4_000_000L + y)) {
                points.add(new Location("L" + points.size(), x, y));
            }
        }
        return () -> ClosestPairFinder.findClosestPair(points).hashCode();
    }

    /* Planned order D1..Dn; actual order is a random shuffle. */
    private static LongSupplier taskB(int n) {
        List<String> planned = new ArrayList<>(n);
        for (int i = 1; i <= n; i++) {
            planned.add("D" + i);
        }
        List<String> actual = new ArrayList<>(planned);
        Collections.shuffle(actual, new Random(SEED));
        return () -> InversionCounter.countInversions(planned, actual);
    }

    /* Random values in 0..1,000,000, k = n / 2. */
    private static LongSupplier taskC(int n) {
        Random random = new Random(SEED);
        int[] times = new int[n];
        for (int i = 0; i < n; i++) {
            times[i] = random.nextInt(1_000_001);
        }
        int k = n / 2;
        return () -> KthSmallestSelector.selectKthSmallest(times, k);
    }
}
