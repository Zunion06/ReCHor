package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Stations;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.S32;
import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U16;

public final class BufferedStations implements Stations {
    public final StructuredBuffer structuredBuffer;
    private final List<String> stringTable;

    public final static int NAME_ID_INDEX =0;
    public final static int LONGITUDE_ID_INDEX=1;
    public final static int LATITUDE_ID_INDEX=2;

    private final int size;

    /**
     * @param stringTable table of string documenting the stations
     * @param buffer values to be set in the stations information
     */
    public BufferedStations(List<String> stringTable, ByteBuffer buffer){
        Structure stationStructure = new Structure(
                new Structure.Field(NAME_ID_INDEX, U16),
                new Structure.Field(LONGITUDE_ID_INDEX,S32),
                new Structure.Field(LATITUDE_ID_INDEX,S32));
        this.structuredBuffer=new StructuredBuffer(stationStructure,buffer);
        this.stringTable=stringTable;
        size = structuredBuffer.size() / stationStructure.totalSize();
    }

    /**
     * @param id station id
     * @return the name of the station for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public String name(int id) {
        return stringTable.get(structuredBuffer.getU8(NAME_ID_INDEX,id));
    }

    /**
     * @param id station id
     * @return the longitude (in degree) of the station for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public double longitude(int id) {
        return StrictMath.scalb(360*structuredBuffer.getS32(LONGITUDE_ID_INDEX,id),-32);
    }

    /**
     * @param id station id
     * @return the latitude (in degree) of the station for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public double latitude(int id) {
        return StrictMath.scalb(360*structuredBuffer.getS32(LATITUDE_ID_INDEX,id),-32);
    }

    /**
     * @return the number of stations indexed
     */
    @Override
    public int size() {
        return size;
    }
}
