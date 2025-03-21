package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.PackedRange;
import ch.epfl.rechor.timetable.Transfers;

import java.nio.ByteBuffer;
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
     * create an instance of BuffedTransfers to stock information for transfer
     * @param buffer values to be set in the platforms information
     */
    public BufferedTransfers(ByteBuffer buffer){
        Structure tripsStructure = new Structure(
                new Structure.Field(DEP_STATION_ID, U16),
                new Structure.Field(ARR_STATION_ID,U16),
                new Structure.Field(TRANSFER_MINUTES,U8));
        structuredBuffer=new StructuredBuffer(tripsStructure,buffer);

        int maxArrStationIndex = 0;
        for (int i = 1; i < size(); i++) {
            int currentArrStationIndex = structuredBuffer.getU16(ARR_STATION_ID,i);
            if (currentArrStationIndex > maxArrStationIndex)
                maxArrStationIndex = currentArrStationIndex;
        }

        transfersIntervals = new int[maxArrStationIndex+1];
        int previousArrStationID = structuredBuffer.getU16(ARR_STATION_ID,0); //ID in transfersIntervals
        int ithIntervalLength = 1;
        for (int i = 1; i < size(); i++) {
            if (structuredBuffer.getU16(ARR_STATION_ID,i) != structuredBuffer.getU16(ARR_STATION_ID,i-1)){
                transfersIntervals[previousArrStationID] = PackedRange.pack(i - ithIntervalLength, i);
                previousArrStationID = structuredBuffer.getU16(ARR_STATION_ID,i);
                ithIntervalLength = 0;
            }
            ithIntervalLength++;
        }
        transfersIntervals[previousArrStationID] = PackedRange.pack(size() - ithIntervalLength, size()); // here previousArrStationID should be equal to transfersIntervalsLength
    }


    /**
     * gives the id of the departing station in a given transfer
     * @param id transfer id
     * @return the id of the departing station of the transfer
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public int depStationId(int id) {
        return structuredBuffer.getU16(DEP_STATION_ID,id);
    }

    /**
     * gives the length of the transfer (in minutes)
     * @param id transfer id
     * @return the length of the transfer (in minutes)
     * @throws IndexOutOfBoundsException for invalid index
     */
    @Override
    public int minutes(int id) {
        return structuredBuffer.getU8(TRANSFER_MINUTES,id);
    }

    /**
     * gives the interval for all transfers arriving to this station
     * @param stationId id of the arrival station
     * @return packed interval of all transfer ids arriving at this station
     * @throws IndexOutOfBoundsException if stationId is invalid
     */
    @Override
    public int arrivingAt(int stationId) {
      return transfersIntervals[stationId];
    }

    /**
     * return the time taken (in minutes) for the transfer between the two stations
     * @param depStationId departing station id
     * @param arrStationId arriving station id
     * @return transfer length in minutes
     * @throws NoSuchElementException if there is no such transfers between both stations
     */
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
     * gives the size of the Buffer used (number of elements)
     * @return the number of Transfer indexed
     */
    @Override
    public int size() {
        return structuredBuffer.size();
    }
}
