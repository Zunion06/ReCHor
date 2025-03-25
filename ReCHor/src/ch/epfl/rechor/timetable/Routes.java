package ch.epfl.rechor.timetable;

import ch.epfl.rechor.journey.Vehicle;

/**
 * represent the indexed routes
 */
public interface Routes extends Indexed{

    /**
     * gives the kind of vehicle used on a given route
     * @param id route id
     * @return the type of vehicle used on a given route
     * @throws IndexOutOfBoundsException for invalid index
     */
    abstract Vehicle vehicle(int id);

    /**
     * gives the name of a given route
     * @param id route id
     * @return the name of the route for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    abstract String name(int id);
}
