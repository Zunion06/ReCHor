package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Platforms;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U16;

/**
 * used to access a list of platforms represented as flattened values
 * @author Matthias Gibon
 */
public final class BufferedPlatforms implements Platforms {
    private final static int NAME_ID=0;
    private final static int STATION_ID=1;

    private final StructuredBuffer structuredBuffer;
    private final List<String> stringTable;

    /**
     * create an instance of BufferedPlatforms with the needed information
     * @param stringTable table of string documenting the platforms
     * @param buffer values to be set in the platforms information
     */
    public BufferedPlatforms(List<String> stringTable, ByteBuffer buffer){
        Structure plateformStructure = new Structure(
                new Structure.Field(NAME_ID, U16),
                new Structure.Field(STATION_ID,U16));
        this.structuredBuffer=new StructuredBuffer(plateformStructure,buffer);
        this.stringTable = List.copyOf(stringTable);
    }

    /**
     * gives the name a given platform
     * @param id platform id
     * @return the name of the station for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public String name(int id) {
        return stringTable.get(structuredBuffer.getU16(NAME_ID,id));
    }

    /**
     * gives the stationId of a given platform
     * @param id platform id
     * @return the station id for the given platform id
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public int stationId(int id) {
        return structuredBuffer.getU16(STATION_ID,id);
    }

    /**
     * gives the size of the Buffer used (number of elements)
     * @return the number of platforms indexed
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
