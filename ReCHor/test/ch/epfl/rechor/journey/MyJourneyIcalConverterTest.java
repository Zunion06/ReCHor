package ch.epfl.rechor.journey;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class MyJourneyIcalConverterTest {
    public static Journey exampleJourney() {
        var s1 = new Stop("Ecublens VD, EPFL", null, 6.566141, 46.522196);
        var s2 = new Stop("Renens VD, gare", null, 6.578519, 46.537619);
        var s3 = new Stop("Renens VD", "4", 6.578935, 46.537042);
        var s4 = new Stop("Lausanne", "5", 6.629092, 46.516792);
        var s5 = new Stop("Lausanne", "1", 6.629092, 46.516792);
        var s6 = new Stop("Romont FR", "2", 6.911811, 46.693508);
        var s7 = new Stop("Romont FR", "1", 6.911811, 46.693508);
        var s8 = new Stop("Bulle", "2", 6.999811, 46.699908);
        var s9 = new Stop("Bulle", "4", 6.999811, 46.699908);
        var s10 = new Stop("Gruyères", "2", 6.999811, 46.699908);

        var d = LocalDate.of(2025, Month.FEBRUARY, 18);
        var l1 = new Journey.Leg.Transport(
                s1,
                d.atTime(16, 13),
                s2,
                d.atTime(16, 19),
                List.of(),
                Vehicle.METRO,
                "m1",
                "Renens VD, gare");

        var l2 = new Journey.Leg.Foot(s2, d.atTime(16, 19), s3, d.atTime(16, 22));

        var l3 = new Journey.Leg.Transport(
                s3,
                d.atTime(16, 26),
                s4,
                d.atTime(16, 33),
                List.of(),
                Vehicle.TRAIN,
                "R4",
                "Bex");

        var l4 = new Journey.Leg.Foot(s4, d.atTime(16, 33), s5, d.atTime(16, 38));

        var l5 = new Journey.Leg.Transport(
                s5,
                d.atTime(16, 40),
                s6,
                d.atTime(17, 13),
                List.of(),
                Vehicle.TRAIN,
                "IR15",
                "Luzern");

        var l6 = new Journey.Leg.Foot(s6, d.atTime(17, 13), s7, d.atTime(17, 16));

        var l7 = new Journey.Leg.Transport(
                s7,
                d.atTime(17, 22),
                s8,
                d.atTime(17, 41),
                List.of(),
                Vehicle.TRAIN,
                "IR15",
                "Luzern");

        var l8 = new Journey.Leg.Foot(s8, d.atTime(17, 41), s9, d.atTime(17, 44));

        var l9 = new Journey.Leg.Transport(
                s9,
                d.atTime(17, 50),
                s10,
                d.atTime(17, 57),
                List.of(),
                Vehicle.TRAIN,
                "IR15",
                "Luzern");

        // 3. Put legs into a Journey
        List<Journey.Leg> legs = List.of(l1, l2, l3, l4, l5, l6, l7, l8, l9);
        Journey journey = new Journey(legs);

        // 4. Convert to iCalendar

        String icalString = JourneyIcalConverter.toIcalendar(journey);

        // 5. Print and check
        return journey;

    }

//    @Test
//    void EqualTest() {
//        String expected = "BEGIN:VCALENDAR\n" +
//                "VERSION:2.0\n" +
//                "PRODID:ReCHor\n" +
//                "BEGIN:VEVENT\n" +
//                "UID:bbff399f-d276-4052-8798-9c2f227353de\n" +
//                "DTSTAMP:20250216T182750\n" +
//                "DTSTART:20250218T161300\n" +
//                "DTEND:20250218T175700\n" +
//                "SUMMARY:Ecublens VD, EPFL → Gruyères\n" +
//                "DESCRIPTION:16h13 Ecublens VD, EPFL → Renens VD, gare (arr. 16h19)\\ntrajet \n" +
//                " à pied (3 min)\\n16h26 Renens VD (voie 4) → Lausanne (arr. 16h33 voie 5)\\nc\n" +
//                " hangement (5 min)\\n16h40 Lausanne (voie 1) → Romont FR (arr. 17h13 voie 2)\n" +
//                " \\nchangement (3 min)\\n17h22 Romont FR (voie 1) → Bulle (arr. 17h41 voie 2)\n" +
//                " \\nchangement (3 min)\\n17h50 Bulle (voie 4) → Gruyères (arr. 17h57 voie 2)\n" +
//                "END:VEVENT\n" +
//                "END:VCALENDAR";
//        System.out.println(testJourney1());
//        System.out.println(expected);
//        assertEquals(testJourney1(), expected);
//
//
//    }

    @Test
    void journeyIcalConverterToIcalendarWorks() {
        var dateFmt = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
        var vcalendarContext = List.of("VCALENDAR");
        var veventContext = List.of("VCALENDAR", "VEVENT");

        var actual = JourneyIcalConverter.toIcalendar(exampleJourney());
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
                    Assertions.assertEquals("2.0", value);
                    Assertions.assertEquals(vcalendarContext, context);
                }
                case "PRODID" -> {
                    Assertions.assertEquals("ReCHor", value);
                    Assertions.assertEquals(vcalendarContext, context);
                }
                case "UID" -> {
                    assertFalse(value.isBlank());
                    Assertions.assertEquals(veventContext, context);
                }
                case "DTSTAMP" -> {
                    var timeStamp = LocalDateTime.parse(value, dateFmt);
                    var elapsed = Duration.between(timeStamp, LocalDateTime.now()).abs();
                    assertTrue(elapsed.compareTo(Duration.ofSeconds(10)) < 0);
                    Assertions.assertEquals(veventContext, context);
                }
                case "DTSTART" -> {
                    var timeStart = LocalDateTime.parse(value, dateFmt);
                    Assertions.assertEquals(exampleJourney().depTime(), timeStart);
                    Assertions.assertEquals(veventContext, context);
                }
                case "DTEND" -> {
                    var timeEnd = LocalDateTime.parse(value, dateFmt);
                    Assertions.assertEquals(exampleJourney().arrTime(), timeEnd);
                    Assertions.assertEquals(veventContext, context);
                }
                case "SUMMARY" -> {
                    Assertions.assertEquals("Ecublens VD, EPFL → Gruyères", value);
                    Assertions.assertEquals(veventContext, context);
                }
                case "DESCRIPTION" -> {
                    Assertions.assertEquals(
                            "16h13 Ecublens VD, EPFL → Renens VD, gare (arr. 16h19)\\n" +
                                    "trajet à pied (3 min)\\n" +
                                    "16h26 Renens VD (voie 4) → Lausanne (arr. 16h33 voie 5)\\n" +
                                    "changement (5 min)\\n" +
                                    "16h40 Lausanne (voie 1) → Romont FR (arr. 17h13 voie 2)\\n" +
                                    "changement (3 min)\\n" +
                                    "17h22 Romont FR (voie 1) → Bulle (arr. 17h41 voie 2)\\n" +
                                    "changement (3 min)\\n" +
                                    "17h50 Bulle (voie 4) → Gruyères (arr. 17h57 voie 2)"
                            , value);
                    Assertions.assertEquals(veventContext, context);
                }
            }
        }
        Assertions.assertEquals(Set.of(), requiredNames);
    }
}