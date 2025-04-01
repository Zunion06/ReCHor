package ch.epfl.rechor.timetable.mapped;


import ch.epfl.rechor.timetable.Trips;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U16;

/**
 * used to access a list of trips represented as flattened values
 * @author Matthias Gibon
 */
public final class BufferedTrips implements Trips {
    private final static int ROUTE_ID=0;
    private final static int DESTINATION_ID=1;

    private final StructuredBuffer structuredBuffer;
    private final List<String> stringTable;

    /**
     * create an instance of bufferedTrips to stock information about trips
     * @param stringTable table of string documenting the platforms
     * @param buffer values to be set in the platforms information
     */
    public BufferedTrips(List<String> stringTable, ByteBuffer buffer){
        Structure tripsStructure = new Structure(
                new Structure.Field(ROUTE_ID, U16),
                new Structure.Field(DESTINATION_ID,U16));
        this.structuredBuffer=new StructuredBuffer(tripsStructure,buffer);
        this.stringTable = List.copyOf(stringTable);
    }

    /**
     * gives the id of the route taken by the trip
     * @param id stop id
     * @return the route id for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public int routeId(int id) {
        return structuredBuffer.getU16(ROUTE_ID,id);
    }

    /**
     * gives the destination of the trip
     * @param id route id
     * @return the name of a trip's destination for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public String destination(int id) {
        return stringTable.get(structuredBuffer.getU16(DESTINATION_ID,id));
    }

    /**
     * gives the size of the Buffer used (number of elements)
     * @return the number of trips indexed
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
