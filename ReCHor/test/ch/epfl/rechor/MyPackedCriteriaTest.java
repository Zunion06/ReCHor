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
        int depMins = 4094;
        int arrMins = 4095;
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

    @Test
    void PackedCriteriaChangesWorksOnNonTrivialCase() {
        int arrMins = 780; // 9h00
        int changes = 2;
        int payload = 255;
        long pack = PackedCriteria.pack(arrMins,changes,payload);
        int actualResult = PackedCriteria.changes(pack);
        int expectedResult = 2 ;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void PackedCriteriaChangesWorksOnLimitCases() {
        int arrMins = 780; // 9h00
        int changes1 = (1 << 7) - 1 ;
        int payload = 255;
        long pack1 = PackedCriteria.pack(arrMins,changes1,payload);
        int actualResult1 = PackedCriteria.changes(pack1);
        int expectedResult1 = (1 << 7) - 1 ;
        assertEquals(expectedResult1, actualResult1);

        int changes2 = 0 ;
        long pack2 = PackedCriteria.pack(arrMins,changes2,payload);
        int actualResult2 = PackedCriteria.changes(pack2);
        int expectedResult2 = 0 ;
        assertEquals(expectedResult2, actualResult2);
    }

    @Test
    void PackedCriteriaPayloadWorksOnNonTrivialCase() {
        int arrMins = 780; // 9h00
        int changes = 2;
        int payload = 255;
        long pack = PackedCriteria.pack(arrMins,changes,payload);
        int actualResult = PackedCriteria.payload(pack);
        int expectedResult = 255 ;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void PackedCriteriaPayloadWorksOnLimitCases() {
        int arrMins = 780; // 9h00
        int changes = 2;

        int actualResult1 = PackedCriteria.payload(-1L >>> 1);
        int expectedResult1 = -1 ;
        assertEquals(expectedResult1, actualResult1);

        int payload2 = 0;
        long pack2 = PackedCriteria.pack(arrMins,changes,payload2);
        int actualResult2 = PackedCriteria.payload(pack2);
        int expectedResult2 = 0 ;
        assertEquals(expectedResult2, actualResult2);
    }

    @Test
    void PackedCriteriaDominatesOrIsEqualWorks1() {
        int depMins1 = 725; // 8h05
        int arrMins1 = 780; // 9h00
        int changes1 = 2;
        int payload1 = 255;
        long pack1 = PackedCriteria.withDepMins(PackedCriteria.pack(arrMins1,changes1,payload1),depMins1);

        int depMins2 = 720; // 8h00
        int arrMins2 = 780; // 9h00
        int changes2 = 2;
        int payload2 = 255;
        long pack2 = PackedCriteria.withDepMins(PackedCriteria.pack(arrMins2,changes2,payload2),depMins2);

        assertTrue(PackedCriteria.dominatesOrIsEqual(pack1,pack2));
        assertTrue(PackedCriteria.dominatesOrIsEqual(pack1,pack1));
        assertFalse(PackedCriteria.dominatesOrIsEqual(pack2,pack1));
    }

    @Test
    void PackedCriteriaDominatesOrIsEqualWorks2() {
        int depMins1 = 725; // 8h05
        int arrMins1 = 780; // 9h00
        int changes1 = 2;
        int payload1 = 255;
        long pack1 = PackedCriteria.withDepMins(PackedCriteria.pack(arrMins1,changes1,payload1),depMins1);

        int depMins2 = 720; // 8h00
        int arrMins2 = 775; // 8h55
        int changes2 = 2;
        int payload2 = 255;
        long pack2 = PackedCriteria.withDepMins(PackedCriteria.pack(arrMins2,changes2,payload2),depMins2);

        assertFalse(PackedCriteria.dominatesOrIsEqual(pack1,pack2));
        assertTrue(PackedCriteria.dominatesOrIsEqual(pack1,pack1));
        assertFalse(PackedCriteria.dominatesOrIsEqual(pack2,pack1));
    }

    @Test
    void PackedCriteriaDominatesOrIsEqualWorks3() {
        int depMins1 = 725; // 8h05
        int arrMins1 = 775; // 8h55
        int changes1 = 6;
        int payload1 = 255;
        long pack1 = PackedCriteria.withDepMins(PackedCriteria.pack(arrMins1,changes1,payload1),depMins1);

        int depMins2 = 720; // 8h00
        int arrMins2 = 780; // 9h00
        int changes2 = 2;
        int payload2 = 255;
        long pack2 = PackedCriteria.withDepMins(PackedCriteria.pack(arrMins2,changes2,payload2),depMins2);

        assertFalse(PackedCriteria.dominatesOrIsEqual(pack1,pack2));
        assertTrue(PackedCriteria.dominatesOrIsEqual(pack1,pack1));
        assertFalse(PackedCriteria.dominatesOrIsEqual(pack2,pack1));
    }

    @Test
    void PackedCriteriaDominatesOrIsEqualThrowsWhenNoDepartureTime() {
        int depMins1 = 725; // 8h05
        int arrMins1 = 780; // 9h00
        int changes1 = 2;
        int payload1 = 255;
        long pack1 = PackedCriteria.withDepMins(PackedCriteria.pack(arrMins1,changes1,payload1),depMins1);

        int arrMins2 = 780; // 9h00
        int changes2 = 2;
        int payload2 = 255;
        long pack2 = PackedCriteria.pack(arrMins2,changes2,payload2);

        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.dominatesOrIsEqual(pack1,pack2)
        );
        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.dominatesOrIsEqual(pack2,pack1)
        );
    }

    @Test
    void PackedCriteriaWithDepMinsWorksOnNonTrivialCase() {
        int depMins = 720; // 8h00
        int arrMins = 780; // 9h00
        int changes = 2;
        int payload = 255;
        long actualResult = PackedCriteria.withDepMins(PackedCriteria.pack(arrMins,changes,payload),depMins);
        long expectedResult = 0b01101001011110011000011000000010L<<32 | 0b11111111;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void PackedCriteriaWithDepMinsWorksOnLimitCase() {
        int depMins1 = (1 << 12) - 1 ;
        int arrMins1 = (1 << 12) - 1 ;
        int changes = 2;
        int payload = 255;
        long actualResult1 = PackedCriteria.withDepMins(PackedCriteria.pack(arrMins1,changes,payload),depMins1);
        long expectedResult1 = 0b00000000000001111111111110000010L<<32 | 0b11111111 ;
        assertEquals(expectedResult1, actualResult1);

        int depMins2 = 0 ;
        int arrMins2 = 0 ;
        long actualResult2 = PackedCriteria.withDepMins(PackedCriteria.pack(arrMins2,changes,payload),depMins2);
        long expectedResult2 = 0b01111111111110000000000000000010L<<32 | 0b11111111 ;
        assertEquals(expectedResult2, actualResult2);
    }

    @Test
    void PackedCriteriaWithDepMinsThrowsWhenNoDepMinsInCriteria() {
        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.depMins(0)
        );
        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.depMins(1L << 63)
        );
    }

    @Test
    void PackedCriteriaWithAdditionalChangeWorksOnNonTrivialCase() {
        int arrMins = 780; // 9h00
        int changes = 2;
        int payload = 255;
        long pack = PackedCriteria.pack(arrMins,changes,payload);
        long actualResult = PackedCriteria.withAdditionalChange(pack);
        long expectedResult = 0b00000000000000011000011000000011L<<32 | 0b11111111 ;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void PackedCriteriaWithAdditionalChangeWorksOnLimitCases() {
        int arrMins = 780; // 9h00
        int changes1 = (1 << 7) - 2 ;
        int payload = 255;
        long pack1 = PackedCriteria.pack(arrMins,changes1,payload);
        long actualResult1 = PackedCriteria.withAdditionalChange(pack1);
        long expectedResult1 = 0b00000000000000011000011001111111L<<32 | 0b11111111 ;
        assertEquals(expectedResult1, actualResult1);

        int changes2 = 0 ;
        long pack2 = PackedCriteria.pack(arrMins,changes2,payload);
        long actualResult2 = PackedCriteria.withAdditionalChange(pack2);
        long expectedResult2 = 0b00000000000000011000011000000001L<<32 | 0b11111111 ;
        assertEquals(expectedResult2, actualResult2);
    }

    @Test
    void PackedCriteriaWithPayloadWorksOnNonTrivialCase() {
        int arrMins = 780; // 9h00
        int changes = 2;
        int payload = 255;
        long pack = PackedCriteria.pack(arrMins,changes,payload);
        long actualResult = PackedCriteria.withPayload(pack,256);
        long expectedResult = 0b00000000000000011000011000000010L<<32 | 0b100000000 ;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void PackedCriteriaWithPayloadWorksOnLimitCases() {
        int arrMins = 780; // 9h00
        int changes = 2;
        int payload1 = -1;
        long pack1 = PackedCriteria.pack(arrMins,changes,payload1);
        long actualResult1 = PackedCriteria.withPayload(pack1,0);
        long expectedResult1 = 0b00000000000000011000011000000010L<<32 ;

        assertEquals(expectedResult1, actualResult1);

        int payload2 = 0;
        long pack2 = PackedCriteria.pack(arrMins,changes,payload2);
        long actualResult2 = PackedCriteria.withPayload(pack2,-1);
        long expectedResult2 = 0b00000000000000011000011000000010L<<32 | Integer.toUnsignedLong(-1) ;
        assertEquals(expectedResult2, actualResult2);
    }
}
