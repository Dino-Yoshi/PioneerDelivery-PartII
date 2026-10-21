package edu.pioneer.inversions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class InversionCounterTest {

    @Test
    void countsExampleFromSds() {
        assertEquals(2, InversionCounter.countInversions(
                List.of("D1", "D2", "D3", "D4", "D5"), List.of("D1", "D3", "D4", "D2", "D5")));
    }

    @Test
    void doesNotModifyInputs() {
        List<String> planned = List.of("A", "B", "C");
        List<String> actual = List.of("C", "B", "A");
        assertEquals(3, InversionCounter.countInversions(planned, actual));
        assertEquals(List.of("C", "B", "A"), actual);
    }
}
