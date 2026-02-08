package epfl.rechor.journey;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import static epfl.rechor.journey.JourneyExtractorTest.testPath;
import epfl.rechor.timetable.TimeTable;
import epfl.rechor.timetable.mapped.FileTimeTable;

public class MyJourneyExtractorTest {
    static Profile readProfile(TimeTable timeTable,LocalDate date,int arrStationId) throws IOException {
        Path path =
                testPath("profile_" + date + "_" + arrStationId + ".txt");
        try (BufferedReader r = Files.newBufferedReader(path)) {
            Profile.Builder profileB =
                    new Profile.Builder(timeTable, date, arrStationId);
            int stationId = -1;
            String line;
            while ((line = r.readLine()) != null) {
                stationId += 1;
                if (line.isEmpty()) continue;
                ParetoFront.Builder frontB = new ParetoFront.Builder();
                for (String t : line.split(","))
                    frontB.add(Long.parseLong(t, 16));
                profileB.setForStation(stationId, frontB);
            }
            return profileB.build();
        }
    }

    @Test
    void JourneyExtractorJourneysWorks() {
        assertDoesNotThrow(() -> {
            TimeTable t = FileTimeTable.in(testPath("timetable"));
            LocalDate date = LocalDate.of(2025, Month.MARCH, 18);
            Profile p = readProfile(t, date, 11486);
            List<Journey> js = JourneyExtractor.journeys(p, 7872);
            String j = JourneyIcalConverter.toIcalendar(js.get(32));
        });

    }

    @Test
    void journeyExtractorToIcalendarWorks() throws IOException{

        TimeTable t = FileTimeTable.in(testPath("timetable"));
        LocalDate date = LocalDate.of(2025, Month.MARCH, 18);
        Profile p = readProfile(t, date, 11486);
        List<Journey> js = JourneyExtractor.journeys(p, 7872);
        String j = JourneyIcalConverter.toIcalendar(js.get(32));

        var dateFmt = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
        var vcalendarContext = List.of("VCALENDAR");
        var veventContext = List.of("VCALENDAR", "VEVENT");

        var actual = JourneyIcalConverter.toIcalendar(js.get(32));
        var unfoldedActual = actual.replaceAll("\r\n ", "");
        var context = new ArrayList<String>();
        var requiredNames = new HashSet<>(
                Set.of("VERSION", "PRODID", "UID", "DTSTAMP", "DTSTART", "SUMMARY", "DESCRIPTION"));

        for (var line : unfoldedActual.split("\r\n")) {
            var colonLoc = line.indexOf(':');
            var name = line.substring(0, colonLoc);
            var value = line.substring(colonLoc + 1);
            requiredNames.remove(name);
            switch (name) {
                case "BEGIN" ->
                        context.addLast(value);
                case "END" -> {
                    assertFalse(context.isEmpty());
                    context.removeLast();
                }
                case "VERSION" -> {
                    assertEquals("2.0", value);
                    assertEquals(vcalendarContext, context);
                }
                case "PRODID" -> {
                    assertEquals("ReCHor", value);
                    assertEquals(vcalendarContext, context);
                }
                case "UID" -> {
                    assertFalse(value.isBlank());
                    assertEquals(veventContext, context);
                }
                case "DTSTAMP" -> {
                    var timeStamp = LocalDateTime.parse(value, dateFmt);
                    var elapsed = Duration.between(timeStamp, LocalDateTime.now()).abs();
                    assertTrue(elapsed.compareTo(Duration.ofSeconds(10)) < 0);
                    assertEquals(veventContext, context);
                }
                case "DTSTART" -> {
                    var timeStart = LocalDateTime.parse(value, dateFmt);
                    assertEquals(js.get(32).depTime(), timeStart);
                    assertEquals(veventContext, context);
                }
                case "DTEND" -> {
                    var timeEnd = LocalDateTime.parse(value, dateFmt);
                    assertEquals(js.get(32).arrTime(), timeEnd);
                    assertEquals(veventContext, context);
                }
                case "SUMMARY" -> {
                    assertEquals("Ecublens VD, EPFL → Gruyères", value);
                    assertEquals(veventContext, context);
                }
                case "DESCRIPTION" -> {
                    assertEquals(
                            "16h13 Ecublens VD, EPFL → Renens VD, gare (arr. 16h19)\\n" +
                                    "trajet à pied (3 min)\\n" +
                                    "16h26 Renens VD (voie 4) → Lausanne (arr. 16h33 voie 5)\\n" +
                                    "changement (5 min)\\n" +
                                    "16h40 Lausanne (voie 1) → Romont FR (arr. 17h13 voie 2)\\n"+"changement (3 min)\\n"+
                                    "17h22 Romont FR (voie 1) → Bulle (arr. 17h41 voie 2)\\n"+"changement (3 min)\\n"+
                                    "17h50 Bulle (voie 4) → Gruyères (arr. 17h57 voie 2)", value);
                }
            }
        }
        assertEquals(Set.of(), requiredNames);
    }


    Journey ExampleIfJourney1Foot1Transport(){
        var s1 = new Stop("Ecublens VD, EPFL", null, 6.566141, 46.522196);
        var s2 = new Stop("Renens VD, gare", null, 6.578519, 46.537619);
        var s3 = new Stop("Renens VD", "4", 6.578935, 46.537042);


        var d = LocalDate.of(2025, Month.FEBRUARY, 18);

        var l1 = new Journey.Leg.Foot(s1, d.atTime(16, 13), s2, d.atTime(16, 29));

        var l2 = new Journey.Leg.Transport(
                s2,
                d.atTime(16, 19),
                s3,
                d.atTime(16, 22),
                List.of(),
                Vehicle.METRO,
                "m1",
                "Renens VD");


        return new Journey(List.of(l1, l2));
    }

}

