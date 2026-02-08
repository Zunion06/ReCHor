package epfl.rechor.journey;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import static epfl.rechor.journey.JourneyExtractorTest.testPath;
import epfl.rechor.timetable.TimeTable;
import epfl.rechor.timetable.mapped.FileTimeTable;

public class MyProfileTest {

    @Test
    void builderEmptyParetoTest() throws IOException {
        Profile expectedProfile = testProfile();

        Profile.Builder builder = new Profile.Builder(expectedProfile.timeTable(),
                expectedProfile.date(), expectedProfile.arrStationId());
        Profile testProfile = builder.build();
        assertEquals(expectedProfile.date(),testProfile.date());
        assertEquals(expectedProfile.arrStationId(),testProfile.arrStationId());
        testProfile.trips();
        assertEquals(testProfile.timeTable().stations().size(),testProfile.stationFront().size());
        for(int i=0; i<testProfile.stationFront().size(); i++){
            assertEquals(ParetoFront.EMPTY,testProfile.stationFront().get(i));
        }

    }

    @Test
    void builderNotEmptyParetoTest() throws IOException {
        Profile expectedProfile = testProfile();
        ParetoFront.Builder paretoBuilder = new ParetoFront.Builder();
        for (var i = 0; i < 20; i += 1) {
            paretoBuilder.add(PackedCriteria.withDepMins(
                    PackedCriteria.pack(10 + i, 127 - i, i),
                    i - 10));
        }

        Profile.Builder builder = new Profile.Builder(expectedProfile.timeTable(),
                expectedProfile.date(), expectedProfile.arrStationId());
        builder.setForStation(1,paretoBuilder);
        assertThrows(IndexOutOfBoundsException.class, () ->
                builder.setForStation(expectedProfile.timeTable().stations().size()+1,paretoBuilder)
        );
        assertThrows(IndexOutOfBoundsException.class, () ->
                builder.setForTrip(expectedProfile.timeTable().tripsFor(expectedProfile.date()).size()+1,paretoBuilder)
        );
        Profile testProfile = builder.build();

        assertEquals(expectedProfile.date(),testProfile.date());
        assertEquals(expectedProfile.arrStationId(),testProfile.arrStationId());
        testProfile.trips();
        assertEquals(testProfile.timeTable().stations().size(),testProfile.stationFront().size());
        assertNotEquals(ParetoFront.EMPTY,testProfile.forStation(1));
        for(int i=2; i<testProfile.stationFront().size(); i++){
            assertEquals(ParetoFront.EMPTY,testProfile.stationFront().get(i));
        }
        assertThrows(IndexOutOfBoundsException.class, () ->
                testProfile.forStation(testProfile.timeTable().stations().size()+1)
        );

    }

    Profile testProfile() throws IOException {
        TimeTable timeTable = FileTimeTable.in(testPath("timetable"));
        LocalDate date = LocalDate.of(2025, Month.MARCH, 18);
        int arrStationId = 11486;

        Path path = testPath("profile_" + date + "_" + arrStationId + ".txt");
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

}
