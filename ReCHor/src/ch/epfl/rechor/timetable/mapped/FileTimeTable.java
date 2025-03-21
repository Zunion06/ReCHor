package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.*;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/**
 * represent the timetable of public transports for the given values
 * @param directory path to the file with the necessary timetable
 * @param stringTable list of strings to be used in the timetable
 * @param stations stations
 * @param stationAliases aliases of the stations
 * @param platforms platforms
 * @param routes routes
 * @param transfers transfers
 */
public record FileTimeTable(Path directory, List<String> stringTable, Stations stations, StationAliases stationAliases, Platforms platforms, Routes routes, Transfers transfers) implements TimeTable {

    public TimeTable in(Path directory) throws IOException {
        return null;
    }

    @Override
    public Trips tripsFor(LocalDate date) {
        return null;
    }

    @Override
    public Connections connectionsFor(LocalDate date) {
        return null;
    }
}
