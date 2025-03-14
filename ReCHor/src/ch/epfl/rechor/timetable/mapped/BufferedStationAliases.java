package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.StationAliases;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.S32;
import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U16;

public final class BufferedStationAliases implements StationAliases {
    public final static int ALIAS_ID=0;
    public final static int STATION_NAME_ID=1;

    public final StructuredBuffer structuredBuffer;
    private final List<String> stringTable;

    public BufferedStationAliases(List<String> stringTable, ByteBuffer buffer){
        Structure stationALiasStructure = new Structure(
                new Structure.Field(ALIAS_ID, U16),
                new Structure.Field(STATION_NAME_ID,U16));
        this.structuredBuffer=new StructuredBuffer(stationALiasStructure, buffer);
        this.stringTable=stringTable;
    }

    @Override
    public String alias(int id) {
        return stringTable.get(structuredBuffer.getU16(ALIAS_ID,id));
    }

    @Override
    public String stationName(int id) {
        return stringTable.get(structuredBuffer.getU16(STATION_NAME_ID,id));
    }

    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
