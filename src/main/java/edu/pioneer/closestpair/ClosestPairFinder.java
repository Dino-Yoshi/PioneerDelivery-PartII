package edu.pioneer.closestpair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * Task A: divide-and-conquer closest pair (SDS 4).
 * Sort once, split down, brute-force groups of 3 or fewer, then choose, strip, and return going up.
 * All distances are squared long values.
 */
public final class ClosestPairFinder {

    static final int LIMIT = 1_000_000_000;
    private static final int BASE_SIZE = 3;

    private ClosestPairFinder() {
    }

    /* Finds a closest pair. Throws IllegalArgumentException on invalid input; never modifies the list. */
    public static ClosestPair findClosestPair(List<Location> locations) {
        validate(locations);
        List<Location> pointsByX = new ArrayList<>(locations);
        pointsByX.sort(Comparator.comparingInt(Location::x).thenComparingInt(Location::y));
        List<Location> pointsByY = new ArrayList<>(locations);
        pointsByY.sort(Comparator.comparingInt(Location::y).thenComparingInt(Location::x));
        return closestPair(pointsByX, pointsByY);
    }

    private static void validate(List<Location> locations) {
        if (locations == null || locations.size() < 2) {
            throw new IllegalArgumentException("at least two locations are required");
        }
        Set<String> ids = new HashSet<>();
        Map<Long, String> byCoordinates = new HashMap<>();
        for (Location location : locations) {
            if (location == null || location.id() == null) {
                throw new IllegalArgumentException("null location or ID");
            }
            if (Math.abs((long) location.x()) > LIMIT || Math.abs((long) location.y()) > LIMIT) {
                throw new IllegalArgumentException("coordinate out of range for ID " + location.id() + " ("
                        + location.x() + ", " + location.y() + "); allowed range is -" + LIMIT + " to " + LIMIT);
            }
            if (!ids.add(location.id())) {
                throw new IllegalArgumentException("duplicate ID " + location.id());
            }
            long key = (location.x() + (long) LIMIT) * (2L * LIMIT + 1) + (location.y() + (long) LIMIT);
            String other = byCoordinates.put(key, location.id());
            if (other != null) {
                throw new IllegalArgumentException("duplicate coordinates (" + location.x() + ", " + location.y()
                        + ") for IDs " + other + " and " + location.id());
            }
        }
    }

    private static ClosestPair closestPair(List<Location> pointsByX, List<Location> pointsByY) {
        if (pointsByX.size() <= BASE_SIZE) {
            return bruteForce(pointsByX);
        }
        int half = pointsByX.size() / 2;
        List<Location> leftByX = pointsByX.subList(0, half);
        List<Location> rightByX = pointsByX.subList(half, pointsByX.size());
        int dividingX = leftByX.get(half - 1).x();

        // Side is recorded by identity, so points sharing dividingX go where pointsByX put them.
        Set<Location> onLeft = Collections.newSetFromMap(new IdentityHashMap<>());
        onLeft.addAll(leftByX);
        List<Location> leftByY = new ArrayList<>(half);
        List<Location> rightByY = new ArrayList<>(pointsByY.size() - half);
        for (Location point : pointsByY) {
            (onLeft.contains(point) ? leftByY : rightByY).add(point);
        }

        ClosestPair leftBest = closestPair(leftByX, leftByY);
        ClosestPair rightBest = closestPair(rightByX, rightByY);
        ClosestPair best = rightBest.squaredDistance() < leftBest.squaredDistance() ? rightBest : leftBest;
        return checkStrip(pointsByY, dividingX, best);
    }

    private static ClosestPair checkStrip(List<Location> pointsByY, int dividingX, ClosestPair best) {
        List<Location> strip = new ArrayList<>();
        for (Location point : pointsByY) {
            long dx = (long) point.x() - dividingX;
            if (dx * dx < best.squaredDistance()) {
                strip.add(point);
            }
        }
        for (int i = 0; i < strip.size(); i++) {
            Location p = strip.get(i);
            for (int j = i + 1; j < strip.size(); j++) {
                Location q = strip.get(j);
                long dy = (long) q.y() - p.y();
                if (dy * dy >= best.squaredDistance()) {
                    break;
                }
                long d = squaredDistance(p, q);
                if (d < best.squaredDistance()) {
                    best = new ClosestPair(p.id(), q.id(), d);
                }
            }
        }
        return best;
    }

    private static ClosestPair bruteForce(List<Location> points) {
        ClosestPair best = null;
        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                long d = squaredDistance(points.get(i), points.get(j));
                if (best == null || d < best.squaredDistance()) {
                    best = new ClosestPair(points.get(i).id(), points.get(j).id(), d);
                }
            }
        }
        return best;
    }

    static long squaredDistance(Location a, Location b) {
        long dx = (long) a.x() - b.x();
        long dy = (long) a.y() - b.y();
        return dx * dx + dy * dy;
    }
}
