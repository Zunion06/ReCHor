package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Stations;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.S32;
import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U16;

public final class BufferedStations implements Stations {
    private final StructuredBuffer structuredBuffer;
    private final List<String> stringTable;

    private final static int NAME_ID_INDEX =0;
    private final static int LONGITUDE_ID_INDEX=1;
    private final static int LATITUDE_ID_INDEX=2;

    /**
     * create an instance of BufferedStations to stock informations
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
    }

    /**
     * @param id station id
     * @return the name of the station for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public String name(int id) {
        return stringTable.get(structuredBuffer.getU16(NAME_ID_INDEX,id));
    }

    /**
     * @param id station id
     * @return the longitude (in degree) of the station for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public double longitude(int id) {
        return StrictMath.scalb(360.0*(structuredBuffer.getS32(LONGITUDE_ID_INDEX,id)),-32);
    }

    /**
     * @param id station id
     * @return the latitude (in degree) of the station for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public double latitude(int id) {
        return StrictMath.scalb(360.0*(structuredBuffer.getS32(LATITUDE_ID_INDEX,id)),-32);
    }

    /**
     * gives the size of the Buffer used (number of elements)
     * @return the number of stations indexed
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
