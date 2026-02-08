package epfl.rechor.journey;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import static epfl.rechor.journey.Vehicle.AERIAL_LIFT;
import static epfl.rechor.journey.Vehicle.ALL;
import static epfl.rechor.journey.Vehicle.BUS;
import static epfl.rechor.journey.Vehicle.FERRY;
import static epfl.rechor.journey.Vehicle.FUNICULAR;
import static epfl.rechor.journey.Vehicle.METRO;
import static epfl.rechor.journey.Vehicle.TRAIN;
import static epfl.rechor.journey.Vehicle.TRAM;

class VehicleTest {
    @Test
    void vehicleEnumIsCorrectlyDefined() {
        var expectedValues = new Vehicle[]{
                TRAM, METRO, TRAIN, BUS, FERRY, AERIAL_LIFT, FUNICULAR
        };
        assertArrayEquals(expectedValues, Vehicle.values());
    }

    @Test
    void vehicleALLIsCorrectlyDefined() {
        var expectedList = List.of(TRAM, METRO, TRAIN, BUS, FERRY, AERIAL_LIFT, FUNICULAR);
        var actualList = ALL;
        assertEquals(expectedList, actualList);
    }
}