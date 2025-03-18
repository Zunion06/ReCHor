package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.PackedRange;
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
        structuredBuffer=new StructuredBuffer(tripsStructure,buffer);

        int previousArrStation = structuredBuffer.getU16(ARR_STATION_ID,0);
        int transfersIntervalsLength = 1;
        for (int i = 1; i < size(); i++) {
            int currentArrStation = structuredBuffer.getU16(ARR_STATION_ID,i);
            if (currentArrStation != previousArrStation) {
                transfersIntervalsLength++;
                previousArrStation = currentArrStation;
            }
        }

        transfersIntervals = new int[transfersIntervalsLength];
        int previousArrStationID = 0; //ID in transfersIntervals
        int ithIntervalLength = 1;
        for (int i = 1; i < size(); i++) {
            if (structuredBuffer.getU16(ARR_STATION_ID,i) != structuredBuffer.getU16(ARR_STATION_ID,i-1)){
                transfersIntervals[previousArrStationID] = PackedRange.pack(i - ithIntervalLength, i);
                previousArrStationID++;
                ithIntervalLength = 0;
            }
            ithIntervalLength++;
        }
        transfersIntervals[previousArrStationID] = PackedRange.pack(size() - ithIntervalLength, size()); // here previousArrStationID should be equal to transfersIntervalsLength
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
      return transfersIntervals[stationId];
    }

    @Override
    public int minutesBetween(int depStationId, int arrStationId) {
        int searchStartingPoint = PackedRange.startInclusive(transfersIntervals[arrStationId]);
        int searchEndPoint = PackedRange.endExclusive(transfersIntervals[arrStationId]);
        for (int i = searchStartingPoint; i < searchEndPoint; i++) {
            if (structuredBuffer.getU16(DEP_STATION_ID,i) == depStationId)
                return structuredBuffer.getU8(TRANSFER_MINUTES,i);
        }
        throw new NoSuchElementException();
    }

    /**
     * @return the number of route indexed
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
