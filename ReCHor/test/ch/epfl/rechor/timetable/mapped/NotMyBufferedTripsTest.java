package ch.epfl.rechor.timetable.mapped;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;




class NotMyBufferedTripsTest {
    private BufferedTrips bufferedTrips;


    @BeforeEach
    void setUp() {
        // Simule une table de chaînes
        List<String> stringTable = List.of("Gare Centrale", "Aéroport", "Université");


        // Création d'un ByteBuffer avec 2 trips
        ByteBuffer buffer = ByteBuffer.allocate(4 * 2); // 2 trips, 2 U16 champs chacun
        buffer.putShort((short) 1); // routeId = 1
        buffer.putShort((short) 2); // destinationId = 2 ("Université")
        buffer.putShort((short) 0); // routeId = 0
        buffer.putShort((short) 1); // destinationId = 1 ("Aéroport")
        buffer.flip();


        bufferedTrips = new BufferedTrips(stringTable, buffer);
    }


    @Test
    void sizeReturnsCorrectValue() {
        assertEquals(2, bufferedTrips.size());
    }


    @Test
    void routeIdReturnsCorrectValue() {
        assertEquals(1, bufferedTrips.routeId(0));
        assertEquals(0, bufferedTrips.routeId(1));
    }


    @Test
    void destinationReturnsCorrectValue() {
        assertEquals("Université", bufferedTrips.destination(0));
        assertEquals("Aéroport", bufferedTrips.destination(1));
    }


    @Test
    void routeIdThrowsExceptionForInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedTrips.routeId(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedTrips.routeId(2));
    }


    @Test
    void destinationThrowsExceptionForInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedTrips.destination(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedTrips.destination(2));
    }
}
