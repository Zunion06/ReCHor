package epfl.rechor.timetable;

/**
 * represent the indexed connections
 * @author Matthias Gibon (397827)
 */
public interface Connections extends Indexed{

    /**
     * gives the stop id of the first stop
     * @param id connection id
     * @return id of the first stop of the connection
     * @throws IndexOutOfBoundsException for invalid index
     */
    public abstract int depStopId(int id);

    /**
     * gives the length in minutes of the connection
     * @param id connection id
     * @return the length of the connection (in minutes)
     * @throws IndexOutOfBoundsException for invalid index
     */
    public abstract int depMins(int id);

    /**
     * gives the arrival stop id of the connection
     * @param id connection id
     * @return the id of the last stop of the connection
     * @throws IndexOutOfBoundsException for invalid index
     */
    public abstract int arrStopId(int id);

    /**
     * gives the arrival time of the connection
     * @param id connection id
     * @return arrival time of the connection (in minutes)
     * @throws IndexOutOfBoundsException for invalid index
     */
    public abstract int arrMins(int id);

    /**
     * gives the id of the trip containing the connection
     * @param id connection id
     * @return the id of the trip containing the connection
     * @throws IndexOutOfBoundsException for invalid index
     */
    public abstract int tripId(int id);

    /**
     * gives the position of the connection in its trip it belongs to
     * @param id connection id
     * @return the position of the connection in its trip
     * @throws IndexOutOfBoundsException for invalid index
     */
    public abstract int tripPos(int id);

    /**
     * gives the id of the following connection in a trip
     * @param id current connection id
     * @return the id of the next connection
     * @throws IndexOutOfBoundsException for invalid index
     */
    public abstract int nextConnectionId(int id);
}
