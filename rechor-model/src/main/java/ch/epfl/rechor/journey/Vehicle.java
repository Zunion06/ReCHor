package ch.epfl.rechor.journey;

import java.util.List;

/**
 * contains all the possible vehicle that can be used for the journeys
 * @author Matthias Gibon (397827)
 */
public enum Vehicle {

    TRAM,
    METRO,
    TRAIN,
    BUS,
    FERRY,
    AERIAL_LIFT,
    FUNICULAR
    ;

    /**
     * list of all type of vehicles with their enum values as index
     */
    public static final List<Vehicle> ALL = List.of(Vehicle.values());
}
