package ch.epfl.rechor.timetable;

/**
 * represent the indexed platforms
 * @author Matthias Gibon (397827)
 */
public interface Platforms extends Indexed{

    /**
     * gives the name a given platform
     * @param id platform id
     * @return the name of the station for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    public abstract String name(int id);

    /**
     * gives the stationId of a given platform
     * @param id platform id
     * @return the station id for the given platform id
     * @throws IndexOutOfBoundsException for invalid index
     */
    public abstract int stationId(int id);
}
