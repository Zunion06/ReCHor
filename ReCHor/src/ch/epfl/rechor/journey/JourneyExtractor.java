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

            LocalDateTime transportArrTime;
            int connectionID = Bits32_24_8.unpack24(payload(profileCriteria));
            Stop transportDepStop = createStop(connections.depStopId(connectionID),profile);

            if (depStationId != getStationId(connections.depStopId(connectionID),profile)) {
                legsList.add(new Journey.Leg.Foot(
                        createStop(depStationId,profile),
                        createLocalDateTime(depMins(profileCriteria),profile),
                        transportDepStop,
                        createLocalDateTime(depMins(profileCriteria)+profile.timeTable().transfers().minutesBetween(depStationId,getStationId(connections.depStopId(connectionID),profile)),profile)
                ));
            }

            Stop transportArrStop = null;
            long currentCriteria = profileCriteria;
            for (int i = 0; i <= changes(profileCriteria); i++) {
                LocalDateTime transportDepTime = createLocalDateTime(connections.depMins(connectionID),profile);
                int depStopID = connections.depStopId(connectionID);

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

                transportArrStop = createStop(connections.arrStopId(connectionID),profile);
                transportArrTime = createLocalDateTime(connections.arrMins(connectionID),profile);

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

                    int footStationDepId = getStationId(connections.arrStopId(connectionID),profile);

                    LocalDateTime footDepTime = createLocalDateTime(connections.arrMins(connectionID),profile);

                    currentCriteria = profile.forStation(footStationDepId).get(arrMins(currentCriteria),changes(currentCriteria)-1);
                    connectionID = Bits32_24_8.unpack24(payload(currentCriteria));

                    int footStationArrId = getStationId(connections.depStopId(connectionID),profile);

                    LocalDateTime footArrTime = footDepTime.plusMinutes(profile.timeTable().transfers().minutesBetween(footStationDepId,footStationArrId));

                    transportDepStop = createStop(connections.depStopId(connectionID),profile);

                    legsList.add(new Journey.Leg.Foot(
                            transportArrStop,
                            footDepTime,
                            transportDepStop,
                            footArrTime
                    ));
                }
            }
            if (profile.arrStationId() != getStationId(connections.arrStopId(connectionID),profile)){
                LocalDateTime footDepTime = createLocalDateTime(connections.arrMins(connectionID),profile);
                LocalDateTime footArrTime = footDepTime.plusMinutes(profile.timeTable().transfers().minutesBetween(profile.arrStationId(), getStationId(connections.arrStopId(connectionID),profile)));
                legsList.add(new Journey.Leg.Foot(
                        transportArrStop,
                        footDepTime,
                        createStop(profile.arrStationId(),profile),
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

    private static Stop createStop(int stopId, Profile profile){
        Stations stations = profile.timeTable().stations();
        TimeTable timeTable = profile.timeTable();

        int stationId = getStationId(stopId,profile);

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

    private static int getStationId(int stopId, Profile profile) {
        return profile.timeTable().isStationId(stopId) ?
                stopId:
                profile.timeTable().platforms().stationId(profile.timeTable().stationId(stopId));
    }
}
