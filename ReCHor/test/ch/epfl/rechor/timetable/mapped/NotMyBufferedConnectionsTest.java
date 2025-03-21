package ch.epfl.rechor.timetable.mapped;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import static org.junit.jupiter.api.Assertions.*;


class BufferedConnectionsTest {
    private BufferedConnections bufferedConnections;


    @BeforeEach
    void setUp() {
        // Création du buffer principal (3 connexions, 5 champs U16/U32)
        ByteBuffer buffer = ByteBuffer.allocate(3 * (2 + 2 + 2 + 2 + 4)); // 2 bytes pour U16, 4 pour S32
        buffer.putShort((short) 10); // depStopId = 10
        buffer.putShort((short) 100); // depMins = 100
        buffer.putShort((short) 20); // arrStopId = 20
        buffer.putShort((short) 120); // arrMins = 120
        buffer.putInt(0x00000102); // tripId = 1, tripPos = 2


        buffer.putShort((short) 30); // depStopId = 30
        buffer.putShort((short) 200); // depMins = 200
        buffer.putShort((short) 40); // arrStopId = 40
        buffer.putShort((short) 220); // arrMins = 220
        buffer.putInt(0x00000204); // tripId = 2, tripPos = 4


        buffer.putShort((short) 50); // depStopId = 50
        buffer.putShort((short) 300); // depMins = 300
        buffer.putShort((short) 60); // arrStopId = 60
        buffer.putShort((short) 320); // arrMins = 320
        buffer.putInt(0x00000308); // tripId = 3, tripPos = 8
        buffer.flip();


        // Création du buffer pour nextConnectionId
        ByteBuffer succBuffer = ByteBuffer.allocate(3 * 4);
        succBuffer.putInt(1);
        succBuffer.putInt(2);
        succBuffer.putInt(0);
        succBuffer.flip();


        bufferedConnections = new BufferedConnections(buffer, succBuffer);
    }


    @Test
    void sizeReturnsCorrectValue() {
        assertEquals(3, bufferedConnections.size());
    }


    @Test
    void depMinsReturnsCorrectValue() {
        assertEquals(100, bufferedConnections.depMins(0));
        assertEquals(200, bufferedConnections.depMins(1));
        assertEquals(300, bufferedConnections.depMins(2));
    }


    @Test
    void arrMinsReturnsCorrectValue() {
        assertEquals(120, bufferedConnections.arrMins(0));
        assertEquals(220, bufferedConnections.arrMins(1));
        assertEquals(320, bufferedConnections.arrMins(2));
    }


    @Test
    void depStopIdReturnsCorrectValue() {
        assertEquals(10, bufferedConnections.depStopId(0));
        assertEquals(30, bufferedConnections.depStopId(1));
        assertEquals(50, bufferedConnections.depStopId(2));
    }


    @Test
    void arrStopIdReturnsCorrectValue() {
        assertEquals(20, bufferedConnections.arrStopId(0));
        assertEquals(40, bufferedConnections.arrStopId(1));
        assertEquals(60, bufferedConnections.arrStopId(2));
    }


    @Test
    void tripIdReturnsCorrectValue() {
        assertEquals(1, bufferedConnections.tripId(0));
        assertEquals(2, bufferedConnections.tripId(1));
        assertEquals(3, bufferedConnections.tripId(2));
    }


//    @Test
//    void tripPosReturnsCorrectValue() {
//        assertEquals(2 << 24, bufferedConnections.tripPos(0));
//        assertEquals(4 << 24, bufferedConnections.tripPos(1));
//        assertEquals(8 << 24, bufferedConnections.tripPos(2));
//    }


    @Test
    void nextConnectionIdReturnsCorrectValue() {
        assertEquals(1, bufferedConnections.nextConnectionId(0));
        assertEquals(2, bufferedConnections.nextConnectionId(1));
        assertEquals(0, bufferedConnections.nextConnectionId(2));
    }


    @Test
    void depMinsThrowsExceptionForInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedConnections.depMins(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedConnections.depMins(3));
    }


    @Test
    void arrMinsThrowsExceptionForInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedConnections.arrMins(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedConnections.arrMins(3));
    }


    @Test
    void depStopIdThrowsExceptionForInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedConnections.depStopId(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedConnections.depStopId(3));
    }


    @Test
    void arrStopIdThrowsExceptionForInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedConnections.arrStopId(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedConnections.arrStopId(3));
    }


    @Test
    void tripIdThrowsExceptionForInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedConnections.tripId(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedConnections.tripId(3));
    }


    @Test
    void tripPosThrowsExceptionForInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedConnections.tripPos(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedConnections.tripPos(3));
    }


    @Test
    void nextConnectionIdThrowsExceptionForInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedConnections.nextConnectionId(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedConnections.nextConnectionId(3));
    }
}
