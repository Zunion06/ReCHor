package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.Trips;

import java.time.LocalDate;
import java.util.List;


public record Profile(TimeTable timeTable, LocalDate date, int arrStationId, List<ParetoFront> stationFront) {
    public Profile(TimeTable timeTable, LocalDate date, int arrStationId, List<ParetoFront> stationFront){
        this.timeTable=timeTable;
        this.date=date;
        this.arrStationId=arrStationId;
        this.stationFront=List.copyOf(stationFront);
    }

    public Connections connections(){
        return timeTable.connectionsFor(date);
    }

    public Trips trips(){
        return timeTable.tripsFor(date);
    }

    public ParetoFront forStation(int stationId){
        return stationFront.get(stationId);
    }
}
