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

import static org.junit.Assert.*;

public class MyIcalBuilderTest{
    @Test
    void testAddStringValue() {
        IcalBuilder builder = new IcalBuilder();
        builder.add(IcalBuilder.Name.SUMMARY, "Test Event");
        assertTrue(builder.build().contains("SUMMARY:Test Event"));
    }

    @Test
    void testAddDateTimeValue() {
        IcalBuilder builder = new IcalBuilder();
        LocalDateTime dateTime = LocalDateTime.of(2025, 2, 25, 10, 30);
        builder.add(IcalBuilder.Name.DTSTART, dateTime);
        assertTrue(builder.build().contains("DTSTART:20250225T103000"));
    }

    @Test
    void testBeginAndEndComponent() {
        IcalBuilder builder = new IcalBuilder();
        builder.begin(IcalBuilder.Component.VCALENDAR);
        builder.end();
        String output = builder.build();
        assertTrue(output.contains("BEGIN:VCALENDAR"));
        assertTrue(output.contains("END:VCALENDAR"));
    }

    @Test
    void testUnmatchedEndThrowsException() {
        IcalBuilder builder = new IcalBuilder();
        assertThrows(IllegalArgumentException.class, builder::end);
    }

    @Test
    void testBuildWithoutClosingComponentThrowsException() {
        IcalBuilder builder = new IcalBuilder();
        builder.begin(IcalBuilder.Component.VCALENDAR);
        assertThrows(IllegalArgumentException.class, builder::build);
    }

}