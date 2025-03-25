package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.Trips;

import java.time.LocalDate;
import java.util.List;


public record Profile(TimeTable timeTable, LocalDate date, int arrStationId, List<ParetoFront> stationFront) {
    /**
     * table containing the timeTable, date, arrival station and paretoFront list for each stops of a travel
     * @param timeTable list of transports and their dates
     * @param date date of the profile
     * @param arrStationId arrival station
     * @param stationFront list of ParetoFront for each stationId
     */
    public Profile(TimeTable timeTable, LocalDate date, int arrStationId, List<ParetoFront> stationFront){
        this.timeTable=timeTable;
        this.date=date;
        this.arrStationId=arrStationId;
        this.stationFront=List.copyOf(stationFront);
    }

    /**
     * gives the connection for the date of the profile
     * @return the connection at the date of the profile
     */
    public Connections connections(){
        return timeTable.connectionsFor(date);
    }

    /**
     * gives thetrip for the date of the profile
     * @return the trip at the date of the profile
     */
    public Trips trips(){
        return timeTable.tripsFor(date);
    }

    /**
     * gives the pareto frontier for a given station index
     * @param stationId index of the given station
     * @return the paretoFront of the given station
     * @throws IndexOutOfBoundsException if index is invalid
     */
    public ParetoFront forStation(int stationId){
        return stationFront.get(stationId);
    }
}
