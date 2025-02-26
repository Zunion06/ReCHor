package ch.epfl.rechor;

import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.JourneyIcalConverter;
import ch.epfl.rechor.journey.Stop;
import ch.epfl.rechor.journey.Vehicle;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
public class MyIcalBuilderTest{
public static void main(String[] args) {
    var s1 = new Stop("Ecublens VD, EPFL", null, 6.566141, 46.522196);
    var s2 = new Stop("Renens VD, gare", null, 6.578519, 46.537619);
    var s3 = new Stop("Renens VD", "4", 6.578935, 46.537042);
    var s4 = new Stop("Lausanne", "5", 6.629092, 46.516792);
    var s5 = new Stop("Lausanne", "1", 6.629092, 46.516792);
    var s6 = new Stop("Romont FR", "2", 6.911811, 46.693508);
    var s7 = new Stop("Arrêt Random", "P2", 6.999811, 46.699908);

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

    // 3. Put legs into a Journey
    List<Journey.Leg> legs = List.of(l1, l2, l3, l4, l5);
    Journey journey = new Journey(legs);

    // 4. Convert to iCalendar
    String icalString = JourneyIcalConverter.toIcalendar(journey);

    // 5. Print and check
    System.out.println(icalString);

}
}