package ch.epfl.rechor.journey;

import static ch.epfl.HelperProfile.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.JourneyExtractor;


class JourneyExtractorTest {
    @Test
    void journeyExtractorJourneysWorksWithSingleTransportLeg() {
        var timeTable = profile().timeTable();
        var depStationId = stationId(timeTable, "Le Pâquier-Montbarry");
        var journeys = JourneyExtractor.journeys(profile(), depStationId);
        assertEquals(39, journeys.size());
        for (var journey : journeys) {
            assertEquals(1, journey.legs().size());
            assertInstanceOf(Journey.Leg.Transport.class, journey.legs().getFirst());
        }
    }

    @Test
    void journeyExtractorJourneyWorksWithInitialFootLeg() {
        var timeTable = profile().timeTable();
        var depStationId = stationId(timeTable, "Le Pâquier-Montbarry, gare");
        var journeys = JourneyExtractor.journeys(profile(), depStationId);
        assertEquals(39, journeys.size());
        for (var journey : journeys) {
            assertEquals(2, journey.legs().size());
            assertInstanceOf(Journey.Leg.Foot.class, journey.legs().getFirst());
            assertInstanceOf(Journey.Leg.Transport.class, journey.legs().getLast());
        }
    }

    @Test
    void journeyExtractorJourneyWorksWithFinalFootLeg() {
        var timeTable = profile().timeTable();
        var depStationId = stationId(timeTable, "Pringy, village");
        var journeys = JourneyExtractor.journeys(profile(), depStationId);
        assertEquals(20, journeys.size());
        for (var journey : journeys) {
            assertEquals(2, journey.legs().size());
            assertInstanceOf(Journey.Leg.Transport.class, journey.legs().getFirst());
            assertInstanceOf(Journey.Leg.Foot.class, journey.legs().getLast());
        }
    }

    @Test
    void journeyExtractorJourneyWorksWithComplexJourney() {
        var timeTable = profile().timeTable();
        var d = profile().date();
        var depStationId = stationId(timeTable, "Martina, cunfin");
        var journeys = JourneyExtractor.journeys(profile(), depStationId);
        assertEquals(22, journeys.size());

        var j = journeys.get(6);
        assertEquals(13, j.legs().size());

        if (j.legs().get(4) instanceof Journey.Leg.Transport t) {
            assertEquals("Klosters Platz", t.depStop().name());
            assertEquals("2", t.depStop().platformName());
            assertEquals(46.869210, t.depStop().latitude(), 1e-6);
            assertEquals(9.880940, t.depStop().longitude(), 1e-6);

            assertEquals("Landquart", t.arrStop().name());
            assertEquals("6", t.arrStop().platformName());
            assertEquals(46.967440, t.arrStop().latitude(), 1e-6);
            assertEquals(9.554045, t.arrStop().longitude(), 1e-6);

            var sIt = t.intermediateStops().iterator();
            var s = sIt.next();
            assertEquals(d.atTime(10, 33), s.arrTime());
            assertEquals(d.atTime(10, 33), s.depTime());
            assertEquals("Klosters Dorf", s.stop().name());

            s = sIt.next();
            assertEquals(d.atTime(10, 47), s.arrTime());
            assertEquals(d.atTime(10, 48), s.depTime());
            assertEquals("Küblis", s.stop().name());

            s = sIt.next();
            assertEquals(d.atTime(10, 54), s.arrTime());
            assertEquals(d.atTime(10, 54), s.depTime());
            assertEquals("Jenaz", s.stop().name());

            s = sIt.next();
            assertEquals(d.atTime(10, 59), s.arrTime());
            assertEquals(d.atTime(11, 0), s.depTime());
            assertEquals("Schiers", s.stop().name());

            assertFalse(sIt.hasNext());
        } else fail();

        if (j.legs().get(5) instanceof Journey.Leg.Foot f) {
            assertEquals(d.atTime(11, 10), f.depTime());
            assertEquals(d.atTime(11, 14), f.arrTime());

            assertTrue(f.isTransfer());
        } else fail();
    }
}