package ch.epfl.rechor.journey;

import java.util.List;

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
