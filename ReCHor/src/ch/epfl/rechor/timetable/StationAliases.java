package ch.epfl.rechor.timetable;

/**
 * represent the station aliases indexed
 */
public interface StationAliases extends Indexed {

    /**
     * returns the alias corresponding to the given StationAliases id
     * @param id station aliases id
     * @return the alias corresponding to the given id
     */
    abstract String alias(int id);

    /**
     * returns the name corresponding to the given StationAliases id
     * @param id station aliases id
     * @return the name corresponding to the given id
     */
    abstract String stationName(int id);
}
