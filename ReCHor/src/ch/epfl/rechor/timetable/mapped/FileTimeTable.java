package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static java.nio.file.Files.readAllLines;

/**
 * represent the timetable of public transports for the given values
 * @param directory path to the file with the necessary timetable
 * @param stringTable list of strings to be used in the timetable
 * @param stations all stations of the timetable
 * @param stationAliases all aliases of the stations of the timetable
 * @param platforms all platforms of the timetable
 * @param routes all routes of the timetable
 * @param transfers all transfers of the timetable
 * @author Matthias Gibon (397827)
 */
public record FileTimeTable(Path directory, List<String> stringTable, Stations stations,
                            StationAliases stationAliases, Platforms platforms, Routes routes,
                            Transfers transfers) implements TimeTable {

    /**
     * return an instance of FileTimeTable taking all the information from the path to the given
     * directory
     * @param directory file path to the information used for the creation of the timetable
     * @return an instance of FileTimeTable with the information found in the directory
     * @throws IOException if any of the required timetable files (e.g., stations.bin,
     * platforms.bin, routes.bin, etc.) are missing, inaccessible, or corrupted
     */
    public static TimeTable in(Path directory) throws IOException {
        List<String> string =  List.copyOf(readAllLines(directory.resolve("strings.txt"),
                StandardCharsets.ISO_8859_1));
        BufferedStations bufferedStations;
        BufferedPlatforms bufferedPlatforms;
        BufferedRoutes bufferedRoutes;
        BufferedStationAliases bufferedStationAliases;
        BufferedTransfers bufferedTransfers;


        //reads the information of each needed files in the directory :
        try(FileChannel channel = FileChannel.open(directory.resolve("stations.bin"))) {
            bufferedStations =
                    new BufferedStations(string,channel.map(READ_ONLY,0,channel.size()));
        } //closes the channel implicitly with the try-with-resources

        try(FileChannel channel = FileChannel.open(directory.resolve("platforms.bin"))) {
            bufferedPlatforms =
                    new BufferedPlatforms(string,channel.map(READ_ONLY,0,channel.size()));
        }

        try(FileChannel channel = FileChannel.open(directory.resolve("routes.bin"))) {
            bufferedRoutes =
                    new BufferedRoutes(string,channel.map(READ_ONLY,0,channel.size()));
        }

        try(FileChannel channel = FileChannel.open(directory.resolve("station-aliases.bin"))){
            bufferedStationAliases =
                    new BufferedStationAliases(string,channel.map(READ_ONLY,0,channel.size()));
        }

        try(FileChannel channel = FileChannel.open(directory.resolve("transfers.bin"))) {
            bufferedTransfers =
                    new BufferedTransfers(channel.map(READ_ONLY,0,channel.size()));
        }

        return new FileTimeTable(directory,string,bufferedStations,bufferedStationAliases,
                bufferedPlatforms,bufferedRoutes,bufferedTransfers);
    }

    /**
     * gives the trips found at a given date
     * @param date date of the trips searched
     * @return all the trips at the given date
     * @throws UncheckedIOException if an I/O error occurs while reading the file
     */
    @Override
    public Trips tripsFor(LocalDate date) {
        BufferedTrips bufferedTrips;
        try(FileChannel channel = FileChannel.open(directory.resolve(date.toString())
                .resolve("trips.bin"))) {
            bufferedTrips = new BufferedTrips(stringTable(),
                    channel.map(READ_ONLY,0,channel.size()));

        } catch (IOException e) { //catch IOExceptions because the method cannot can not throw it
            throw new UncheckedIOException(e);
        }

        return bufferedTrips;
    }

    /**
     * gives the connections found at a given date
     * @param date date of the connections searched
     * @return all the connections at the given date
     * @throws UncheckedIOException if an I/O error occurs while reading the file
     */
    @Override
    public Connections connectionsFor(LocalDate date) {
        ByteBuffer connectionBuffer;
        ByteBuffer connectionSuccBuffer;

        try(FileChannel channel = FileChannel.open(directory.resolve(date.toString())
                .resolve("connections.bin"))){
            connectionBuffer = channel.map(READ_ONLY,0,channel.size());
        } catch (IOException e) { //catch IOExceptions because the method cannot can not throw it
            throw new UncheckedIOException(e);
        }

        try(FileChannel channel = FileChannel.open(directory.resolve(date.toString())
                .resolve("connections-succ.bin"))){
            connectionSuccBuffer = channel.map(READ_ONLY,0,channel.size());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return new BufferedConnections(connectionBuffer,connectionSuccBuffer);
    }
}
