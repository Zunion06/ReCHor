package ch.epfl.rechor.timetable.mapped;


import static org.junit.jupiter.api.Assertions.*;


import ch.epfl.rechor.journey.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;
import java.util.List;


class NotMyBufferedRoutesTest {
    private BufferedRoutes bufferedRoutes;


    @BeforeEach
    void setUp() {
        // Simulons une table de chaînes pour les noms des lignes
        List<String> stringTable = List.of("Ligne A", "Ligne B", "Ligne C");


        // Créons un buffer contenant les données aplaties des lignes
        ByteBuffer buffer = ByteBuffer.allocate(9); // 3 lignes * (U16 + U8)
        buffer.putShort((short) 0).put((byte) 0); // Ligne A - TRAM
        buffer.putShort((short) 1).put((byte) 1); // Ligne B - METRO
        buffer.putShort((short) 2).put((byte) 2); // Ligne C - TRAIN
        buffer.flip();


        bufferedRoutes = new BufferedRoutes(stringTable, buffer);
    }


    @Test
    void testVehicle() {
        assertEquals(Vehicle.TRAM, bufferedRoutes.vehicle(0));
        assertEquals(Vehicle.METRO, bufferedRoutes.vehicle(1));
        assertEquals(Vehicle.TRAIN, bufferedRoutes.vehicle(2));
    }


    @Test
    void testName() {
        assertEquals("Ligne A", bufferedRoutes.name(0));
        assertEquals("Ligne B", bufferedRoutes.name(1));
        assertEquals("Ligne C", bufferedRoutes.name(2));
    }


    @Test
    void testInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedRoutes.vehicle(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedRoutes.vehicle(3));
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedRoutes.name(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedRoutes.name(3));
    }
}
