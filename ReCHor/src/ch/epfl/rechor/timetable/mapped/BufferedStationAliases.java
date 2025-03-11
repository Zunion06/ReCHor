package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.StationAliases;

import java.nio.ByteBuffer;
import java.util.List;

public final class BufferedStationAliases implements StationAliases {

    public BufferedStationAliases(List<String> stringTable, ByteBuffer buffer){

    }

    @Override
    public String alias(int id) {
        return "";
    }

    @Override
    public String stationName(int id) {
        return "";
    }

    @Override
    public int size() {
        return 0;
    }
}
