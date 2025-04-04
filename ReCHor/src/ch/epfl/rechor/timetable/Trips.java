package ch.epfl.rechor.timetable;

/**
 * represent the trips indexed
 * @author Matthias Gibon (397827)
 */
public interface Trips extends Indexed{

    /**
     * gives the id of the route taken by the trip
     * @param id stop id
     * @return the route id for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    public abstract int routeId(int id);

    /**
     * gives the destination of the trip
     * @param id route id
     * @return the name of a trip's destination for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    public abstract String destination(int id);
}
