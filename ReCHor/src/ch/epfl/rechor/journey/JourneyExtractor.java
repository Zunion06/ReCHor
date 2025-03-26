package ch.epfl.rechor.journey;

import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.timetable.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import static ch.epfl.rechor.journey.PackedCriteria.*;

public final class JourneyExtractor {
    private JourneyExtractor() {}

    public static List<Journey> journeys(Profile profile, int depStationId) {

        ParetoFront pf = profile.forStation(depStationId);
        Connections connections = profile.connections();
        Trips trips = profile.trips();

        List<Journey> journeysList = new ArrayList<>();
        pf.forEach((profileCriteria) -> {
            List<Journey.Leg> legsList = new ArrayList<>();

            int connectionID = Bits32_24_8.unpack24(payload(profileCriteria));
            Stop transportDepStop = createStop(connections.depStopId(connectionID),profile);
            Stop transportArrStop;
            long currentCriteria = profileCriteria;
            for (int i = 0; i <= changes(profileCriteria); i++) {
                LocalDateTime transportDepTime = createLocalDateTime(connections.depMins(connectionID),profile);
                int initialConnectionID = connectionID;

                List<Journey.Leg.IntermediateStop> intermediateStops = new ArrayList<>();
                int numberOfIntermediateStops = Bits32_24_8.unpack8(payload(currentCriteria));
                for (int j = 0; j < numberOfIntermediateStops; j++) {
                    intermediateStops.add(
                            new Journey.Leg.IntermediateStop(
                                    createStop(connections.arrStopId(connectionID),profile),
                                    createLocalDateTime(connections.arrMins(connectionID),profile),
                                    createLocalDateTime(connections.depMins(connections.nextConnectionId(connectionID)),profile)
                            )
                    );
                    connectionID = connections.nextConnectionId(connectionID);
                }

                int depStopID = connections.depStopId(initialConnectionID);
                int arrStopID = connections.arrStopId(connectionID);

                transportArrStop = createStop(connections.arrStopId(connectionID),profile);
                LocalDateTime transportArrTime=createLocalDateTime(connections.arrMins(connectionID),profile);

                legsList.add(new Journey.Leg.Transport(
                        transportDepStop,
                        transportDepTime,
                        transportArrStop,
                        transportArrTime,
                        intermediateStops,
                        profile.timeTable().routes().vehicle(trips.routeId(depStopID)),
                        profile.timeTable().routes().name(trips.routeId(depStopID)),
                        trips.destination(depStopID)
                ));

                if (changes(currentCriteria) != 0){

                    int footStationDepId = profile.timeTable().isStationId(connections.arrStopId(connectionID)) ?
                            connections.depStopId(connectionID):
                            profile.timeTable().platforms().stationId(profile.timeTable().stationId(connections.arrStopId(connectionID)));

                    LocalDateTime footDepTime = createLocalDateTime(connections.arrMins(connectionID),profile);

                    currentCriteria = profile.forStation(footStationDepId).get(arrMins(currentCriteria),changes(currentCriteria)-1);
                    connectionID = Bits32_24_8.unpack24(payload(currentCriteria));

                    int footStationArrId = profile.timeTable().isStationId(connections.depStopId(connectionID)) ?
                            connections.depStopId(connectionID):
                            profile.timeTable().platforms().stationId(profile.timeTable().stationId(connections.depStopId(connectionID)));

                    LocalDateTime footArrTime = footDepTime.plusMinutes(profile.timeTable().transfers().minutesBetween(footStationDepId,footStationArrId));

                    transportDepStop = createStop(footStationArrId,profile);

                    legsList.add(new Journey.Leg.Foot(
                            transportArrStop,
                            footDepTime,
                            transportDepStop,
                            footArrTime
                    ));
                }
            }
            journeysList.add(new Journey(legsList));

        });

        journeysList.sort(Comparator
                .comparing(Journey::depTime)
                .thenComparing(Journey::arrTime));

        return journeysList;
    }

    private static Stop createStop(int stopID, Profile profile){
        Platforms platforms = profile.timeTable().platforms();
        Stations stations = profile.timeTable().stations();
        TimeTable timeTable = profile.timeTable();

        int stationId = timeTable.isStationId(stopID) ?
                stopID:
                profile.timeTable().platforms().stationId(profile.timeTable().stationId(stopID));

        return new Stop(
                stations.name(stationId),
                timeTable.platformName(stopID),
                stations.longitude(stationId),
                stations.latitude(stationId)
        );
    }

    private static LocalDateTime createLocalDateTime(int mins,Profile profile){
        return LocalDateTime.of(profile.date(), LocalTime.of(0, 0, 0)).plusMinutes(mins);
    }

}
