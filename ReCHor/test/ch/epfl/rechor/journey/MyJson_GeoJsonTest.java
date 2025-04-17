package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class MyJson_GeoJsonTest {

    @Test
    void JourneyGeoJsonConverterToGeoJsonWorks(){
        assertDoesNotThrow(()->{
            TimeTable t = FileTimeTable.in(Path.of("timetable"));
            LocalDate date = LocalDate.of(2025, Month.MARCH, 18);
            Profile p = MyJourneyExtractorTest.readProfile(t, date, 11486);
            List<Journey> js = JourneyExtractor.journeys(p, 7872);
            System.out.println(JourneyGeoJsonConverter.toGeoJson(js.get(32)));
        });
    }

    @Test
    void JourneyGeoJsonConverterToGeoJsonWorks2(){
        assertDoesNotThrow(()->{
            List<Journey.Leg> legList = new ArrayList<>();
            LocalDateTime time = LocalDateTime.of(2,2,2,2,2,2);
            Stop arrStop = new Stop(
                    "",
                    "",
                    46.005,
                    -54
                    );
            legList.add(
                    new Journey.Leg.Transport(
                            new Stop(
                                    "",
                                    "",
                                    2,
                                    1
                            ),
                            time,
                            arrStop,
                            time,
                            List.of(new Journey.Leg.IntermediateStop(
                                    new Stop(
                                            "",
                                            "",
                                            23,
                                            -20.000006
                                    ),
                                    time,
                                    time
                            )),
                            Vehicle.TRAIN,
                            "",
                            ""
                    )
            );
            legList.add(new Journey.Leg.Foot(
                    arrStop,
                    time,
                    new Stop(
                            "",
                            "",
                            180,
                            -90
                    ),
                    time
            ));
            Journey journey = new Journey(legList);
            System.out.println(JourneyGeoJsonConverter.toGeoJson(journey));
        });
    }
}
