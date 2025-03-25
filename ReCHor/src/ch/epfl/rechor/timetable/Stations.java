package ch.epfl.rechor.timetable;

/**
 * represent the stations indexed
 */
public interface Stations extends Indexed {

    /**
     * @param id station id
     * @return the name of the station for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    abstract String name(int id);

    /**
     * @param id station id
     * @return the longitude (in degree) of the station for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    abstract double longitude(int id);

    /**
     * @param id station id
     * @return the latitude (in degree) of the station for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    abstract double latitude(int id);
}
