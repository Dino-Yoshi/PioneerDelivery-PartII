package edu.pioneer.suite;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.pioneer.closestpair.Location;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FixtureReaderTest {

    @TempDir
    Path dir;

    private Path file(String content) throws IOException {
        return Files.writeString(dir.resolve("f.csv"), content);
    }

    @Test
    void readsLocations() throws IOException {
        List<Location> locations = FixtureReader.readLocations(file("id,x,y\nA,-3,1\nB,-3,11\n"));
        assertEquals(List.of(new Location("A", -3, 1), new Location("B", -3, 11)), locations);
    }

    @Test
    void nonIntegerCoordinateIsRejected() throws IOException {
        Path f = file("id,x,y\nA,1.5,2\n");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> FixtureReader.readLocations(f));
        assertEquals(true, e.getMessage().contains("not an integer"));
    }

    @Test
    void readsDispatchRows() throws IOException {
        FixtureReader.DispatchInput input = FixtureReader.readDispatch(file("planned,A,B,C\nactual,B,A,C\n"));
        assertEquals(List.of("A", "B", "C"), input.planned());
        assertEquals(List.of("B", "A", "C"), input.actual());
    }

    @Test
    void readsSelectionRows() throws IOException {
        FixtureReader.SelectionInput input = FixtureReader.readSelection(file("k,5\ntimes,18,4,29\n"));
        assertEquals(5, input.k());
        assertArrayEquals(new int[] {18, 4, 29}, input.times());
    }

    @Test
    void nonIntegerSelectionValuesAreRejected() throws IOException {
        Path badK = file("k,five\ntimes,1,2\n");
        assertThrows(IllegalArgumentException.class, () -> FixtureReader.readSelection(badK));
        Path badTime = file("k,1\ntimes,1,x\n");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> FixtureReader.readSelection(badTime));
        assertEquals(true, e.getMessage().contains("not an integer"));
    }

    @Test
    void readsPairExpectedWithTies() throws IOException {
        Outcome outcome = FixtureReader.readExpected(file("squared_distance,1\npair,Q,P\npair,R,S\n"));
        assertEquals(new Outcome.PairOutcome(1, List.of(List.of("P", "Q"), List.of("R", "S"))), outcome);
    }

    @Test
    void readsNumberExpected() throws IOException {
        assertEquals(new Outcome.NumberOutcome("inversions", 2), FixtureReader.readExpected(file("inversions,2\n")));
        assertEquals(new Outcome.NumberOutcome("value", 16), FixtureReader.readExpected(file("value,16\n")));
    }

    @Test
    void readsErrorExpected() throws IOException {
        assertEquals(new Outcome.ErrorOutcome("duplicate coordinates"),
                FixtureReader.readExpected(file("error,duplicate coordinates\n")));
    }

    @Test
    void nonIntegerExpectedValueIsRejected() throws IOException {
        Path f = file("value,abc\n");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> FixtureReader.readExpected(f));
        assertEquals(true, e.getMessage().contains("not an integer"));
    }

    @Test
    void missingFileIsAnError() {
        assertThrows(UncheckedIOException.class, () -> FixtureReader.readLocations(dir.resolve("none.csv")));
    }
}
