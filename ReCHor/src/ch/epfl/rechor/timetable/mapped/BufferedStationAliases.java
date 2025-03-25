package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.StationAliases;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.S32;
import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U16;

/**
 * used to access a list of stations aliases represented as flattened values
 */
public final class BufferedStationAliases implements StationAliases {
    private final static int ALIAS_ID=0;
    private final static int STATION_NAME_ID=1;

    private final StructuredBuffer structuredBuffer;
    private final List<String> stringTable;

    /**
     * create an instance of BufferedStationAliases to stock information about station aliases
     * @param stringTable table of string documenting the station aliases
     * @param buffer values to be set in the stations aliases information
     */
    public BufferedStationAliases(List<String> stringTable, ByteBuffer buffer){
        Structure stationALiasStructure = new Structure(
                new Structure.Field(ALIAS_ID, U16),
                new Structure.Field(STATION_NAME_ID,U16));
        this.structuredBuffer=new StructuredBuffer(stationALiasStructure, buffer);
        this.stringTable=List.copyOf(stringTable);
    }

    /**
     * returns the alias corresponding to the given StationAliases id
     * @param id station aliases id
     * @return the alias corresponding to the given id
     */
    @Override
    public String alias(int id) {
        return stringTable.get(structuredBuffer.getU16(ALIAS_ID,id));
    }

    /**
     * returns the name corresponding to the given StationAliases id
     * @param id station aliases id
     * @return the name corresponding to the given id
     */
    @Override
    public String stationName(int id) {
        return stringTable.get(structuredBuffer.getU16(STATION_NAME_ID,id));
    }

    /**
     * gives the size of the Buffer used (number of elements)
     * @return the number of stations aliases indexed
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
