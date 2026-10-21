package edu.pioneer.closestpair;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class ClosestPairFinderTest {

    private static Location loc(String id, int x, int y) {
        return new Location(id, x, y);
    }

    private static List<String> ids(ClosestPair pair) {
        return Arrays.asList(pair.firstId(), pair.secondId()).stream().sorted().toList();
    }

    private static String message(List<Location> input) {
        return assertThrows(IllegalArgumentException.class, () -> ClosestPairFinder.findClosestPair(input)).getMessage();
    }

    @Test
    void twoPoints() {
        ClosestPair pair = ClosestPairFinder.findClosestPair(List.of(loc("A", 0, 0), loc("B", 3, 4)));
        assertEquals(25, pair.squaredDistance());
        assertEquals(List.of("A", "B"), ids(pair));
    }

    @Test
    void closestPairCrossesTheSplit() {
        ClosestPair pair = ClosestPairFinder.findClosestPair(
                List.of(loc("A", -3, 1), loc("B", -3, 11), loc("C", 1, 1), loc("D", 2, 15)));
        assertEquals(16, pair.squaredDistance());
        assertEquals(List.of("A", "C"), ids(pair));
    }

    @Test
    void largeCoordinatesDoNotOverflow() {
        ClosestPair pair = ClosestPairFinder.findClosestPair(
                List.of(loc("A", -1_000_000_000, -1_000_000_000), loc("B", 1_000_000_000, 1_000_000_000)));
        assertEquals(8_000_000_000_000_000_000L, pair.squaredDistance());
    }

    @Test
    void doesNotModifyOrKeepTheCallersList() {
        List<Location> input = new ArrayList<>(List.of(loc("B", 5, 5), loc("A", 0, 0), loc("C", 1, 1)));
        List<Location> copy = new ArrayList<>(input);
        ClosestPairFinder.findClosestPair(input);
        assertEquals(copy, input);
    }

    @Test
    void rejectsInvalidInput() {
        assertTrue(message(List.of(loc("A", 0, 0))).contains("at least two locations"));
        assertTrue(message(List.of(loc("A", 0, 0), loc("A", 1, 1))).contains("duplicate ID"));
        assertTrue(message(List.of(loc("A", 3, 4), loc("B", 3, 4))).contains("duplicate coordinates (3, 4) for IDs A and B"));
        assertTrue(message(List.of(loc("A", 0, 0), loc("B", 1_000_000_001, 0))).contains("out of range"));
        assertTrue(message(List.of(loc("A", 0, 0), loc("B", 0, -1_000_000_001))).contains("out of range"));
        assertTrue(message(null).contains("at least two locations"));
        assertTrue(message(Arrays.asList(loc("A", 0, 0), null)).contains("null"));
    }
}
