package edu.pioneer.inversions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Task B: counts dispatch-order inversions (SDS 5).
 * Replaces each ID with its planned position, then counts out-of-order pairs while merge sorting.
 */
public final class InversionCounter {

    private InversionCounter() {
    }

    /* Returns the number of pairs whose order in actual is the reverse of their order in planned. */
    public static long countInversions(List<String> planned, List<String> actual) {
        if (planned == null || actual == null) {
            throw new IllegalArgumentException("planned and actual must not be null");
        }
        if (planned.isEmpty() || actual.isEmpty()) {
            throw new IllegalArgumentException("planned and actual must each contain at least one ID");
        }
        if (planned.size() != actual.size()) {
            throw new IllegalArgumentException("planned and actual have different lengths ("
                    + planned.size() + " and " + actual.size() + ")");
        }

        Map<String, Integer> rankOf = new HashMap<>();
        for (String id : planned) {
            if (id == null) {
                throw new IllegalArgumentException("planned contains a null ID");
            }
            if (rankOf.put(id, rankOf.size() + 1) != null) {
                throw new IllegalArgumentException("duplicate ID " + id + " in planned");
            }
        }

        int[] ranks = new int[actual.size()];
        boolean[] seen = new boolean[planned.size() + 1];
        for (int i = 0; i < ranks.length; i++) {
            String id = actual.get(i);
            if (id == null) {
                throw new IllegalArgumentException("actual contains a null ID");
            }
            Integer rank = rankOf.get(id);
            if (rank == null) {
                throw new IllegalArgumentException("ID " + id + " in actual is not in planned");
            }
            if (seen[rank]) {
                throw new IllegalArgumentException("duplicate ID " + id + " in actual");
            }
            seen[rank] = true;
            ranks[i] = rank;
        }

        return sortAndCount(ranks, new int[ranks.length], 0, ranks.length);
    }

    /* Sorts ranks[from, to) and returns how many inversions it contained. */
    private static long sortAndCount(int[] ranks, int[] buffer, int from, int to) {
        if (to - from <= 1) {
            return 0;
        }
        int middle = (from + to) >>> 1;
        long count = sortAndCount(ranks, buffer, from, middle) + sortAndCount(ranks, buffer, middle, to);
        return count + mergeAndCount(ranks, buffer, from, middle, to);
    }

    /* Merges the sorted ranges [from, middle) and [middle, to), counting crossing inversions. */
    private static long mergeAndCount(int[] ranks, int[] buffer, int from, int middle, int to) {
        long count = 0;
        int left = from;
        int right = middle;
        int out = from;
        while (left < middle && right < to) {
            if (ranks[left] <= ranks[right]) {
                buffer[out++] = ranks[left++];
            } else {
                buffer[out++] = ranks[right++];
                count += middle - left;
            }
        }
        while (left < middle) {
            buffer[out++] = ranks[left++];
        }
        while (right < to) {
            buffer[out++] = ranks[right++];
        }
        System.arraycopy(buffer, from, ranks, from, to - from);
        return count;
    }
}
