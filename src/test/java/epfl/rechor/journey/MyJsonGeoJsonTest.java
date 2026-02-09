package epfl.rechor.journey;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

import static epfl.HelperProfile.testPath;
import epfl.rechor.timetable.TimeTable;
import epfl.rechor.timetable.mapped.FileTimeTable;

public class MyJsonGeoJsonTest {

    @Test
    void JourneyGeoJsonConverterToGeoJsonWorks(){
        assertDoesNotThrow(()->{
            TimeTable t = FileTimeTable.in(testPath("timetable"));
            LocalDate date = LocalDate.of(2025, Month.MARCH, 18);
            Profile p = MyJourneyExtractorTest.readProfile(t, date, 11486);
            List<Journey> js = JourneyExtractor.journeys(p, 7872);
            System.out.println(JourneyGeoJsonConverter.toGeoJson(js.get(32)));
            String url = "https://umap.osm.ch/fr/map/?data="+ JourneyGeoJsonConverter.toGeoJson(js.get(32));
            String encodedUrl = url.replace("\"", "%22").replace("[", "%5B").replace("]", "%5D").replace("{", "%7B").replace("}", "%7D");
            if (!GraphicsEnvironment.isHeadless()) {
                    Desktop desktop = Desktop.getDesktop();
                    if (desktop.isSupported(Desktop.Action.BROWSE)) {
                            desktop.browse(new URI(encodedUrl));
                    }
            } else {
                    System.out.println("Mode headless : navigateur non ouvert.");
            }
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
                    180,
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
