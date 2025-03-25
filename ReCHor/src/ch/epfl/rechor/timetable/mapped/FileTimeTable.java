package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.timetable.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static java.nio.file.Files.readAllLines;
import static java.time.temporal.WeekFields.ISO;

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

    public static TimeTable in(Path directory) throws IOException {
        List<String> string =  List.copyOf(readAllLines(directory.resolve("strings.txt"), StandardCharsets.ISO_8859_1));
        BufferedStations bufferedStations;
        BufferedPlatforms bufferedPlatforms;
        BufferedRoutes bufferedRoutes;
        BufferedStationAliases bufferedStationAliases;
        BufferedTransfers bufferedTransfers;

        try(FileChannel channel = FileChannel.open(directory.resolve("stations.bin"));) {
            bufferedStations = new BufferedStations(string,channel.map(READ_ONLY,0,channel.size()));
        }

        try(FileChannel channel = FileChannel.open(directory.resolve("platforms.bin"));) {
            bufferedPlatforms = new BufferedPlatforms(string,channel.map(READ_ONLY,0,channel.size()));
        }

        try(FileChannel channel = FileChannel.open(directory.resolve("routes.bin"));) {
            bufferedRoutes = new BufferedRoutes(string,channel.map(READ_ONLY,0,channel.size()));
        }

        try(FileChannel channel = FileChannel.open(directory.resolve("station-aliases.bin"));) {
            bufferedStationAliases = new BufferedStationAliases(string,channel.map(READ_ONLY,0,channel.size()));
        }

        try(FileChannel channel = FileChannel.open(directory.resolve("transfers.bin"));) {
            bufferedTransfers = new BufferedTransfers(channel.map(READ_ONLY,0,channel.size()));
        }

        return new FileTimeTable(directory,string,bufferedStations,bufferedStationAliases,bufferedPlatforms,bufferedRoutes,bufferedTransfers);
    }

    @Override
    public Trips tripsFor(LocalDate date) {
        BufferedTrips bufferedTrips;
        try(FileChannel channel = FileChannel.open(directory.resolve(date.toString()).resolve("trips.bin"))) {
            bufferedTrips = new BufferedTrips(stringTable(),channel.map(READ_ONLY,0,channel.size()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return bufferedTrips;
    }

    @Override
    public Connections connectionsFor(LocalDate date) {
        ByteBuffer connectionBuffer;
        ByteBuffer connectionSuccBuffer;

        try(FileChannel channel = FileChannel.open(directory.resolve(date.toString()).resolve("connections.bin"))){
            connectionBuffer = channel.map(READ_ONLY,0,channel.size());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        try(FileChannel channel = FileChannel.open(directory.resolve(date.toString()).resolve("connections-succ.bin"))){
            connectionSuccBuffer = channel.map(READ_ONLY,0,channel.size());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return new BufferedConnections(connectionBuffer,connectionSuccBuffer);
    }
}
