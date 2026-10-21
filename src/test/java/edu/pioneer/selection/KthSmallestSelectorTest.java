package edu.pioneer.selection;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class KthSmallestSelectorTest {

    @Test
    void worksExampleFromSds() {
        assertEquals(16, KthSmallestSelector.selectKthSmallest(new int[] {18, 4, 29, 11, 4, 16, 35, 9, 22}, 5));
    }

    @Test
    void doesNotModifyInput() {
        int[] times = {9, 3, 7, 1, 8, 2, 6};
        KthSmallestSelector.selectKthSmallest(times, 4);
        assertArrayEquals(new int[] {9, 3, 7, 1, 8, 2, 6}, times);
    }

    @Test
    void rejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> KthSmallestSelector.selectKthSmallest(null, 1));
        assertThrows(IllegalArgumentException.class, () -> KthSmallestSelector.selectKthSmallest(new int[0], 1));
        assertThrows(IllegalArgumentException.class, () -> KthSmallestSelector.selectKthSmallest(new int[] {1}, 2));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> KthSmallestSelector.selectKthSmallest(new int[] {1, -2}, 1));
        assertEquals("negative time -2 at index 1", e.getMessage());
    }
}
