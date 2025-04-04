package ch.epfl.rechor.journey;

import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.timetable.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import static ch.epfl.rechor.journey.PackedCriteria.*;

/**
 * utilitarian class whose sole purpose is to extract a list of Journeys from a profile, given a stating point
 * @author Guillaume Pommier (401065)
 */
public final class JourneyExtractor {
    private JourneyExtractor() {}

    /**
     * Extract a list of Journeys from a profile and a stating point
     * @param profile the profile containing all the necessary information to create the journeys and all the legs in it
     * @param depStationId a departure station index representing the starting point of the journeys
     * @return a list of Journeys starting at the station which the index is a parameter
     */
    public static List<Journey> journeys(Profile profile, int depStationId) {

        ParetoFront pf = profile.forStation(depStationId);
        Connections connections = profile.connections();
        Trips trips = profile.trips();
        TimeTable timeTable = profile.timeTable();

        List<Journey> journeysList = new ArrayList<>();
        pf.forEach((profileCriteria) -> {

            List<Journey.Leg> legsList = new ArrayList<>();
            LocalDateTime transportArrTime;
            int connectionID = Bits32_24_8.unpack24(payload(profileCriteria));
            Stop transportDepStop = createStop(connections.depStopId(connectionID), timeTable);
            Stop transportArrStop = null;
            long currentCriteria = profileCriteria;

            //first add a Leg.Foot to legsList if necessary
            if (depStationId != getStationId(connections.depStopId(connectionID), timeTable)) {
                LocalDateTime footDepTime = createLocalDateTime(depMins(profileCriteria), profile);
                LocalDateTime footArrTime = footDepTime.plusMinutes(
                        timeTable.transfers().minutesBetween(
                                depStationId,
                                getStationId(connections.depStopId(connectionID), timeTable)
                        ));
                legsList.add(new Journey.Leg.Foot(
                        createStop(depStationId, timeTable),
                        footDepTime,
                        transportDepStop,
                        footArrTime
                ));
            }

            //add all the Leg.Transport of the Journey and all the Leg.Foot in between them
            for (int i = 0; i <= changes(profileCriteria); i++) {
                LocalDateTime transportDepTime = createLocalDateTime(connections.depMins(connectionID), profile);
                int depStopID = connections.depStopId(connectionID);

                List<Journey.Leg.IntermediateStop> intermediateStops = new ArrayList<>();
                int numberOfIntermediateStops = Bits32_24_8.unpack8(payload(currentCriteria));
                for (int j = 0; j < numberOfIntermediateStops; j++) {
                    intermediateStops.add(
                            new Journey.Leg.IntermediateStop(
                                    createStop(connections.arrStopId(connectionID), timeTable),
                                    createLocalDateTime(connections.arrMins(connectionID), profile),
                                    createLocalDateTime(connections.depMins(connections.nextConnectionId(connectionID)), profile)
                            )
                    );
                    connectionID = connections.nextConnectionId(connectionID);
                }

                transportArrStop = createStop(connections.arrStopId(connectionID), timeTable);
                transportArrTime = createLocalDateTime(connections.arrMins(connectionID), profile);

                legsList.add(new Journey.Leg.Transport(
                        transportDepStop,
                        transportDepTime,
                        transportArrStop,
                        transportArrTime,
                        intermediateStops,
                        timeTable.routes().vehicle(trips.routeId(depStopID)),
                        timeTable.routes().name(trips.routeId(depStopID)),
                        trips.destination(depStopID)
                ));

                if (changes(currentCriteria) != 0) {

                    int footStationDepId = getStationId(connections.arrStopId(connectionID), timeTable);
                    LocalDateTime footDepTime = createLocalDateTime(connections.arrMins(connectionID), profile);

                    currentCriteria = profile.forStation(footStationDepId)
                            .get(arrMins(currentCriteria), changes(currentCriteria) - 1);
                    connectionID = Bits32_24_8.unpack24(payload(currentCriteria));

                    int footStationArrId = getStationId(connections.depStopId(connectionID), timeTable);
                    LocalDateTime footArrTime = footDepTime.plusMinutes(
                            timeTable.transfers().minutesBetween(footStationDepId, footStationArrId)
                    );

                    transportDepStop = createStop(connections.depStopId(connectionID), timeTable);

                    legsList.add(new Journey.Leg.Foot(
                            transportArrStop,
                            footDepTime,
                            transportDepStop,
                            footArrTime
                    ));
                }
            }

            //add a final Leg.Foot if necessary
            if (profile.arrStationId() != getStationId(connections.arrStopId(connectionID), timeTable)) {
                LocalDateTime footDepTime = createLocalDateTime(connections.arrMins(connectionID), profile);
                LocalDateTime footArrTime = footDepTime.plusMinutes(
                        timeTable.transfers().minutesBetween(
                                profile.arrStationId(),
                                getStationId(connections.arrStopId(connectionID), timeTable)
                        ));

                legsList.add(new Journey.Leg.Foot(
                        transportArrStop,
                        footDepTime,
                        createStop(profile.arrStationId(), timeTable),
                        footArrTime
                ));
            }

            journeysList.add(new Journey(legsList));
        });

        journeysList.sort(Comparator
                .comparing(Journey::depTime)
                .thenComparing(Journey::arrTime));

        return journeysList;
    }

    private static Stop createStop(int stopId, TimeTable timeTable){
        Stations stations = timeTable.stations();

        int stationId = getStationId(stopId,timeTable);

        return new Stop(
                stations.name(stationId),
                timeTable.platformName(stopId),
                stations.longitude(stationId),
                stations.latitude(stationId)
        );
    }

//    private static Journey.Leg.Foot createLegFoot(int depStopId, int arrStopId, Profile profile) {
//        int footStationDepId = getStationId(depStopId,profile);
//        int footStationArrId = getStationId(arrStopId,profile);
//        LocalDateTime footDepTime = createLocalDateTime(depStopId,profile);
//        LocalDateTime footArrTime = footDepTime.plusMinutes(profile.timeTable().transfers().minutesBetween(footStationDepId,footStationArrId));
//        return new Journey.Leg.Foot(
//
//        )
//    }

    private static LocalDateTime createLocalDateTime(int mins,Profile profile){
        return LocalDateTime.of(profile.date(), LocalTime.of(0, 0, 0)).plusMinutes(mins);
    }

    private static int getStationId(int stopId, TimeTable timeTable) {
        return timeTable.isStationId(stopId) ?
                stopId:
                timeTable.platforms().stationId(timeTable.stationId(stopId));
    }
}
