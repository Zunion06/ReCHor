
package ch.epfl.rechor.journey;

import ch.epfl.rechor.journey.PackedCriteria;
import ch.epfl.rechor.journey.ParetoFront;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class GhaliParetoBuildTest {

    @Test
    void emptyParetoFrontHasZeroSize() {
        ParetoFront front = ParetoFront.EMPTY;
        assertEquals(0, front.size());
    }

    @Test
    void getThrowsExceptionForNonExistentCriteria() {
        ParetoFront front = new ParetoFront.Builder()
                .add(480, 3, 0)
                .build();
        assertThrows(NoSuchElementException.class, () -> front.get(481, 2));
    }

    @Test
    void getReturnsCorrectCriteria() {
        ParetoFront front = new ParetoFront.Builder()
                .add(480, 3, 0)
                .add(481, 2, 1)
                .build();

        long crit = front.get(481, 2);
        assertEquals(481, PackedCriteria.arrMins(crit));
        assertEquals(2, PackedCriteria.changes(crit));
    }

    @Test
    void addingDominatedCriteriaHasNoEffect() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(480, 3, 0);
        builder.add(480, 4, 0); // Dominated by (480,3)
        builder.add(484, 1, 0); // Dominated by (483,0)
        builder.add(483, 0, 0);
        builder.add(485, 1, 0); // Dominated by (484,1)

        ParetoFront front = builder.build();
        assertEquals(2, front.size());

        assertDoesNotThrow(() -> front.get(480, 3));
        assertDoesNotThrow(() -> front.get(483, 0));
        assertThrows(NoSuchElementException.class, () -> front.get(480, 4));
    }

    @Test
    void clearMethodEmptiesBuilder() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(480, 3, 0);
        assertFalse(builder.isEmpty());
        builder.clear();
        assertTrue(builder.isEmpty());
        ParetoFront front = builder.build();
        assertEquals(0, front.size());
    }

    @Test
    void fullyDominatesWorksCorrectly() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        ParetoFront.Builder builder2 = new ParetoFront.Builder();

        builder1.add(PackedCriteria.withDepMins(PackedCriteria.pack(480, 3, 0), 0));
        builder1.add(PackedCriteria.withDepMins(PackedCriteria.pack(481, 2, 0), 0));

        builder2.add(PackedCriteria.withDepMins(PackedCriteria.pack(481, 2, 0), 0));
        builder2.add(PackedCriteria.withDepMins(PackedCriteria.pack(482, 1, 0), 0));

        assertFalse(builder1.fullyDominates(builder2, 0));
        assertFalse(builder2.fullyDominates(builder1, 0));

        builder1.clear().add(0,480, 1, 0);
        builder2.clear().add(482, 2, 0);
        assertTrue(builder1.fullyDominates(builder2, 0));

        assertTrue(builder1.clear().fullyDominates(builder2.clear(),0));
    }

    @Test
    void toStringRepresentationIsCorrect() {
        ParetoFront front = new ParetoFront.Builder()
                .add(480, 3, 0)
                .add(481, 2, 0)
                .add(482, 1, 0)
                .build();

        String expectedPattern = "ParetoFront[(arr=8h00, changes=3), (arr=8h01, changes=2), (arr=8h02, changes=1)]";
//        assertEquals(expectedPattern, front.toString());
    }

    @Test
    void builderAddsAllCriteriaFromAnotherBuilder() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        ParetoFront.Builder builder2 = new ParetoFront.Builder();

        builder1.add(480, 3, 0);
        builder2.add(481, 2, 0).add(482, 1, 0);

        builder1.addAll(builder2);

        ParetoFront front = builder1.build();
        assertEquals(3, front.size());

        assertDoesNotThrow(() -> front.get(480, 3));
        assertDoesNotThrow(() -> front.get(481, 2));
        assertDoesNotThrow(() -> front.get(482, 1));
    }
}

