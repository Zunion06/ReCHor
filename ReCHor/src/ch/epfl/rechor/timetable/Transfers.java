package ch.epfl.rechor.timetable;

import java.util.NoSuchElementException;

/**
 * represent the transfers indexed (only between stations)
 * @author Matthias Gibon
 */
public interface Transfers extends Indexed{

    /**
     * gives the id of the departing station in a given transfer
     * @param id transfer id
     * @return the id of the departing station of the transfer
     * @throws IndexOutOfBoundsException for invalid index
     */
    public abstract int depStationId(int id);

    /**
     * gives the length of the transfer (in minutes)
     * @param id transfer id
     * @return the length of the transfer (in minutes)
     * @throws IndexOutOfBoundsException for invalid index
     */
    public abstract int minutes(int id);

    /**
     * gives the interval for all transfers arriving to this station
     * @param stationId id of the arrival station
     * @return packed interval of all transfer ids arriving at this station
     * @throws IndexOutOfBoundsException if stationId is invalid
     */
    public abstract int arrivingAt(int stationId);

    /**
     * return the time taken (in minutes) for the transfer between the two stations
     * @param depStationId departing station id
     * @param arrStationId arriving station id
     * @return transfer length in minutes
     * @throws NoSuchElementException if there is no such transfers between both stations
     */
    public abstract int minutesBetween(int depStationId, int arrStationId);
}
