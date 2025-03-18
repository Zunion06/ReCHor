package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.Transfers;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.NoSuchElementException;

import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U16;
import static ch.epfl.rechor.timetable.mapped.Structure.FieldType.U8;

public final class BufferedTransfers implements Transfers {
    private final static int DEP_STATION_ID=0;
    private final static int ARR_STATION_ID=1;
    private final static int TRANSFER_MINUTES=2;

    private final StructuredBuffer structuredBuffer;
    private final int[] transfersIntervals;

    /**
     * @param buffer values to be set in the platforms information
     */
    BufferedTransfers(ByteBuffer buffer){
        Structure tripsStructure = new Structure(
                new Structure.Field(DEP_STATION_ID, U16),
                new Structure.Field(ARR_STATION_ID,U16),
                new Structure.Field(TRANSFER_MINUTES,U8));
        this.structuredBuffer=new StructuredBuffer(tripsStructure,buffer);
        this.transfersIntervals=new int[structuredBuffer.size()];
        this.transfersIntervals=
    }


    @Override
    public int depStationId(int id) {
        return structuredBuffer.getU16(DEP_STATION_ID,id);
    }

    @Override
    public int minutes(int id) {
        return structuredBuffer.getU8(TRANSFER_MINUTES,id);
    }

    @Override
    public int arrivingAt(int stationId) {
        //return structuredBuffer.getU16(ARR_STATION_ID,stationId);
    }

    @Override
    public int minutesBetween(int depStationId, int arrStationId) {
        if(false){throw new NoSuchElementException();}
        return 0;
    }

    /**
     * @return the number of route indexed
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
