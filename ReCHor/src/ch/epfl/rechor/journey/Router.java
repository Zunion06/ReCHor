package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.TimeTable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static ch.epfl.rechor.journey.PackedCriteria.*;

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

        Profile.Builder profileBuilder = new Profile.Builder(timeTable,date,arrStationId);

        List<ParetoFront.Builder> connectionFronts = new ArrayList<>();

        for(int connectionId=0; connectionId < timeTable.connectionsFor(date).size();connectionId++){

            ParetoFront.Builder stationFrontBuilder = new ParetoFront.Builder();
//            profileBuilder.setForStation(timeTable.connectionsFor(date)
//                    .depStopId(connectionId),stationFrontBuilder);

            ParetoFront.Builder tripFrontBuilder = new ParetoFront.Builder();
//            profileBuilder.setForTrip(timeTable.connectionsFor(date).
//                    nextConnectionId(connectionId),tripFrontBuilder);

            //OPTION 1 go by foot
            int transferDuration;
            if(true){ // Add connection by foot if it exists, (comment savoir si le transfer existe?)
                transferDuration=timeTable.transfers().minutesBetween(timeTable
                        .connectionsFor(date).arrStopId(connectionId),arrStationId);
                stationFrontBuilder.add(timeTable.connectionsFor(date).arrMins(connectionId)+transferDuration
                        ,0,0); //will need to create a payload later
                // number of changes?
            }
            //OPTION 2 stay until next intermediate stop
            if(true){

            }

            //OPTION 3 change vehicle
            if(true){

            }

            //UPDATE existing Pareto Front Trips
            if(profileBuilder.forTrip(timeTable.connectionsFor(date).tripId(connectionId))==null){
                profileBuilder.setForTrip(timeTable.connectionsFor(date).tripId(connectionId),new ParetoFront.Builder());
            } //create the profile's builder for a given trip id if necessary
            profileBuilder.forTrip(timeTable.connectionsFor(date).tripId(connectionId)).addAll(tripFrontBuilder);

            //UPDATE existing Pareto Front Station

            stationFrontBuilder.forEach(x -> withDepMins(x,PackedCriteria.depMins(x)-transferDuration));

        }

        return new Profile(timeTable,date,arrStationId, null);
    }

    private long packPayload(int firstConnectionId, LocalDate date){
        return 0;
    }
}
