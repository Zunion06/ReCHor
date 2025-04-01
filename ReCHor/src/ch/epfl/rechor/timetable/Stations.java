package ch.epfl.rechor.timetable;

/**
 * represent the stations indexed
 * @author Matthias Gibon
 */
public interface Stations extends Indexed {

    /**
     * @param id station id
     * @return the name of the station for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    public abstract String name(int id);

    /**
     * @param id station id
     * @return the longitude (in degree) of the station for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    public abstract double longitude(int id);

    /**
     * @param id station id
     * @return the latitude (in degree) of the station for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    public abstract double latitude(int id);
}
