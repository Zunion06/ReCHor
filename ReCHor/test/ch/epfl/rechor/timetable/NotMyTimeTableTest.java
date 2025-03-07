package ch.epfl.rechor.timetable;
import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

class NotMyTimeTableTest {

    private TimeTable timeTable;


    @BeforeEach
    void setUp() {
        timeTable = new FakeTimeTable();
    }


    @Test
    void testIsStationId() {
        assertTrue(timeTable.isStationId(0));  // ID 0 est une station
        assertTrue(timeTable.isStationId(2));  // ID 2 est aussi une station
        assertFalse(timeTable.isStationId(3)); // ID 3 est un quai
        assertFalse(timeTable.isStationId(5)); // ID 5 aussi
    }


    @Test
    void testIsPlatformId() {
        assertFalse(timeTable.isPlatformId(0)); // ID 0 est une station
        assertFalse(timeTable.isPlatformId(2)); // ID 2 est une station
        assertTrue(timeTable.isPlatformId(3));  // ID 3 est un quai
        assertTrue(timeTable.isPlatformId(5));  // ID 5 est aussi un quai
    }


    @Test
    void testStationId() {
        assertEquals(0, timeTable.stationId(0));  // Une station retourne son propre ID
        assertEquals(2, timeTable.stationId(2));  // Une autre station aussi
        assertEquals(0, timeTable.stationId(3));  // Quai 3 appartient à la station 0
        assertEquals(2, timeTable.stationId(5));  // Quai 5 appartient à la station 2
    }


    @Test
    void testPlatformName() {
        //assertNull(timeTable.platformName(0)); // Une station ne doit pas avoir de nom de quai
        //assertNull(timeTable.platformName(2)); // Une autre station non plus

        assertEquals("Quai 1", timeTable.platformName(3)); // Quai 3 → "Quai 1"
        //assertEquals("Quai 3", timeTable.platformName(5)); // Quai 5 → "Quai 3"
    }
}

// Classe factice pour tester TimeTable
class FakeTimeTable implements TimeTable {
    private final int stationCount = 3; // Supposons 3 stations (ID 0, 1, 2)


    @Override
    public Stations stations() {
        Stations stations = new Stations() {
            @Override
            public String name(int id) {
                return "";
            }


            @Override
            public double longitude(int id) {
                return 0;
            }


            @Override
            public double latitude(int id) {
                return 0;
            }


            @Override
            public int size() {
                return stationCount;
            }
        };
        return stations;
    }


    @Override
    public Platforms platforms() {
        return new Platforms() {
            @Override
            public int size() {
                return 0;
            }


            @Override
            public String name(int stopId) {
                return "Quai " + (stopId+1);
            }


            @Override
            public int stationId(int id) {
                return 0;
            }
        };
    }


    // Les autres méthodes abstraites (pas nécessaires pour nos tests)
    @Override public StationAliases stationAliases() { return null; }
    @Override public Routes routes() { return null; }
    @Override public Transfers transfers() { return null; }
    @Override public Trips tripsFor(LocalDate date) { return null; }
    @Override public Connections connectionsFor(LocalDate date) { return null; }
}
