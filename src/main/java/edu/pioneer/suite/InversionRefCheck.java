package edu.pioneer.suite;

import edu.pioneer.inversions.InversionCounter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/* B-ref: random shuffles checked against two nested loops (SDS 7.4). Testing only. */
final class InversionRefCheck {

    private InversionRefCheck() {
    }

    static CheckResult run() {
        Random random = new Random(413);
        for (int round = 0; round < 500; round++) {
            int n = 1 + random.nextInt(40);
            List<String> planned = new ArrayList<>();
            for (int i = 1; i <= n; i++) {
                planned.add("D" + i);
            }
            List<String> actual = new ArrayList<>(planned);
            Collections.shuffle(actual, random);
            long expected = nestedLoops(planned, actual);
            long got = InversionCounter.countInversions(planned, actual);
            if (expected != got) {
                return new CheckResult("B-ref", "500 random inputs match two nested loops",
                        "inversions = " + expected, "inversions = " + got + " for planned " + planned
                                + ", actual " + actual, false);
            }
        }
        return new CheckResult("B-ref", "500 random inputs match two nested loops",
                "500 inputs agree", "500 inputs agree", true);
    }

    static long nestedLoops(List<String> planned, List<String> actual) {
        long count = 0;
        for (int i = 0; i < actual.size(); i++) {
            for (int j = i + 1; j < actual.size(); j++) {
                if (planned.indexOf(actual.get(i)) > planned.indexOf(actual.get(j))) {
                    count++;
                }
            }
        }
        return count;
    }
}
