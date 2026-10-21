package edu.pioneer.suite;

import java.util.List;

/*
 * A task result or an expected-file content, in the shape the suite compares.
 * Task A pair IDs are kept in alphabetical order inside each pair.
 */
public sealed interface Outcome {

    /* Task A: d² and one or more acceptable pairs. A returned result holds exactly one pair. */
    record PairOutcome(long squaredDistance, List<List<String>> pairs) implements Outcome {
    }

    /* Task B (label "inversions") or Task C (label "value"). */
    record NumberOutcome(String label, long value) implements Outcome {
    }

    /* An expected error holds the words the message must contain; an actual one holds the message. */
    record ErrorOutcome(String text) implements Outcome {
    }
}
