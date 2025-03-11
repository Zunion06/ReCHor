package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Platforms;

import java.nio.ByteBuffer;
import java.util.List;

public final class BufferedPlatforms implements Platforms {

    public BufferedPlatforms(List<String> stringTable, ByteBuffer buffer){

    }

    @Override
    public String name(int id) {
        return "";
    }

    @Override
    public int stationId(int id) {
        return 0;
    }

    @Override
    public int size() {
        return 0;
    }
}
