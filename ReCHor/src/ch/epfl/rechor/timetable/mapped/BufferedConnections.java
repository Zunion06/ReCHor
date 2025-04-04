package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.timetable.Connections;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.*;

/**
 * used to access a list of connections represented as flattened values
 * @author Matthias Gibon (397827)
 */
public final class BufferedConnections implements Connections {
    private final static int DEP_STOP_ID=0;
    private final static int DEP_MINUTES=1;
    private final static int ARR_STOP_ID=2;
    private final static int ARR_MINUTES=3;
    private final static int TRIP_POS_ID=4;


    private final StructuredBuffer structuredBuffer;
    private final IntBuffer nextConnectionIntBuffer;

    /**
     * create an instance of BufferedConnections stocking the corresponding information
     * @param buffer values to be set in the connections information
     * @param succBuffer values to be set as the next connections information
     */
    public BufferedConnections(ByteBuffer buffer, ByteBuffer succBuffer){
        Structure connectionsStructure = new Structure(
                new Structure.Field(DEP_STOP_ID, U16),
                new Structure.Field(DEP_MINUTES,U16),
                new Structure.Field(ARR_STOP_ID,U16),
                new Structure.Field(ARR_MINUTES,U16),
                new Structure.Field(TRIP_POS_ID,S32));
        this.structuredBuffer=new StructuredBuffer(connectionsStructure,buffer);

        this.nextConnectionIntBuffer=succBuffer.asIntBuffer();
    }

    /**
     * gives the stop id of the first stop
     * @param id connection id
     * @return id of the first stop of the connection
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public int depStopId(int id) {
        return structuredBuffer.getU16(DEP_STOP_ID,id);
    }

    /**
     * gives the length in minutes of the connection
     * @param id connection id
     * @return the length of the connection (in minutes)
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public int depMins(int id) {
        return structuredBuffer.getU16(DEP_MINUTES,id);
    }

    /**
     * gives the arrival stop id of the connection
     * @param id connection id
     * @return the id of the last stop of the connection
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public int arrStopId(int id) {
        return structuredBuffer.getU16(ARR_STOP_ID,id);
    }

    /**
     * gives the arrival time of the connection
     * @param id connection id
     * @return arrival time of the connection (in minutes)
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public int arrMins(int id) {
        return structuredBuffer.getU16(ARR_MINUTES,id);
    }

    /**
     * gives the id of the trip containing the connection
     * @param id connection id
     * @return the id of the trip containing the connection
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public int tripId(int id) {
        return Bits32_24_8.unpack24(structuredBuffer.getS32(TRIP_POS_ID,id));
    }

    /**
     * gives the position of the connection in its trip it belongs to
     * @param id connection id
     * @return the position of the connection in its trip
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public int tripPos(int id) {
        return Bits32_24_8.unpack8(structuredBuffer.getS32(TRIP_POS_ID,id));
    }

    /**
     * gives the id of the following connection in a trip
     * @param id current connection id
     * @return the id of the next connection
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public int nextConnectionId(int id) {
        return nextConnectionIntBuffer.get(id);
    }

    /**
     * gives the size of the Buffer used (number of elements)
     * @return the number of connections indexed
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
