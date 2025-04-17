package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
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

}
