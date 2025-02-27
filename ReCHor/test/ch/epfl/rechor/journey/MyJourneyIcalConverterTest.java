package ch.epfl.rechor.journey;

//import org.testng.annotations.Test;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MyJourneyIcalConverterTest {
    public static String testJourney1() {
        var s1 = new Stop("Ecublens VD, EPFL", null, 6.566141, 46.522196);
        var s2 = new Stop("Renens VD, gare", null, 6.578519, 46.537619);
        var s3 = new Stop("Renens VD", "4", 6.578935, 46.537042);
        var s4 = new Stop("Lausanne", "5", 6.629092, 46.516792);
        var s5 = new Stop("LOLOLOLOLOLOLOLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOL", "1", 6.629092, 46.516792);
        var s6 = new Stop("Romont FR", "2", 6.911811, 46.693508);
        var s7 = new Stop("Romont FR", "1", 6.911811, 46.693508);
        var s8 = new Stop("LOLOLOLOLOLOLOLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOL", "2", 6.999811, 46.699908);
        var s9 = new Stop("LOLOLOLOLOLOLOLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOL", "4", 6.999811, 46.699908);
        var s10 = new Stop("LOLOLOLOLOLOLOLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOLOLOLOLOLOLOLOLLOLOLOLOLOL", "2", 6.999811, 46.699908);

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
        return (icalString);

    }

    public static String testJourney2() {
        var s1 = new Stop("Ecublens VD, EPFL", null, 6.566141, 46.522196);
        var s2 = new Stop("Renens VD, gare", null, 6.578519, 46.537619);
        var s3 = new Stop("Renens VD", "4", 6.578935, 46.537042);
        var s4 = new Stop("Lausanne", "5", 6.629092, 46.516792);
        var s5 = new Stop("Lausanne", "1", 6.629092, 46.516792);
        var s6 = new Stop("Romont FR", "2", 6.911811, 46.693508);
        var s7 = new Stop("Romont FR", "1", 6.911811, 46.693508);
        var s8 = new Stop("Bulle", "2", 6.999811, 46.699908);
        var s9 = new Stop("Bulle", "4", 6.999811, 46.699908);
        var s10 = new Stop("LOLOLOLOLOLOLOLOLOLOLOLOL", "2", 6.999811, 46.699908);

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
        return (icalString);

    }

    @Test
    void EqualTest() {
        String expected = "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:ReCHor\n" +
                "BEGIN:VEVENT\n" +
                "UID:bbff399f-d276-4052-8798-9c2f227353de\n" +
                "DTSTAMP:20250216T182750\n" +
                "DTSTART:20250218T161300\n" +
                "DTEND:20250218T175700\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\n" +
                "DESCRIPTION:16h13 Ecublens VD, EPFL → Renens VD, gare (arr. 16h19)\\ntrajet \n" +
                " à pied (3 min)\\n16h26 Renens VD (voie 4) → Lausanne (arr. 16h33 voie 5)\\nc\n" +
                " hangement (5 min)\\n16h40 Lausanne (voie 1) → Romont FR (arr. 17h13 voie 2)\n" +
                " \\nchangement (3 min)\\n17h22 Romont FR (voie 1) → Bulle (arr. 17h41 voie 2)\n" +
                " \\nchangement (3 min)\\n17h50 Bulle (voie 4) → Gruyères (arr. 17h57 voie 2)\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR";
        System.out.println(testJourney1());
        System.out.println(expected);
        assertEquals(testJourney1(), expected);


    }
}