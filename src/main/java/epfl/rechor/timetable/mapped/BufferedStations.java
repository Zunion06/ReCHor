package epfl.rechor.timetable.mapped;

import java.nio.ByteBuffer;
import java.util.List;

import epfl.rechor.timetable.Stations;
import static epfl.rechor.timetable.mapped.Structure.FieldType.S32;
import static epfl.rechor.timetable.mapped.Structure.FieldType.U16;

/**
 * used to access a list of stations represented as flattened values
 * @author Matthias Gibon (397827)
 */
public final class BufferedStations implements Stations {
    private final StructuredBuffer structuredBuffer;
    private final List<String> stringTable;

    private final static int NAME_ID_INDEX =0;
    private final static int LONGITUDE_ID_INDEX=1;
    private final static int LATITUDE_ID_INDEX=2;
    private final static int SCALE_FACTOR = -32;
    private final static double TO_DEGREE = 360.0;
    private final static double ANGLE_TO_COORDINATES = StrictMath.scalb(TO_DEGREE,SCALE_FACTOR);

    /**
     * create an instance of BufferedStations to stock information
     * @param stringTable table of string documenting the stations
     * @param buffer values to be set in the stations information
     */
    public BufferedStations(List<String> stringTable, ByteBuffer buffer){
        Structure stationStructure = new Structure(
                new Structure.Field(NAME_ID_INDEX, U16),
                new Structure.Field(LONGITUDE_ID_INDEX,S32),
                new Structure.Field(LATITUDE_ID_INDEX,S32));
        this.structuredBuffer=new StructuredBuffer(stationStructure,buffer);
        this.stringTable=List.copyOf(stringTable);
    }

    /**
     * gives the name
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
        return (structuredBuffer.getS32(LONGITUDE_ID_INDEX,id)) * ANGLE_TO_COORDINATES;
    }

    /**
     * @param id station id
     * @return the latitude (in degree) of the station for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public double latitude(int id) {
        return (structuredBuffer.getS32(LATITUDE_ID_INDEX,id)) * ANGLE_TO_COORDINATES;
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
