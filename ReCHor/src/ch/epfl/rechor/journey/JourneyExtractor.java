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
            long currentCriteria = profileCriteria;
            while (changes(currentCriteria) > 0){
                int initialConnectionID = connectionID;

                List<Journey.Leg.IntermediateStop> intermediateStops = new ArrayList<>();
                int numberOfIntermediateStops = Bits32_24_8.unpack8(payload(currentCriteria));
                for (int i = 0; i < numberOfIntermediateStops; i++) {
                    intermediateStops.add(
                            new Journey.Leg.IntermediateStop(
                                    createStop(connections.arrStopId(connectionID),profile),
                                    createLocalDateTime(connections.arrMins(connectionID)),
                                    createLocalDateTime(connections.depMins(connectionID))
                            )
                    );
                    connectionID = connections.nextConnectionId(connectionID);
                }

                int depStopID = connections.depStopId(initialConnectionID);
                int arrStopID = connections.arrStopId(connectionID);

                legsList.add(new Journey.Leg.Transport(
                        createStop(depStopID,profile),
                        createLocalDateTime(connections.depMins(initialConnectionID)),
                        createStop(arrStopID,profile),
                        createLocalDateTime(connections.arrMins(connectionID)),
                        intermediateStops,
                        profile.timeTable().routes().vehicle(trips.routeId(depStopID)),
                        profile.timeTable().routes().name(trips.routeId(depStopID)),
                        trips.destination(depStopID)
                ));

                connectionID = connections.nextConnectionId(connectionID);

                legsList.add(
                        new Journey.Leg.Foot(
                                createStop(connections.depStopId(connectionID),profile),
                                createLocalDateTime(connections.depMins(connectionID)),
                                createStop(connections.arrStopId(connectionID),profile),
                                createLocalDateTime(connections.arrMins(connectionID))
                        )
                );

                connectionID = connections.nextConnectionId(connectionID);
                currentCriteria = profile.forStation(connections.depStopId(connectionID)).get(arrMins(currentCriteria),changes(currentCriteria)-1);
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
        return new Stop(stations.name(platforms.stationId(stopID)),
                platforms.name(stopID),
                stations.longitude(platforms.stationId(stopID)),
                stations.latitude(platforms.stationId(stopID))
        );
    }

    private static LocalDateTime createLocalDateTime(int mins){
        return LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0)).plusMinutes(mins);
    }

}
