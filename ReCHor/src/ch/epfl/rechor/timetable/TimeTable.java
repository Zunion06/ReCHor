package ch.epfl.rechor.timetable;

import java.time.LocalDate;

/**
 * represent the timetable of public transports for the given values
 * @author Matthias Gibon
 */
public interface TimeTable {

    /**
     * gives the stations index of the timetable
     * @return the station index of the timetable
     */
    public abstract Stations stations();

    /**
     * gives the alternative names of stations in the timetable
     * @return the aliases of stations in the timetable
     */
    public abstract StationAliases stationAliases();

    /**
     * gives the platforms index of the timetable
     * @return the platforms index of the timetable
     */
    public abstract Platforms platforms();

    /**
     * gives the routes index of the timetable
     * @return the routes index of the timetable
     */
    public abstract Routes routes();

    /**
     * gives the transfers index of the timetable
     * @return the transfers index of the timetable
     */
    public abstract Transfers transfers();

    /**
     * gives the trips found at a given date
     * @param date date of the trips searched
     * @return all the trips at the given date
     */
    public abstract Trips tripsFor(LocalDate date);

    /**
     * gives the connections found at a given date
     * @param date date of the connections searched
     * @return all the connections at the given date
     */
    public abstract Connections connectionsFor(LocalDate date);

    /**
     * Returns true if the given stop index is a station (not a platform).
     * @param stopId stop index to be evaluated
     * @return true if the stop index is linked to a station
     */
    public default boolean isStationId(int stopId){
        return (stopId < stations().size());
    }

    /**
     * Returns true if the given stop index is a platform (not a station).
     * @param stopId stop index to be evaluated
     * @return true if the stop index is linked to a platform
     */
    public default boolean isPlatformId(int stopId){
        return !isStationId(stopId);
    }

    /**
     * Returns the station of platform index of the given stop (same if the stop is a station).
     * @param stopId stop index to be "translated"
     * @return the station or platform index correspond to the stop index
     */
    public default int stationId(int stopId){
        return isStationId(stopId) ? stopId : stopId - stations().size();
    }

    /**
     * Returns the platform name of the given stop, or null if it is a station.
     * @param stopId stop index evaluated
     * @return the name of the platform if it is a platform, else returns null
     */
    public default String platformName(int stopId){
        return isPlatformId(stopId) ? platforms().name(stationId(stopId)) : null;
    }
}
