package ch.epfl.rechor.timetable.mapped;



import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.timetable.Connections;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.*;

public final class BufferedConnections implements Connections {
    private final static int DEP_STOP_ID=0;
    private final static int DEP_MINUTES=1;
    private final static int ARR_STOP_ID=2;
    private final static int ARR_MINUTES=3;
    private final static int TRIP_POS_ID=4;


    private final StructuredBuffer structuredBuffer;
    private final IntBuffer nextConnectionIntBuffer;

    /**
     * @param buffer values to be set in the connections information
     * @param succBuffer values to be set as the next connections information
     */
    BufferedConnections(ByteBuffer buffer, ByteBuffer succBuffer){
        Structure connectionsStructure = new Structure(
                new Structure.Field(DEP_STOP_ID, U16),
                new Structure.Field(DEP_MINUTES,U16),
                new Structure.Field(ARR_STOP_ID,U16),
                new Structure.Field(ARR_MINUTES,U16),
                new Structure.Field(TRIP_POS_ID,S32));
        this.structuredBuffer=new StructuredBuffer(connectionsStructure,buffer);

        this.nextConnectionIntBuffer=succBuffer.asIntBuffer();
    }

    @Override
    public int depStopId(int id) {
        return structuredBuffer.getU16(DEP_STOP_ID,id);
    }

    @Override
    public int depMins(int id) {
        return structuredBuffer.getU16(DEP_MINUTES,id);
    }

    @Override
    public int arrStopId(int id) {
        return structuredBuffer.getU16(ARR_STOP_ID,id);
    }

    @Override
    public int arrMins(int id) {
        return structuredBuffer.getU16(ARR_MINUTES,id);
    }

    @Override
    public int tripId(int id) {
        return Bits32_24_8.unpack24(structuredBuffer.getS32(TRIP_POS_ID,id));
    }

    @Override
    public int tripPos(int id) {
        return Bits32_24_8.unpack24(structuredBuffer.getS32(TRIP_POS_ID,id));
    }

    @Override
    public int nextConnectionId(int id) {
        return nextConnectionIntBuffer.get(id);
    }

    /**
     * @return the number of connections indexed
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
