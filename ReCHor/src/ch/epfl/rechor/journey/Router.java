package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.TimeTable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A Router capable of calculating the profile of every journey taken to reach a destination at
 * a given day.
 * @author Matthias Gibon (397827)
 */
public record Router(TimeTable timeTable) {

    /**
     * Uses a CSA algorithm to find create a profile containing all the optimised journeys to reach
     * a given station at a given date
     * @param date date of the journeys
     * @param arrStationId index of the station aimed to reach
     * @return a profile containing all the optimised journeys to reach the destination at a given date
     */
    public Profile profile(LocalDate date, int arrStationId){

        ParetoFront.Builder builder = new ParetoFront.Builder();
        for(int connectionId=0; connectionId < timeTable.connectionsFor(date).size();connectionId++){
            
            // Add connection by foot if it exists, (comment savoir si le transfer existe?)


        }

        return new Profile(timeTable,date,arrStationId, null);
    }
}
