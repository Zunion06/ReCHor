package ch.epfl.rechor.journey;

import org.junit.jupiter.api.Test;

import static ch.epfl.rechor.journey.PackedCriteria.*;
import static org.junit.jupiter.api.Assertions.*;

public class MyPackedCriteriaTest {
    @Test
    void PackedCriteriaPackWorksOnNonTrivialCase() {
        int arrMins = 540; // 9h00
        int changes = 2;
        long actualResult = pack(arrMins,changes,0);
        long expectedResult = 0b00000000000000011000011000000010L << 32;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void PackedCriteriaPackWorksOnLimitCase() {
        int arrMins = 2879 ;
        int changes = (1 << 7) - 1;
        int payload = -1 ;
        long actualResult = pack(arrMins,changes,payload);
        long expectedResult = 0b110000101111111111111111111111111111111111111111111L;
//        System.out.println(Integer.toBinaryString(2880));
//        System.out.println(Long.toBinaryString(expectedResult));
//        System.out.println(Long.toBinaryString(actualResult));
        assertEquals(expectedResult, actualResult);

        arrMins = -240 ;
        changes = 0;
        payload = 0 ;
        actualResult = pack(arrMins,changes,payload);
        expectedResult = 0;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void PackedCriteriaThrowsWhenArrMinsIsTooBig() {
        int arrMins = 2880;
        int changes = (1 << 7) - 1;
        int payload = -1 ;
        assertThrows(IllegalArgumentException.class, () ->
            pack(arrMins,changes,payload)
        );
    }

    @Test
    void PackedCriteriaThrowsWhenArrMinsIsTooLOW() {
        int arrMins = -241;
        int changes = (1 << 7) - 1;
        int payload = -1 ;
        assertThrows(IllegalArgumentException.class, () ->
                pack(arrMins,changes,payload)
        );
    }

    @Test
    void PackedCriteriaThrowsWhenChangesIsTooBig() {
        int arrMins = (1 << 12) - 1 ;
        int changes = 1 << 7;
        int payload = -1 ;
        assertThrows(IllegalArgumentException.class, () ->
            pack(arrMins,changes,payload)
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
        int depMins = 480; // 8h00
        int arrMins = 540; // 9h00
        int changes = 2;
        int payload = 255;
        long pack = PackedCriteria.withDepMins(pack(arrMins,changes,payload),depMins);
        int actualResult = PackedCriteria.depMins(pack);
        int expectedResult = 8 * 60 ;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void PackedCriteriaDepMinsWorksOnLimitCase() {
        int depMins = 2879;
        int arrMins = 2879;
        int changes = 2;
        int payload = 255;
        long pack = PackedCriteria.withDepMins(pack(arrMins,changes,payload),depMins);
        int actualResult = PackedCriteria.depMins(pack);
        int expectedResult = 2879 ;
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
        int arrMins = 540; // 9h00
        int changes = 2;
        int payload = 255;
        long pack = pack(arrMins,changes,payload);
        int actualResult = PackedCriteria.arrMins(pack);
        int expectedResult = 9 * 60 ;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void PackedCriteriaChangesWorksOnNonTrivialCase() {
        int arrMins = 540; // 9h00
        int changes = 2;
        int payload = 255;
        long pack = pack(arrMins,changes,payload);
        int actualResult = changes(pack);
        int expectedResult = 2 ;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void PackedCriteriaChangesWorksOnLimitCases() {
        int arrMins = 540; // 9h00
        int changes1 = (1 << 7) - 1 ;
        int payload = 255;
        long pack1 = pack(arrMins,changes1,payload);
        int actualResult1 = changes(pack1);
        int expectedResult1 = (1 << 7) - 1 ;
        assertEquals(expectedResult1, actualResult1);

        int changes2 = 0 ;
        long pack2 = pack(arrMins,changes2,payload);
        int actualResult2 = changes(pack2);
        int expectedResult2 = 0 ;
        assertEquals(expectedResult2, actualResult2);
    }

    @Test
    void PackedCriteriaPayloadWorksOnNonTrivialCase() {
        int arrMins = 540; // 9h00
        int changes = 2;
        int payload = 255;
        long pack = pack(arrMins,changes,payload);
        int actualResult = PackedCriteria.payload(pack);
        int expectedResult = 255 ;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void PackedCriteriaPayloadWorksOnLimitCases() {
        int arrMins = 540; // 9h00
        int changes = 2;

        int actualResult1 = PackedCriteria.payload(-1L >>> 1);
        int expectedResult1 = -1 ;
        assertEquals(expectedResult1, actualResult1);

        int payload2 = 0;
        long pack2 = pack(arrMins,changes,payload2);
        int actualResult2 = PackedCriteria.payload(pack2);
        int expectedResult2 = 0 ;
        assertEquals(expectedResult2, actualResult2);
    }

    @Test
    void PackedCriteriaDominatesOrIsEqualWorks1() {
        int depMins1 = 725; // 8h05
        int arrMins1 = 540; // 9h00
        int changes1 = 2;
        int payload1 = 255;
        long pack1 = PackedCriteria.withDepMins(pack(arrMins1,changes1,payload1),depMins1);

        int depMins2 = 480; // 8h00
        int arrMins2 = 540; // 9h00
        int changes2 = 2;
        int payload2 = 255;
        long pack2 = PackedCriteria.withDepMins(pack(arrMins2,changes2,payload2),depMins2);

        assertTrue(PackedCriteria.dominatesOrIsEqual(pack1,pack2));
        assertTrue(PackedCriteria.dominatesOrIsEqual(pack1,pack1));
        assertFalse(PackedCriteria.dominatesOrIsEqual(pack2,pack1));
    }

    @Test
    void PackedCriteriaDominatesOrIsEqualWorks2() {
        int depMins1 = 725 -240 ; // 8h05
        int arrMins1 = 540; // 9h00
        int changes1 = 2;
        int payload1 = 255;
        long pack1 = PackedCriteria.withDepMins(pack(arrMins1,changes1,payload1),depMins1);

        int depMins2 = 480; // 8h00
        int arrMins2 = 775-240; // 8h55
        int changes2 = 2;
        int payload2 = 255;
        long pack2 = PackedCriteria.withDepMins(pack(arrMins2,changes2,payload2),depMins2);

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
        long pack1 = PackedCriteria.withDepMins(pack(arrMins1,changes1,payload1),depMins1);

        int depMins2 = 480; // 8h00
        int arrMins2 = 540; // 9h00
        int changes2 = 2;
        int payload2 = 255;
        long pack2 = PackedCriteria.withDepMins(pack(arrMins2,changes2,payload2),depMins2);

        assertFalse(PackedCriteria.dominatesOrIsEqual(pack1,pack2));
        assertTrue(PackedCriteria.dominatesOrIsEqual(pack1,pack1));
        assertFalse(PackedCriteria.dominatesOrIsEqual(pack2,pack1));
    }

    @Test
    void PackedCriteriaDominatesOrIsEqualThrowsWhenNoDepartureTime() {
        int depMins1 = 725; // 8h05
        int arrMins1 = 540; // 9h00
        int changes1 = 2;
        int payload1 = 255;
        long pack1 = PackedCriteria.withDepMins(pack(arrMins1,changes1,payload1),depMins1);

        int arrMins2 = 540; // 9h00
        int changes2 = 2;
        int payload2 = 255;
        long pack2 = pack(arrMins2,changes2,payload2);

        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.dominatesOrIsEqual(pack1,pack2)
        );
        assertThrows(IllegalArgumentException.class, () ->
                PackedCriteria.dominatesOrIsEqual(pack2,pack1)
        );
    }

    @Test
    void PackedCriteriaWithDepMinsWorksOnNonTrivialCase() {
        int depMins = 480; // 8h00
        int arrMins = 540; // 9h00
        int changes = 2;
        int payload = 255;
        long actualResult = PackedCriteria.withDepMins(pack(arrMins,changes,payload),depMins);
        long expectedResult = 0b01101001011110011000011000000010L<<32 | 0b11111111;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void PackedCriteriaWithDepMinsWorksOnLimitCase() {
        int depMins1 = 2879 ;
        int arrMins1 = 2879 ;
        int changes = 2;
        int payload = 255;
        long actualResult1 = PackedCriteria.withDepMins(pack(arrMins1,changes,payload),depMins1);
//        System.out.println(Integer.toBinaryString(4095-0b110000101111));
        long expectedResult1 = 0b00011110100001100001011110000010L<<32 | 0b11111111 ;
        assertEquals(expectedResult1, actualResult1);

        int depMins2 = -240 ;
        int arrMins2 = -240 ;
        long actualResult2 = PackedCriteria.withDepMins(pack(arrMins2,changes,payload),depMins2);
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
        int arrMins = 540; // 9h00
        int changes = 2;
        int payload = 255;
        long pack = pack(arrMins,changes,payload);
        long actualResult = withAdditionalChange(pack);
        long expectedResult = 0b00000000000000011000011000000011L<<32 | 0b11111111 ;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void PackedCriteriaWithAdditionalChangeWorksOnLimitCases() {
        int arrMins = 540; // 9h00
        int changes1 = (1 << 7) - 2 ;
        int payload = 255;
        long pack1 = pack(arrMins,changes1,payload);
        long actualResult1 = withAdditionalChange(pack1);
        long expectedResult1 = 0b00000000000000011000011001111111L<<32 | 0b11111111 ;
        assertEquals(expectedResult1, actualResult1);

        int changes2 = 0 ;
        long pack2 = pack(arrMins,changes2,payload);
        long actualResult2 = withAdditionalChange(pack2);
        long expectedResult2 = 0b00000000000000011000011000000001L<<32 | 0b11111111 ;
        assertEquals(expectedResult2, actualResult2);
    }

    @Test
    void PackedCriteriaWithPayloadWorksOnNonTrivialCase() {
        int arrMins = 540; // 9h00
        int changes = 2;
        int payload = 255;
        long pack = pack(arrMins,changes,payload);
        long actualResult = PackedCriteria.withPayload(pack,256);
        long expectedResult = 0b00000000000000011000011000000010L<<32 | 0b100000000 ;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void PackedCriteriaWithPayloadWorksOnLimitCases() {
        int arrMins = 540; // 9h00
        int changes = 2;
        int payload1 = -1;
        long pack1 = pack(arrMins,changes,payload1);
        long actualResult1 = PackedCriteria.withPayload(pack1,0);
        long expectedResult1 = 0b00000000000000011000011000000010L<<32 ;

        assertEquals(expectedResult1, actualResult1);

        int payload2 = 0;
        long pack2 = pack(arrMins,changes,payload2);
        long actualResult2 = PackedCriteria.withPayload(pack2,-1);
        long expectedResult2 = 0b00000000000000011000011000000010L<<32 | Integer.toUnsignedLong(-1) ;
        assertEquals(expectedResult2, actualResult2);
    }
}

