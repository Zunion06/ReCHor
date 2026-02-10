package ch.epfl.rechor.journey;

import java.util.List;

import static ch.epfl.rechor.journey.Vehicle.AERIAL_LIFT;
import static ch.epfl.rechor.journey.Vehicle.ALL;
import static ch.epfl.rechor.journey.Vehicle.BUS;
import static ch.epfl.rechor.journey.Vehicle.FERRY;
import static ch.epfl.rechor.journey.Vehicle.FUNICULAR;
import static ch.epfl.rechor.journey.Vehicle.METRO;
import static ch.epfl.rechor.journey.Vehicle.TRAIN;
import static ch.epfl.rechor.journey.Vehicle.TRAM;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import ch.epfl.rechor.journey.Vehicle;

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