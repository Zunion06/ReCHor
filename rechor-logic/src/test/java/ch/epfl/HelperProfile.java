package ch.epfl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.NoSuchElementException;

import ch.epfl.rechor.journey.ParetoFront;
import ch.epfl.rechor.journey.Profile;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;

public class HelperProfile {
    public static Path testPath(String path) {
        var maybeTestDataDir = System.getenv("RECHOR_TEST_DATA_DIR");
        return maybeTestDataDir != null
                ? Path.of(maybeTestDataDir).resolve(path)
                : Path.of(path);
    }

    public static Profile readProfile() throws IOException {
        var timeTable = FileTimeTable.in(testPath("timetable"));
        var date = LocalDate.of(2025, Month.MARCH, 18);
        var arrStationId = stationId(timeTable, "Gruyères");

        var path = testPath("profile_" + date + "_" + arrStationId + ".txt");
        try (BufferedReader r = Files.newBufferedReader(path)) {
            var profileB = new Profile.Builder(timeTable, date, arrStationId);
            var stationId = -1;
            String line;
            while ((line = r.readLine()) != null) {
                stationId += 1;
                if (line.isEmpty()) continue;
                var frontB = new ParetoFront.Builder();
                for (var t : line.split(","))
                    frontB.add(Long.parseLong(t, 16));
                profileB.setForStation(stationId, frontB);
            }
            return profileB.build();
        }
    }

    public static Profile readProfileUnchecked() {
        try {
            return readProfile();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static int stationId(TimeTable timeTable, String name) {
        var stations = timeTable.stations();
        for (var i = 0; i < stations.size(); i += 1)
            if (stations.name(i).equals(name)) return i;
        throw new NoSuchElementException();
    }

    private static final Profile PROFILE = readProfileUnchecked();

    public static Profile profile() {
        return PROFILE;
    }

}
