package ch.epfl.rechor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;



public class MyPackedCriteria2Test {
//Test d'aliénor
    @Test
    public void testPack() {
        long packed = PackedCriteria.pack(600, 3, 123);
        System.out.println(Integer.toBinaryString(600));
        System.out.println(Long.toBinaryString(PackedCriteria.arrMins(packed)));
        assertEquals(600, PackedCriteria.arrMins(packed));
        assertEquals(3, PackedCriteria.changes(packed));
        assertEquals(123, PackedCriteria.payload(packed));
        assertFalse(PackedCriteria.hasDepMins(packed));
    }

    @Test
    public void testPackInvalidArrivalTime() {
        assertThrows(IllegalArgumentException.class, () -> {
            PackedCriteria.pack(3000, 3, 123); // Heure d'arrivée invalide
        });
    }

    @Test
    public void testPackInvalidChanges() {
        assertThrows(IllegalArgumentException.class, () -> {
            PackedCriteria.pack(600, 128, 123);
        });
    }

    @Test
    public void testHasDepMins() {
        long packed = PackedCriteria.pack(600, 3, 123);
        assertFalse(PackedCriteria.hasDepMins(packed));

        packed = PackedCriteria.withDepMins(packed, 300);
        assertTrue(PackedCriteria.hasDepMins(packed));
    }

    @Test
    public void testDepMins() {
        long packed = PackedCriteria.pack(600, 3, 123);
        packed = PackedCriteria.withDepMins(packed, 300);

        assertEquals(300, PackedCriteria.depMins(packed));
    }

    @Test
    public void testDepMinsWithoutDepMins() {
        long packed = PackedCriteria.pack(600, 3, 123);
        assertThrows(IllegalArgumentException.class, () -> {
            PackedCriteria.depMins(packed); // Pas d'heure de départ
        });
    }

    @Test
    public void testArrMins() {
        long packed = PackedCriteria.pack(600, 3, 123);
        assertEquals(600, PackedCriteria.arrMins(packed));
    }

    @Test
    public void testChanges() {
        long packed = PackedCriteria.pack(600, 3, 123);
        assertEquals(3, PackedCriteria.changes(packed));
    }

    @Test
    public void testPayload() {
        long packed = PackedCriteria.pack(600, 3, 123);
        assertEquals(123, PackedCriteria.payload(packed));
    }

    @Test
    public void testDominatesOrIsEqual() {
        long criteria1 = PackedCriteria.pack(600, 3, 123);
        long criteria2 = PackedCriteria.pack(600, 3, 123);
        assertTrue(PackedCriteria.dominatesOrIsEqual(criteria1, criteria2));

        long criteria3 = PackedCriteria.pack(500, 2, 200);
        assertTrue(PackedCriteria.dominatesOrIsEqual(criteria3, criteria1));

        long criteria4 = PackedCriteria.pack(700, 4, 100);
        assertFalse(PackedCriteria.dominatesOrIsEqual(criteria4, criteria1));
    }

    @Test
    public void testDominatesOrIsEqualWithDepMins() {
        long criteria1 = PackedCriteria.pack(600, 3, 123);
        criteria1 = PackedCriteria.withDepMins(criteria1, 300);

        long criteria2 = PackedCriteria.pack(600, 3, 123);
        criteria2 = PackedCriteria.withDepMins(criteria2, 300);

        assertTrue(PackedCriteria.dominatesOrIsEqual(criteria1, criteria2));

        long criteria3 = PackedCriteria.pack(600, 3, 123);
        long finalCriteria = criteria1;
        assertThrows(IllegalArgumentException.class, () -> {
            PackedCriteria.dominatesOrIsEqual(finalCriteria, criteria3); // Incompatible (l'un a une heure de départ, l'autre non)
        });
    }

    @Test
    void testDominatesOrIsEqualProMaxPlus() {
        //without departure time
        long criteria1 = PackedCriteria.pack(600, 3, 123); // Arrival: 600, Changes: 3, Payload: 123
        long criteria2 = PackedCriteria.pack(600, 3, 123); // Arrival: 600, Changes: 3, Payload: 123
        assertTrue(PackedCriteria.dominatesOrIsEqual(criteria1, criteria2));

        //higher arrival time, fewer changes, higher payload
        long criteria3 = PackedCriteria.pack(500, 2, 200); // Arrival: 500, Changes: 2, Payload: 200
        long criteria4 = PackedCriteria.pack(600, 3, 123); // Arrival: 600, Changes: 3, Payload: 123
        assertTrue(PackedCriteria.dominatesOrIsEqual(criteria3, criteria4));

        //worse arrival time, more changes, lower payload
        long criteria5 = PackedCriteria.pack(700, 4, 100); // Arrival: 700, Changes: 4, Payload: 100
        long criteria6 = PackedCriteria.pack(600, 3, 123); // Arrival: 600, Changes: 3, Payload: 123
        assertFalse(PackedCriteria.dominatesOrIsEqual(criteria5, criteria6));

        // one has departure time, the other not
        long criteria7 = PackedCriteria.pack(600, 3, 123); // No departure time
        long criteria8 = PackedCriteria.withDepMins(criteria7, 300); // Add departure time
        assertThrows(IllegalArgumentException.class, () -> {
            PackedCriteria.dominatesOrIsEqual(criteria7, criteria8);
        });
        // = arrival, changes, payload
        long criteria9 = PackedCriteria.withDepMins(criteria7, 300); // Departure: 300
        long criteria10 = PackedCriteria.withDepMins(criteria7, 400); // Departure: 400

        assertTrue(PackedCriteria.dominatesOrIsEqual(criteria9, criteria10)); //<--- ce truc n'est pas sensé etre true car on prefere partir plus tard
    }
    @Test
    public void testWithoutDepMins() {
        long packed = PackedCriteria.pack(600, 3, 123);
        packed = PackedCriteria.withDepMins(packed, 300);

        long withoutDep = PackedCriteria.withoutDepMins(packed);
        assertFalse(PackedCriteria.hasDepMins(withoutDep));
        assertEquals(600, PackedCriteria.arrMins(withoutDep));
        assertEquals(3, PackedCriteria.changes(withoutDep));
        assertEquals(123, PackedCriteria.payload(withoutDep));
    }

    @Test
    public void testWithDepMins() {
        long packed = PackedCriteria.pack(600, 3, 123);
        packed = PackedCriteria.withDepMins(packed, 300);

        assertTrue(PackedCriteria.hasDepMins(packed));
        assertEquals(300, PackedCriteria.depMins(packed));
    }

    @Test
    public void testWithAdditionalChange() {
        long packed = PackedCriteria.pack(600, 3, 123);
        packed = PackedCriteria.withAdditionalChange(packed);

        assertEquals(4, PackedCriteria.changes(packed));
    }

    @Test
    public void testWithAdditionalChangeMax() {
        long packed = PackedCriteria.pack(600, 127, 123);
        assertThrows(IllegalArgumentException.class, () -> {
            PackedCriteria.withAdditionalChange(packed); // Nombre maximal de changements atteint
        });
    }

    @Test
    public void testWithPayload() {
        long packed = PackedCriteria.pack(600, 3, 123);
        packed = PackedCriteria.withPayload(packed, 456);

        assertEquals(456, PackedCriteria.payload(packed));
    }
}
