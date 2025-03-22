package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.TimeTable;

import java.time.LocalDate;
import java.util.List;


public record Profile(TimeTable timeTable, LocalDate date, int arrStationId, List<ParetoFront> stationFront) {
    public Profile(TimeTable timeTable, LocalDate date, int arrStationId, List<ParetoFront> stationFront){
        this.timeTable=timeTable;
        this.date=date;
        this.arrStationId=arrStationId;
        this.stationFront=List.copyOf(stationFront);

    }
}
