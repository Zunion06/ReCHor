package epfl.rechor.timetable;

/**
 * represent the station aliases indexed
 * @author Matthias Gibon (397827)
 */
public interface StationAliases extends Indexed {

    /**
     * returns the alias corresponding to the given StationAliases id
     * @param id station aliases id
     * @return the alias corresponding to the given id
     */
    public abstract String alias(int id);

    /**
     * returns the name corresponding to the given StationAliases id
     * @param id station aliases id
     * @return the name corresponding to the given id
     */
    public abstract String stationName(int id);
}
