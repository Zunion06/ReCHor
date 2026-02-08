package epfl.rechor.timetable;

import java.time.LocalDate;

/**
 * Represent the timetable of public transports for the given values
 * With the particularity of using a Cache to better retrieve useful data
 * @author Guillaume Pommier (401065)
 */
public final class CachedTimeTable implements TimeTable{
    private final TimeTable underlyingTimeTable;
    private Trips trips;
    private Connections connections;
    private LocalDate tripsDate;
    private LocalDate connectionsDate;

    /**
     * returns an instance of CachedTimeTable
     * @param underlyingTimeTable the instance of TimeTable that will be used for all methods
     */
    public CachedTimeTable(TimeTable underlyingTimeTable){
        this.underlyingTimeTable = underlyingTimeTable;
    }

    /**
     * gives the transfers index of the timetable
     *
     * @return the transfers index of the timetable
     */
    @Override
    public Transfers transfers() {
        return underlyingTimeTable.transfers();
    }

    /**
     * gives the routes index of the timetable
     *
     * @return the routes index of the timetable
     */
    @Override
    public Routes routes() {
        return underlyingTimeTable.routes();
    }

    /**
     * gives the platforms index of the timetable
     *
     * @return the platforms index of the timetable
     */
    @Override
    public Platforms platforms() {
        return underlyingTimeTable.platforms();
    }

    /**
     * gives the alternative names of stations in the timetable
     *
     * @return the aliases of stations in the timetable
     */
    @Override
    public StationAliases stationAliases() {
        return underlyingTimeTable.stationAliases();
    }

    /**
     * gives the stations index of the timetable
     *
     * @return the station index of the timetable
     */
    @Override
    public Stations stations() {
        return underlyingTimeTable.stations();
    }

    /**
     * gives the trips found at a given date
     *
     * @param date date of the trips searched
     * @return all the trips at the given date
     */
    @Override
    public Trips tripsFor(LocalDate date) {
        if (!date.equals(tripsDate)) {
            trips = underlyingTimeTable.tripsFor(date);
            tripsDate = date;
        }
        return trips;
    }

    /**
     * gives the connections found at a given date
     *
     * @param date date of the connections searched
     * @return all the connections at the given date
     */
    @Override
    public Connections connectionsFor(LocalDate date) {
        if (!date.equals(connectionsDate)) {
            connections = underlyingTimeTable.connectionsFor(date);
            connectionsDate = date;
        }
        return connections;
    }
}
