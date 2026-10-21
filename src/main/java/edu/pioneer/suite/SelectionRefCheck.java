package edu.pioneer.suite;

import edu.pioneer.selection.KthSmallestSelector;
import java.util.Arrays;
import java.util.Random;

/* C-ref: random inputs checked against a full sort, then index k - 1 (SDS 7.4). Testing only. */
final class SelectionRefCheck {

    private SelectionRefCheck() {
    }

    static CheckResult run() {
        String description = "500 random inputs match full sort";
        Random random = new Random(413);
        for (int round = 0; round < 500; round++) {
            int[] times = new int[1 + random.nextInt(40)];
            for (int i = 0; i < times.length; i++) {
                times[i] = random.nextInt(11);
            }
            int k = 1 + random.nextInt(times.length);
            int[] sorted = times.clone();
            Arrays.sort(sorted);
            int expected = sorted[k - 1];
            int got = KthSmallestSelector.selectKthSmallest(times, k);
            if (expected != got) {
                return new CheckResult("C-ref", description, "value = " + expected,
                        "value = " + got + " for k = " + k + ", times " + Arrays.toString(times), false);
            }
        }
        return new CheckResult("C-ref", description, "500 inputs agree", "500 inputs agree", true);
    }
}
