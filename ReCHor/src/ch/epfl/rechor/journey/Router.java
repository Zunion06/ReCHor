package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.TimeTable;

import java.time.LocalDate;
import java.util.Arrays;

import static ch.epfl.rechor.PackedRange.*;
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

        int[] transferTable = new int[timeTable.stations().size()];
        Arrays.fill(transferTable,-1);
        for(int i=startInclusive(timeTable.transfers().arrivingAt(arrStationId));
            i<endExclusive(timeTable.transfers().arrivingAt(arrStationId));i++) {
            transferTable[timeTable.transfers().depStationId(i)] = timeTable.transfers().
                    minutesBetween(timeTable.transfers().depStationId(i), arrStationId);
        }

        for(int connectionId=0; connectionId < timeTable.connectionsFor(date).size();connectionId++){

            ParetoFront.Builder tripFrontBuilder = new ParetoFront.Builder();

            //OPTION 1 go by foot
            // if a connection by foot exists from where we arrive to the destination
            if(transferTable[timeTable.stationId(timeTable.connectionsFor(date)
                    .depStopId(timeTable.connectionsFor(date).arrStopId(connectionId)))] != -1){
                tripFrontBuilder.add(timeTable.connectionsFor(date).arrMins(connectionId)
                                +transferTable[timeTable.stationId(timeTable.connectionsFor(date)
                                .arrStopId(connectionId))],
                        0,
                        0); //will need to create a payload later
            } //add the tuple for going by foot from where we arrived to the destination

            //OPTION 2 stay until next intermediate stop
            //if the pareto frontier for the trips containing our connection exist
            if(profileBuilder.forTrip(timeTable.connectionsFor(date).tripId(connectionId))!=null){
                tripFrontBuilder.addAll(profileBuilder.forTrip(timeTable.connectionsFor(date).tripId(connectionId)));
            } // keep going on that trip and add all the tuples of that frontier

            //OPTION 3 change vehicle
            //if the pareto frontier for the station at which we arrive exist
            if(profileBuilder.forStation(timeTable.stationId(timeTable.connectionsFor(date).arrStopId(connectionId)))!=null){
                int arrMins =timeTable.connectionsFor(date).arrMins(connectionId);
                profileBuilder.forStation(timeTable.stationId(timeTable.connectionsFor(date).arrStopId(connectionId)))
                        .forEach(x->{if(depMins(x)>=arrMins){ tripFrontBuilder.add(arrMins(x),changes(x)+1,0);}}); //will need payload
            } //convert and add all the tuple of that frontier

            //UPDATE existing Pareto Front Trips
            if(profileBuilder.forTrip(timeTable.connectionsFor(date).tripId(connectionId))==null){
                profileBuilder.setForTrip(timeTable.connectionsFor(date).tripId(connectionId),new ParetoFront.Builder());
            } //create the profile builder for a given trip id if necessary
            profileBuilder.forTrip(timeTable.connectionsFor(date).tripId(connectionId)).addAll(tripFrontBuilder);

            //UPDATE Pareto Front Station
            int connectionDepStationId=timeTable.stationId(timeTable.connectionsFor(date).depStopId(connectionId));
            int connectionDepMins=timeTable.connectionsFor(date).depMins(connectionId);
            //for each transfer arriving at the station
            for(int i = startInclusive(timeTable.transfers().arrivingAt(connectionDepStationId));
                i <endExclusive(timeTable.transfers().arrivingAt(connectionDepStationId));i++){

                int depStationid = timeTable.transfers().depStationId(i);

                if(profileBuilder.forStation(depStationid)==null) {
                    profileBuilder.setForStation(depStationid, new ParetoFront.Builder());
                } // create a profile builder for the start station if necessary

                tripFrontBuilder.forEach(x -> profileBuilder.forStation(depStationid)
                        .add(withDepMins(x, connectionDepMins
                                - timeTable.transfers().minutesBetween(depStationid, connectionDepStationId))
                )); //create a tuple starting from the station i and take the created trip and add it
            }


        }

        return profileBuilder.build();
    }

    private long packPayload(int firstConnectionId, LocalDate date){
        return 0;
    }
}
