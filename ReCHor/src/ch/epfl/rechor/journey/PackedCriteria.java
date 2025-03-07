package ch.epfl.rechor.journey;

import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.Preconditions;

// should it be final ?
public final class PackedCriteria {
    private static final int NUMBER_OF_BITS_FOR_TIMES = 12;
    private static final int NUMBER_OF_BITS_FOR_CHANGES = 7;
    private static final int NUMBER_OF_BITS_FOR_PAYLOAD = 32;
    private static final int BIT_INDEX_OF_DEPARTURE_TIME = 51;
    private static final int BIT_INDEX_OF_ARRIVING_TIME = 39;
    private static final int BIT_INDEX_OF_CHANGES = 32;
    private static final int BIT_INDEX_OF_PAYLOAD = 0;

    private PackedCriteria() {}

    public static long pack(int arrMins, int changes, int payload) {
        Preconditions.checkArgument(-240<=arrMins && arrMins < 2880);
        Preconditions.checkBitLength(changes,NUMBER_OF_BITS_FOR_CHANGES);
        // No need to check "payload" as it is an int, therefore it has a maximum of 32 bits
        long arrMinsLong = Integer.toUnsignedLong(arrMins+240);
        long changesLong = Integer.toUnsignedLong(changes);
        long payloadLong = Integer.toUnsignedLong(payload);
        return arrMinsLong << BIT_INDEX_OF_ARRIVING_TIME | changesLong << BIT_INDEX_OF_CHANGES | payloadLong;
    }

    public static boolean hasDepMins(long criteria) {
        long mask = Bits32_24_8.longMask(NUMBER_OF_BITS_FOR_TIMES) << BIT_INDEX_OF_DEPARTURE_TIME;
        return (criteria & mask) != 0;
    }

    public static int depMins(long criteria) {
        Preconditions.checkArgument(hasDepMins(criteria));
        long mask = Bits32_24_8.longMask(NUMBER_OF_BITS_FOR_TIMES) << BIT_INDEX_OF_DEPARTURE_TIME;
        int depMinsBeforeMidnight = 4095 - (int) ((criteria & mask) >>> BIT_INDEX_OF_DEPARTURE_TIME); //taking the complement into account
        return depMinsBeforeMidnight - 240 ;
    }

    public static int arrMins(long criteria) {
        int mask =  Bits32_24_8.mask(NUMBER_OF_BITS_FOR_TIMES);
        int arrMinsBeforeMidnight = (int) (criteria >>> BIT_INDEX_OF_ARRIVING_TIME) & mask;
        return arrMinsBeforeMidnight - 240;
    }

    public static int changes(long criteria) {
        int mask =  Bits32_24_8.mask(NUMBER_OF_BITS_FOR_CHANGES);
        return (int) (criteria >>> BIT_INDEX_OF_CHANGES) & mask;
    }

    public static int payload(long criteria) {
        return (int) criteria;
    }

    public static boolean dominatesOrIsEqual(long criteria1, long criteria2) {
//        Preconditions.checkArgument((criteria1 | criteria2) >>> 63 == 0); // test if the last bits are 0 as it should
        Preconditions.checkArgument(hasDepMins(criteria1) == hasDepMins(criteria2));

        System.out.println(arrMins(criteria1) <= arrMins(criteria2)
                && changes(criteria1) <= changes(criteria2));
        return hasDepMins(criteria1) ?
                depMins(criteria1) >= depMins(criteria2)
                        && arrMins(criteria1) <= arrMins(criteria2)
                        && changes(criteria1) <= changes(criteria2) :
                arrMins(criteria1) <= arrMins(criteria2)
                        && changes(criteria1) <= changes(criteria2);
    }

    public static long withoutDepMins(long criteria) {
        return Bits32_24_8.bitsErasure(criteria,BIT_INDEX_OF_DEPARTURE_TIME,NUMBER_OF_BITS_FOR_TIMES);
    }

    // should I check if criteria does not have a depMins or should I just change it ?
    public static long withDepMins(long criteria, int depMins1) {
//        Preconditions.checkArgument(!hasDepMins(criteria));
//        Preconditions.checkArgument(-240<=depMins1 && depMins1 < 2880);
//        Preconditions.checkArgument(depMins1 <= arrMins(criteria)); // should it be done ? it was not specified
                // and should it be " <= " or just " < " ?
        long resetedCriteria = Bits32_24_8.bitsErasure(criteria, BIT_INDEX_OF_DEPARTURE_TIME,NUMBER_OF_BITS_FOR_TIMES);
        long depMinsComp = 4095 - depMins1 -240;
        return depMinsComp << BIT_INDEX_OF_DEPARTURE_TIME | resetedCriteria;
    }

    public static long withAdditionalChange(long criteria) {
        long newChanges = (changes(criteria) + 1);
//        Preconditions.checkBitLength(newChanges,NUMBER_OF_BITS_FOR_CHANGES); // would not really be an IllegalArgumentException tho --> need modifications (?)
        long resetedCriteria = Bits32_24_8.bitsErasure(criteria,BIT_INDEX_OF_CHANGES,NUMBER_OF_BITS_FOR_CHANGES);
        int mask = Bits32_24_8.mask(NUMBER_OF_BITS_FOR_CHANGES);
        return (newChanges & mask) << BIT_INDEX_OF_CHANGES | resetedCriteria;
    }

    // should I check if criteria does not have a payload or should I just change it ?
    public static long withPayload(long criteria, int payload1) {
//        Preconditions.checkArgument(payload(criteria)==0);
        long resetedCriteria = Bits32_24_8.bitsErasure(criteria,BIT_INDEX_OF_PAYLOAD,NUMBER_OF_BITS_FOR_PAYLOAD);
        return resetedCriteria | Integer.toUnsignedLong(payload1);
    }

    public String packToString(long criteria) {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        if (hasDepMins(criteria)) {
            int depMins = depMins(criteria);
            builder.append(depMins/60)
                    .append('h')
                    .append(depMins%60)
                    .append(',');
        }
        int arrMins = arrMins(criteria);
        builder.append(arrMins/60)
                .append('h')
                .append(arrMins%60)
                .append(',')
                .append(changes(criteria))
                .append(')')
                .append(' ')
                .append(payload(criteria))
                .append('\n');
        return builder.toString();
    }
}
