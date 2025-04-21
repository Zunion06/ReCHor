package ch.epfl.rechor.journey;

import ch.epfl.rechor.Bits32_24_8;
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
     *
     * @param date         date of the journeys
     * @param arrStationId index of the station aimed to reach
     * @return a profile containing all the optimised journeys to reach the destination at a given date
     */
    public Profile profile(LocalDate date, int arrStationId) {

        Profile.Builder profileBuilder = new Profile.Builder(timeTable, date, arrStationId);

        int[] transferTable = new int[timeTable.stations().size()];
        Arrays.fill(transferTable, -1);
        int packedTransferId = timeTable.transfers().arrivingAt(arrStationId);
        for (int i = startInclusive(packedTransferId); i < endExclusive(packedTransferId); i++) {
            transferTable[timeTable.transfers().depStationId(i)] = timeTable.transfers().minutes(i);
            System.out.println(timeTable.transfers().depStationId(i));
        }

        for (int connectionId = 0; connectionId < timeTable.connectionsFor(date).size(); connectionId++) {

            ParetoFront.Builder tripFrontBuilder = new ParetoFront.Builder();

            //OPTION 1 go by foot
            // if a connection by foot exists from where we arrive to the destination
            if (transferTable[getStationId(timeTable.connectionsFor(date)
                    .arrStopId(connectionId))] != -1) {
                tripFrontBuilder.add(timeTable.connectionsFor(date).arrMins(connectionId)
                                + transferTable[getStationId(timeTable.connectionsFor(date)
                                .arrStopId(connectionId))],
                        0,
                        connectionId); //will need to create a payload later
            } //add the tuple for going by foot from where we arrived to the destination

            //OPTION 2 stay until next intermediate stop
            //if the pareto frontier for the trips containing our connection exist
            int tripId=timeTable.connectionsFor(date).tripId(connectionId);
            if (profileBuilder.forTrip(tripId) != null) {
                tripFrontBuilder.addAll(profileBuilder.forTrip(tripId));
            } // keep going on that trip and add all the tuples of that frontier

            //OPTION 3 change vehicle
            //if the pareto frontier for the station at which we arrive exist
            if (profileBuilder.forStation(getStationId(timeTable.connectionsFor(date).arrStopId(connectionId))) != null) {
                int arrMins = timeTable.connectionsFor(date).arrMins(connectionId);
                int firstConnection = connectionId;
                profileBuilder.forStation(getStationId(timeTable.connectionsFor(date).arrStopId(connectionId)))
                        .forEach(x -> {
                            if (depMins(x) >= arrMins) {
                                tripFrontBuilder.add(arrMins(x), changes(x) + 1,
                                        firstConnection);
                            }
                        }); //will need payload
            } //convert and add all the tuple of that frontier

            if(!tripFrontBuilder.isEmpty()){
                //UPDATE existing Pareto Front Trips
                if (profileBuilder.forTrip(timeTable.connectionsFor(date).tripId(connectionId)) == null) {
                    profileBuilder.setForTrip(timeTable.connectionsFor(date).tripId(connectionId), new ParetoFront.Builder());
                } //create the profile builder for a given trip id if necessary
                profileBuilder.forTrip(timeTable.connectionsFor(date).tripId(connectionId)).addAll(tripFrontBuilder);

                //UPDATE Pareto Front Station
                int connectionDepStationId = getStationId(timeTable.connectionsFor(date).depStopId(connectionId));
                int connectionDepMins = timeTable.connectionsFor(date).depMins(connectionId);
                //for each transfer arriving at the station
                for (int i = startInclusive(timeTable.transfers().arrivingAt(connectionDepStationId));
                     i < endExclusive(timeTable.transfers().arrivingAt(connectionDepStationId)); i++) {

                    int firstConnectionId = connectionId;
                    int connectionPos = timeTable.connectionsFor(date).tripPos(connectionId);
                    int depStationId = timeTable.transfers().depStationId(i);
                    int transferId = i;

                    if (profileBuilder.forStation(depStationId) == null) {
                        profileBuilder.setForStation(depStationId, new ParetoFront.Builder());
                    } // create a profile builder for the start station if necessary

                    tripFrontBuilder.forEach(x -> {
                        timeTable.transfers().minutesBetween(depStationId,connectionDepStationId);

                        profileBuilder.forStation(depStationId)
                                .add(withPayload(withDepMins(x, connectionDepMins
                                                - timeTable.transfers().minutes(transferId)),
                                        Bits32_24_8.pack(
                                                firstConnectionId,
                                                timeTable.connectionsFor(date).tripPos(payload(x)) - connectionPos
                                        ))
                                );
                    }); //create a tuple starting from the station i and take the created trip and add it
                }
            }


        }

        return profileBuilder.build();
    }

    /**
     * find the StationId of a stopId
     * @param stopId stopId of the station we're looking for
     * @return the StationId corresponding to the stopId
     */
    private int getStationId(int stopId) {
        return timeTable.isStationId(stopId) ?
                stopId:
                timeTable.platforms().stationId(timeTable.stationId(stopId));
    }

    //first 24 bits are the id of the first connection while the first 8 bits are the number of change

}
