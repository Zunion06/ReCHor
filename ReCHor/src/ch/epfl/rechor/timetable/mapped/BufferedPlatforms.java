package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Platforms;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U16;

public final class BufferedPlatforms implements Platforms {
    StructuredBuffer structuredBuffer;
    String[] stringTable;

    public final static int NAME_ID=0;
    public final static int STATION_ID=1;

    public BufferedPlatforms(List<String> stringTable, ByteBuffer buffer){
        this.structuredBuffer=new StructuredBuffer(new Structure(new Structure.Field(NAME_ID, U16),
                new Structure.Field(STATION_ID,U16)),buffer);
        this.stringTable=new String[stringTable.size()];
        stringTable.toArray(this.stringTable);
    }

    @Override
    public String name(int id) {
        return stringTable[structuredBuffer.getU16(NAME_ID,id)];
    }

    @Override
    public int stationId(int id) {
        return structuredBuffer.getU16(STATION_ID,id);
    }

    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
