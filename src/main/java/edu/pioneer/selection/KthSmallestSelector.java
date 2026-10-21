package edu.pioneer.selection;

import java.util.Arrays;

/*
 * Task C: deterministic median-of-medians selection (SDS 6).
 * Never sorts the whole input; only groups and base cases of at most five values are sorted.
 */
public final class KthSmallestSelector {

    private static final int GROUP = 5;

    private KthSmallestSelector() {
    }

    /* Returns the kth smallest value of times, with k counted from 1. The input is not modified. */
    public static int selectKthSmallest(int[] times, int k) {
        if (times == null) {
            throw new IllegalArgumentException("times must not be null");
        }
        if (times.length == 0) {
            throw new IllegalArgumentException("times must contain at least one value");
        }
        for (int i = 0; i < times.length; i++) {
            if (times[i] < 0) {
                throw new IllegalArgumentException("negative time " + times[i] + " at index " + i);
            }
        }
        if (k < 1 || k > times.length) {
            throw new IllegalArgumentException("k = " + k + " is out of range 1.." + times.length);
        }
        return select(times.clone(), k);
    }

    private static int select(int[] values, int k) {
        // Base case: few enough values to sort directly
        if (values.length <= GROUP) {
            Arrays.sort(values);
            return values[k - 1];
        }

        int pivot = medianOfMedians(values);

        // One pass into smaller / same / larger
        int smallerCount = 0;
        int sameCount = 0;
        for (int value : values) {
            if (value < pivot) {
                smallerCount++;
            } else if (value == pivot) {
                sameCount++;
            }
        }
        if (k > smallerCount && k <= smallerCount + sameCount) {
            return pivot;
        }
        boolean keepSmaller = k <= smallerCount;
        int[] kept = new int[keepSmaller ? smallerCount : values.length - smallerCount - sameCount];
        int next = 0;
        for (int value : values) {
            if (keepSmaller ? value < pivot : value > pivot) {
                kept[next++] = value;
            }
        }
        // Keeping the larger group throws away everything below it, so k shifts down
        return select(kept, keepSmaller ? k : k - smallerCount - sameCount);
    }

    private static int medianOfMedians(int[] values) {
        int groups = (values.length + GROUP - 1) / GROUP;
        int[] medians = new int[groups];
        for (int g = 0; g < groups; g++) {
            int[] group = Arrays.copyOfRange(values, g * GROUP, Math.min(values.length, (g + 1) * GROUP));
            Arrays.sort(group);
            medians[g] = group[(group.length - 1) / 2];
        }
        // Lower middle, 1-based
        return select(medians, (groups + 1) / 2);
    }
}
