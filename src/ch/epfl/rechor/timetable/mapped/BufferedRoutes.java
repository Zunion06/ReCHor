package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.journey.Vehicle;
import ch.epfl.rechor.timetable.Routes;

import java.nio.ByteBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U16;
import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U8;

/**
 * used to access a list of routes represented as flattened values
 * @author Matthias Gibon (397827)
 */
public final class BufferedRoutes implements Routes {

    private final static int NAME_ID=0;
    private final static int KIND=1;

    private final StructuredBuffer structuredBuffer;
    private final List<String> stringTable;

    /**
     * create an instance of BufferedRoutes with the needed information
     * @param stringTable table of string documenting the platforms
     * @param buffer values to be set in the platforms information
     */
    public BufferedRoutes(List<String> stringTable, ByteBuffer buffer){
        Structure routesStructure = new Structure(
                new Structure.Field(NAME_ID, U16),
                new Structure.Field(KIND,U8));
        this.structuredBuffer=new StructuredBuffer(routesStructure,buffer);
        this.stringTable = List.copyOf(stringTable);
    }

    /**
     * gives the name of a given route
     * @param id route id
     * @return the name of the route for a given index
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public String name(int id) {
        return stringTable.get(structuredBuffer.getU16(NAME_ID,id));
    }

    /**
     * gives the kind of vehicle used on a given route
     * @param id route id
     * @return the type of vehicle used on a given route
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public Vehicle vehicle(int id) {
        return Vehicle.ALL.get(structuredBuffer.getU8(KIND,id));
    }

    /**
     * gives the size of the Buffer used (number of elements)
     * @return the number of routes indexed
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
