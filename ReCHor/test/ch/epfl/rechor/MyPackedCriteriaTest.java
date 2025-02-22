package ch.epfl.rechor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyPackedCriteriaTest {
    @Test
    void PackedCriteriaPackWorksOnNonTrivialCase() {
        int arrMins = 780; // 9h00
        int changes = 2;
        long actualResult = PackedCriteria.pack(arrMins,changes,0);
        long expectedResult = 0b00000000000000011000011000000010L << 32;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void PackedCriteriaPackWorksOnLimitCase() {
        int arrMins = (1 << 12) - 1 ;
        int changes = (1 << 7) - 1;
        int payload = -1 ;
        long actualResult = PackedCriteria.pack(arrMins,changes,payload);
        long expectedResult = (1L << 51) - 1;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void PackedCriteriaThrowsWhenArrMinsIsTooBig() {
        int arrMins = 1 << 12;
        int changes = (1 << 7) - 1;
        int payload = -1 ;
        assertThrows(IllegalArgumentException.class, () ->
            PackedCriteria.pack(arrMins,changes,payload)
        );
    }

    @Test
    void PackedCriteriaThrowsWhenChangesIsTooBig() {
        int arrMins = (1 << 12) - 1 ;
        int changes = 1 << 7;
        int payload = -1 ;
        assertThrows(IllegalArgumentException.class, () ->
            PackedCriteria.pack(arrMins,changes,payload)
        );
    }

    @Test
    void PackedCriteriaHasDepMinsWorks() {
        assertFalse(PackedCriteria.hasDepMins(0));
        assertTrue(PackedCriteria.hasDepMins(1L << 51));
        assertTrue(PackedCriteria.hasDepMins(1L << 62));
        assertFalse(PackedCriteria.hasDepMins(1L << 63));
    }

    @Test
    void PackedCriteriaDepMinsWorksOnNonTrivialCase() {
        int depMins = 720; // 8h00
        int arrMins = 780; // 9h00
        int changes = 2;
        int payload = 255;
        long pack = PackedCriteria.withDepMins(PackedCriteria.pack(arrMins,changes,payload),depMins);
        int actualResult = PackedCriteria.depMins(pack);
        int expectedResult = 8 * 60 ;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void PackedCriteriaDepMinsWorksOnLimitCase() {
        int depMins = 4094; // 8h00
        int arrMins = 4095; // 9h00
        int changes = 2;
        int payload = 255;
        long pack = PackedCriteria.withDepMins(PackedCriteria.pack(arrMins,changes,payload),depMins);
        int actualResult = PackedCriteria.depMins(pack);
        int expectedResult = 4094 - 240 ;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void PackedCriteriaDepMinsThrowsWhenNoDepMinsInCriteria() {
        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.depMins(0)
        );
        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.depMins(1L << 63)
        );
    }

    @Test
    void PackedCriteriaArrMinsWorks() {
        int arrMins = 780; // 9h00
        int changes = 2;
        int payload = 255;
        long pack = PackedCriteria.pack(arrMins,changes,payload);
        int actualResult = PackedCriteria.arrMins(pack);
        int expectedResult = 9 * 60 ;
        assertEquals(expectedResult, actualResult);
    }
}
