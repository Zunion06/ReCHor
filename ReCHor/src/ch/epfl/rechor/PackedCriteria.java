package ch.epfl.rechor;

public class PackedCriteria {
    private static final int NUMBER_OF_BITS_FOR_TIMES = 12;
    private static final int NUMBER_OF_BITS_FOR_CHANGES = 7;
    private static final int NUMBER_OF_BITS_FOR_PAYLOAD = 32;
    private static final int BIT_INDEX_OF_DEPARTURE_TIME = 51;
    private static final int BIT_INDEX_OF_ARRIVING_TIME = 39;
    private static final int BIT_INDEX_OF_CHANGES = 32;
    private static final int BIT_INDEX_OF_PAYLOAD = 0;

    private PackedCriteria() {}

    public static long pack(int arrMins, int changes, int payload) {
        Preconditions.checkBitLength(arrMins,NUMBER_OF_BITS_FOR_TIMES);
        Preconditions.checkBitLength(changes,NUMBER_OF_BITS_FOR_CHANGES);
        // No need to check "payload" as it is an int, therefore it has a maximum of 32 bits
        long arrMinsLong = Integer.toUnsignedLong(arrMins);
        long changesLong = Integer.toUnsignedLong(changes);
        return arrMinsLong << BIT_INDEX_OF_ARRIVING_TIME | changesLong << BIT_INDEX_OF_CHANGES | payload;
    }

    public static boolean hasDepMins(long criteria) {
        long mask = Bits32_24_8.longMask(NUMBER_OF_BITS_FOR_TIMES) << BIT_INDEX_OF_DEPARTURE_TIME;
        return (criteria & mask) != 0;
    }

    public static int depMins(long criteria) {
        Preconditions.checkArgument(hasDepMins(criteria));
        Preconditions.checkArgument(criteria >>> 63 == 0); // test if the last bit is 0 as it should
        int depMinsBeforeMidnight = 4095 - (int) (criteria >>> BIT_INDEX_OF_DEPARTURE_TIME); //taking the complement into account
        return depMinsBeforeMidnight - 240 ;
    }

    public static int arrMins(long criteria) {
        int mask =  Bits32_24_8.mask(NUMBER_OF_BITS_FOR_TIMES);
        int arrMinsBeforeMidnight = (int) (criteria >> BIT_INDEX_OF_DEPARTURE_TIME) & mask;
        return arrMinsBeforeMidnight - 240;
    }

    public static int changes(long criteria) {
        int mask =  Bits32_24_8.mask(NUMBER_OF_BITS_FOR_CHANGES);
        return (int) (criteria >> BIT_INDEX_OF_CHANGES) & mask;
    }

    public static int payload(long criteria) {
        return (int) criteria;
    }

    public static boolean dominatesOrIsEqual(long criteria1, long criteria2) {
        Preconditions.checkArgument((criteria1 | criteria2) >>> 63 == 0); // test if the last bits are 0 as it should
        boolean depMinsPart = criteria1 <= criteria2; //works because the departure hour is on the most significant bits
        boolean arrMinsPart = arrMins(criteria1) <= arrMins(criteria2);
        boolean changesPart = changes(criteria1) <= changes(criteria2);
        return depMinsPart && arrMinsPart && changesPart;
    }

    public static long withoutDepMins(long criteria) {
        return Bits32_24_8.bitsErasure(criteria,BIT_INDEX_OF_DEPARTURE_TIME,NUMBER_OF_BITS_FOR_TIMES);
    }

    // should I check if criteria does not have a depMins or should I just change it ?
    public static long withDepMins(long criteria, int depMins1) {
        Preconditions.checkArgument(!hasDepMins(criteria));
        Preconditions.checkBitLength(depMins1,NUMBER_OF_BITS_FOR_TIMES);
        return Integer.toUnsignedLong(depMins1) << BIT_INDEX_OF_DEPARTURE_TIME | criteria;
    }

    public static long withAdditionalChange(long criteria) {
        int newChanges = changes(criteria) + 1 ;
        Preconditions.checkBitLength(newChanges,NUMBER_OF_BITS_FOR_CHANGES);
        long resetedCriteria = Bits32_24_8.bitsErasure(criteria,BIT_INDEX_OF_CHANGES,NUMBER_OF_BITS_FOR_CHANGES);
        long newChangesLong = Integer.toUnsignedLong(newChanges);
        return newChangesLong << BIT_INDEX_OF_CHANGES | resetedCriteria;
    }

    // should I check if criteria does not have a payload or should I just change it ?
    public static long withPayload(long criteria, int payload1) {
        Preconditions.checkArgument(payload(criteria)==0); // would not really be an IllegalArgumentException tho --> need modifications
        long resetedCriteria = Bits32_24_8.bitsErasure(criteria,BIT_INDEX_OF_PAYLOAD,NUMBER_OF_BITS_FOR_PAYLOAD);
        return resetedCriteria | payload1;
    }
}
