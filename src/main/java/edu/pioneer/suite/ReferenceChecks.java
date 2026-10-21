package edu.pioneer.suite;

import edu.pioneer.closestpair.ClosestPair;
import edu.pioneer.closestpair.ClosestPairFinder;
import edu.pioneer.closestpair.Location;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/* Simple reference methods and the random checks that compare the algorithms with them (SDS 7.4). Testing only. */
public final class ReferenceChecks {

    static final long SEED = 413;
    static final int INPUTS = 500;

    private ReferenceChecks() {
    }

    /* A-ref: random distinct points in -20..20 against all-pairs brute force. */
    public static CheckResult closestPair() {
        String description = INPUTS + " random inputs match brute force";
        Random random = new Random(SEED);
        for (int i = 0; i < INPUTS; i++) {
            List<Location> points = randomPoints(random);
            long expected = bruteForceMinimum(points);
            ClosestPair actual = ClosestPairFinder.findClosestPair(points);
            if (actual.squaredDistance() != expected || !pairIsAtDistance(points, actual)) {
                return new CheckResult("A-ref", "Random input " + (i + 1) + " disagrees with brute force: " + points,
                        "d² = " + expected,
                        actual.firstId() + ", " + actual.secondId() + "; d² = " + actual.squaredDistance(), false);
            }
        }
        return new CheckResult("A-ref", description, "d² equals the brute-force minimum", "all " + INPUTS + " match", true);
    }

    static List<Location> randomPoints(Random random) {
        int n = 2 + random.nextInt(39);
        Set<Integer> used = new HashSet<>();
        List<Location> points = new ArrayList<>();
        while (points.size() < n) {
            int x = random.nextInt(41) - 20;
            int y = random.nextInt(41) - 20;
            if (used.add(x * 100 + y)) {
                points.add(new Location("P" + points.size(), x, y));
            }
        }
        return points;
    }

    static long bruteForceMinimum(List<Location> points) {
        long best = Long.MAX_VALUE;
        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                best = Math.min(best, distance(points.get(i), points.get(j)));
            }
        }
        return best;
    }

    private static boolean pairIsAtDistance(List<Location> points, ClosestPair pair) {
        Location first = find(points, pair.firstId());
        Location second = find(points, pair.secondId());
        return first != null && second != null && first != second && distance(first, second) == pair.squaredDistance();
    }

    private static Location find(List<Location> points, String id) {
        return points.stream().filter(p -> p.id().equals(id)).findFirst().orElse(null);
    }

    private static long distance(Location a, Location b) {
        long dx = (long) a.x() - b.x();
        long dy = (long) a.y() - b.y();
        return dx * dx + dy * dy;
    }
}
