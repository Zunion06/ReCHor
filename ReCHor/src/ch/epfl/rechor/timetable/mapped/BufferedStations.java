package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Stations;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.S32;
import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U16;

public final class BufferedStations implements Stations {
    StructuredBuffer structuredBuffer;
    String[] stringTable;

    public final static int NAME_ID=0;
    public final static int LONGITUDE_ID=1;
    public final static int LATITUDE_ID=2;

    /**
     * @param stringTable table of string documenting the stations
     * @param buffer values to be set in the stations information
     */
    public BufferedStations(List<String> stringTable, ByteBuffer buffer){
        this.structuredBuffer=new StructuredBuffer(new Structure(new Structure.Field(NAME_ID, U16),
                new Structure.Field(LONGITUDE_ID,S32),new Structure.Field(LATITUDE_ID,S32)),buffer);
        this.stringTable=new String[stringTable.size()];
        stringTable.toArray(this.stringTable);
    }

    /**
     * @param id station id
     * @return the name of the station for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public String name(int id) {
        return stringTable[structuredBuffer.getU8(NAME_ID,id)];
    }

    /**
     * @param id station id
     * @return the longitude (in degree) of the station for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public double longitude(int id) {
        return StrictMath.scalb(360*structuredBuffer.getS32(LONGITUDE_ID,id),-32);
        //return StrictMath.scalb(structuredBuffer.getS32(LONGITUDE_ID, id), -30);
    }

    /**
     * @param id station id
     * @return the latitude (in degree) of the station for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public double latitude(int id) {
        return StrictMath.scalb(360*structuredBuffer.getS32(LATITUDE_ID,id),-32);
    }

    /**
     * @return the number of stations indexed
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
