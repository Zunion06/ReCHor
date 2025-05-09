package ch.epfl.rechor.journey;

import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.Transfers;

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

        Connections connections = timeTable.connectionsFor(date);
        Transfers transfers = timeTable.transfers();

        int[] transferTable = new int[timeTable.stations().size()];
        Arrays.fill(transferTable, -1);
        int packedTransferId = transfers.arrivingAt(arrStationId);

        for (int i = startInclusive(packedTransferId); i < endExclusive(packedTransferId); i++) {
            transferTable[transfers.depStationId(i)] = transfers.minutes(i);
        }// fill the table with transfer duration when the transfer from the station of the same index has a transfer toward arrStation

        for (int connectionId = 0; connectionId < connections.size(); connectionId++) {

            ParetoFront.Builder tripFrontBuilder = new ParetoFront.Builder();

            int connectionArrStopId = transferTable[getStationId(connections
                    .arrStopId(connectionId))];

            // OPTION 1 go by foot
            // if a connection by foot exists from where we arrive to the destination
            if (connectionArrStopId != -1) {
                tripFrontBuilder.add(connections.arrMins(connectionId)
                                + connectionArrStopId,
                        0,
                        connectionId);
            } // add the tuple for going by foot from where we arrived to the destination

            //OPTION 2 stay until next intermediate stop
            //if the pareto frontier for the trips containing our connection exist
            int tripId=connections.tripId(connectionId);
            if (profileBuilder.forTrip(tripId) != null) {
                tripFrontBuilder.addAll(profileBuilder.forTrip(tripId));
            } // keep going on that trip and add all the tuples of that frontier

            // OPTION 3 change vehicle
            // if the pareto frontier for the station at which we arrive exist
            if (profileBuilder.forStation(getStationId(connections
                    .arrStopId(connectionId))) != null) {
                int arrMins = connections.arrMins(connectionId);
                int firstConnection = connectionId;
                profileBuilder.forStation(getStationId(connections
                                .arrStopId(connectionId)))
                        .forEach(x -> {
                            if (depMins(x) >= arrMins) {
                                tripFrontBuilder.add(arrMins(x), changes(x) + 1,
                                        firstConnection);
                            }
                        });
            } // convert and add all the tuple of that frontier

            if(!tripFrontBuilder.isEmpty()){

                // UPDATE existing Pareto Front Trips
                if (profileBuilder.forTrip(tripId) == null) {
                    profileBuilder.setForTrip(tripId, new ParetoFront.Builder());
                } // create the profile builder for a given trip id if necessary
                profileBuilder.forTrip(tripId).addAll(tripFrontBuilder);


                // UPDATE Pareto Front Station
                int connectionDepStationId = getStationId(connections.depStopId(connectionId));
                int connectionDepMins = connections.depMins(connectionId);

                if(profileBuilder.forStation(connectionDepStationId)==null ||
                        !profileBuilder.forStation(connectionDepStationId).fullyDominates(tripFrontBuilder,connectionDepMins)){

                    // for each transfer arriving at the depStation of the connection:
                    for (int i = startInclusive(transfers.arrivingAt(connectionDepStationId));
                         i < endExclusive(transfers.arrivingAt(connectionDepStationId)); i++) {

                        int depStationId = transfers.depStationId(i);
                        int newDepTime = connectionDepMins - transfers.minutes(i);
                        int firstConnectionId = connectionId;
                        int connectionPos = connections.tripPos(connectionId);

                        if (profileBuilder.forStation(depStationId) == null) {
                            profileBuilder.setForStation(depStationId, new ParetoFront.Builder());
                        } // create a profile builder for the start station if necessary

                        tripFrontBuilder.forEach(x -> {
                            byte intermediateStops = (byte) (connections.tripPos(payload(x)) - connectionPos);

                            profileBuilder.forStation(depStationId).add(
                                    withPayload(withDepMins(x, newDepTime),
                                            Bits32_24_8.pack(firstConnectionId, intermediateStops))
                                    ); // add a depMin and a payload to each t
                        }); // create a tuple starting from the station i and take the created trip and add it
                    }
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
}
