package ch.epfl.rechor.timetable.mapped;


import ch.epfl.rechor.PackedRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;


import static org.junit.jupiter.api.Assertions.*;


class NotMyBufferedTransfersTest {
    private BufferedTransfers bufferedTransfers;


    @BeforeEach
    void setUp() {
        // Création du buffer principal (3 transferts, 3 champs U16/U8)
        ByteBuffer buffer = ByteBuffer.allocate(3 * (2 + 2 + 1)); // 2 bytes pour U16, 1 byte pour U8
        buffer.putShort((short) 1); // depStationId = 1
        buffer.putShort((short) 5); // arrStationId = 5
        buffer.put((byte) 10); // minutes = 10


        buffer.putShort((short) 2); // depStationId = 2
        buffer.putShort((short) 5); // arrStationId = 5
        buffer.put((byte) 12); // minutes = 12


        buffer.putShort((short) 3); // depStationId = 3
        buffer.putShort((short) 6); // arrStationId = 6
        buffer.put((byte) 15); // minutes = 15
        buffer.flip();


        bufferedTransfers = new BufferedTransfers(buffer);
    }


    @Test
    void sizeReturnsCorrectValue() {
        assertEquals(3, bufferedTransfers.size());
    }


    @Test
    void depStationIdReturnsCorrectValue() {
        assertEquals(1, bufferedTransfers.depStationId(0));
        assertEquals(2, bufferedTransfers.depStationId(1));
        assertEquals(3, bufferedTransfers.depStationId(2));
    }


    @Test
    void minutesReturnsCorrectValue() {
        assertEquals(10, bufferedTransfers.minutes(0));
        assertEquals(12, bufferedTransfers.minutes(1));
        assertEquals(15, bufferedTransfers.minutes(2));
    }


    @Test
    void arrivingAtReturnsCorrectPackedRange() {
        int range = bufferedTransfers.arrivingAt(5);
        assertEquals(0, PackedRange.startInclusive(range));
        assertEquals(2, PackedRange.endExclusive(range));


        range = bufferedTransfers.arrivingAt(6);
        assertEquals(2, PackedRange.startInclusive(range));
        assertEquals(3, PackedRange.endExclusive(range));
    }


    @Test
    void minutesBetweenReturnsCorrectValue() {
        assertEquals(10, bufferedTransfers.minutesBetween(1, 5));
        assertEquals(12, bufferedTransfers.minutesBetween(2, 5));
        assertEquals(15, bufferedTransfers.minutesBetween(3, 6));
    }


    @Test
    void arrivingAtThrowsExceptionForInvalidStationId() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> bufferedTransfers.arrivingAt(10));
    }


    @Test
    void minutesBetweenThrowsExceptionForInvalidTransfer() {
        assertThrows(NoSuchElementException.class, () -> bufferedTransfers.minutesBetween(1, 6));
    }


    @Test
    void depStationIdThrowsExceptionForInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedTransfers.depStationId(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedTransfers.depStationId(3));
    }


    @Test
    void minutesThrowsExceptionForInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedTransfers.minutes(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedTransfers.minutes(3));
    }
}
