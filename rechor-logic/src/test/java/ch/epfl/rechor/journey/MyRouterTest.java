package ch.epfl.rechor.journey;

import static ch.epfl.HelperProfile.testPath;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;

import org.junit.Test;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.JourneyExtractor;
import ch.epfl.rechor.journey.JourneyIcalConverter;
import ch.epfl.rechor.journey.Profile;
import ch.epfl.rechor.journey.Router;
import ch.epfl.rechor.timetable.CachedTimeTable;
import ch.epfl.rechor.timetable.Stations;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;

public class MyRouterTest {

    static int stationId(Stations stations, String stationName) {
        for(int i=0;i<stations.size();i++){
            if(stations.name(i).equals(stationName)){
                return i;
            }
        }
        return -1;
    }

    @Test
    public void testWithJourneyExtractor() throws IOException {
        long tStart = System.nanoTime();

        TimeTable timeTable =
                new CachedTimeTable(FileTimeTable.in(testPath("timetable")));
        Stations stations = timeTable.stations();
        LocalDate date = LocalDate.of(2025, Month.MARCH, 18);
        int depStationId = stationId(stations, "Ecublens VD, EPFL");
        int arrStationId = stationId(stations, "Gruyères");
        Router router = new Router(timeTable);
        Profile profile = router.profile(date, arrStationId);
        Journey journey = JourneyExtractor
                .journeys(profile, depStationId)
                .get(32);
        System.out.println(JourneyIcalConverter.toIcalendar(journey));
        System.out.println();
        System.out.println(FormatterFr.formatLeg((Journey.Leg.Transport)journey.legs().get(0)));

        double elapsed = (System.nanoTime() - tStart) * 1e-9;
        System.out.printf("Temps écoulé : %.3f s%n", elapsed);
    }

    @Test
    public void testWOJourneyExtractor() throws IOException {
        TimeTable timeTable =
                new CachedTimeTable(FileTimeTable.in(testPath("timetable")));
        Stations stations = timeTable.stations();
        LocalDate date = LocalDate.of(2025, Month.MARCH, 18);
        int depStationId = stationId(stations, "Ecublens VD, EPFL");
        int arrStationId = stationId(stations, "Gruyères");
        Router router = new Router(timeTable);
        Profile profile = router.profile(date, arrStationId);
        System.out.println("paretofront:");
        System.out.println(profile.forStation(depStationId).toString());
    }



}
