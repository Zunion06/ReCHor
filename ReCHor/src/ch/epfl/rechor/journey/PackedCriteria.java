package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;

/**
 * utilitarian class used to pack and unpack, in longs, tuples containing departure Time,
 * Arriving Time and changes, and also add a payload
 *
 * @author Guillaume Pommier (401065)
 */
public final class PackedCriteria {
    private static final int NUMBER_OF_BITS_FOR_TIMES = 12;
    private static final int NUMBER_OF_BITS_FOR_CHANGES = 7;
    private static final int NUMBER_OF_BITS_FOR_PAYLOAD = 32;
    private static final int BIT_INDEX_OF_DEPARTURE_TIME = 51;
    private static final int BIT_INDEX_OF_ARRIVING_TIME = 39;
    private static final int BIT_INDEX_OF_CHANGES = 32;
    private static final int BIT_INDEX_OF_PAYLOAD = 0;
    private static final int TIME_OFFSET = 240;
    private static final int CONSTANT_FOR_TAKING_DEPARTURE_TIME_COMPLEMENT = 4095;
    private static final int MAXIMUM_CHANGES_POSSIBLE = 0x7F;
    private static final int MAXIMUM_MINUTES_EXCLUDED = 2880;
    private static final long LONG_WITH_ONE_IN_EVERY_BITS = -1L;

    private PackedCriteria() {}

    /**
     * Package the arrival time, the number of change and a "payload" in a long
     * @param arrMins the arrival time (between -240 included and 2880 excluded)
     * @param changes the number of changes (describe over 7 bits)
     * @param payload additional information
     * @return the long-type value resulting from packaging the given arrival time, number of transfers, and "payload" according to the format described
     * @throws IllegalArgumentException if changes has more bits than 7 bits, if arrTime is not between -240 and 2879
     */
    public static long pack(int arrMins, int changes, int payload) {
        Preconditions.checkArgument(-TIME_OFFSET<=arrMins
                && arrMins < MAXIMUM_MINUTES_EXCLUDED);
        Preconditions.checkArgument(changes >> NUMBER_OF_BITS_FOR_CHANGES == 0);
        // No need to check "payload" as it is an int, therefore it has a maximum of 32 bits
        long arrMinsLong = Integer.toUnsignedLong(arrMins + TIME_OFFSET);
        long changesLong = Integer.toUnsignedLong(changes);
        long payloadLong = Integer.toUnsignedLong(payload);
        return arrMinsLong << BIT_INDEX_OF_ARRIVING_TIME | changesLong << BIT_INDEX_OF_CHANGES
                | payloadLong;
    }

    /**
     * Checks if the criteria in argument contains a departure time in it
     * @param criteria a packaged criteria
     * @return a boolean, true if the criteria contains a departure time in it, false otherwise
     */
    public static boolean hasDepMins(long criteria) {
        long mask = mask(NUMBER_OF_BITS_FOR_TIMES) << BIT_INDEX_OF_DEPARTURE_TIME;
        return (criteria & mask) != 0;
    }

    /**
     * Returns the departure time in the criteria in argument
     * @param criteria a packaged criteria
     * @return the departure time in the criteria in argument
     * @throws IllegalArgumentException if the criteria does not have a departure time
     */
    public static int depMins(long criteria) {
        Preconditions.checkArgument(hasDepMins(criteria));
        long mask = mask(NUMBER_OF_BITS_FOR_TIMES) << BIT_INDEX_OF_DEPARTURE_TIME;
        int depMinsBeforeMidnight = CONSTANT_FOR_TAKING_DEPARTURE_TIME_COMPLEMENT
                - (int) ((criteria & mask) >>> BIT_INDEX_OF_DEPARTURE_TIME);
        //taking the complement into account
        return depMinsBeforeMidnight - TIME_OFFSET ;
    }

    /**
     * Returns the arriving time in the criteria in argument
     * @param criteria a packaged criteria
     * @return the arriving time in the criteria in argument
     */
    public static int arrMins(long criteria) {
        int arrMinsBeforeMidnight = (int) ((criteria >>> BIT_INDEX_OF_ARRIVING_TIME)
                & mask(NUMBER_OF_BITS_FOR_TIMES));
        return arrMinsBeforeMidnight - TIME_OFFSET;
    }

    /**
     * Returns the number of changes in the criteria in argument
     * @param criteria a packaged criteria
     * @return the number of changes in the criteria in argument
     */
    public static int changes(long criteria) {
        return (int) ((criteria >>> BIT_INDEX_OF_CHANGES) & mask(NUMBER_OF_BITS_FOR_CHANGES));
    }

    /**
     * Returns the payload in the criteria in argument
     * @param criteria a packaged criteria
     * @return the payload in the criteria in argument
     */
    public static int payload(long criteria) {
        return (int) criteria;
    }

    /**
     * Checks if the first packed criteria dominates or is equal to the second packed criteria
     * @param criteria1 the first packed criteria
     * @param criteria2 the second packed criteria
     * @return true if and only if the first packed criteria dominates or is equal to the second
     * packed criteria, false otherwise
     * @throws IllegalArgumentException if one packed criteria has a departure time and the other
     * does not
     */
    public static boolean dominatesOrIsEqual(long criteria1, long criteria2) {
        Preconditions.checkArgument(hasDepMins(criteria1) == hasDepMins(criteria2));
        return hasDepMins(criteria1) ?
                depMins(criteria1) >= depMins(criteria2)
                        && arrMins(criteria1) <= arrMins(criteria2)
                        && changes(criteria1) <= changes(criteria2) :
                arrMins(criteria1) <= arrMins(criteria2)
                        && changes(criteria1) <= changes(criteria2);
    }

    /**
     * Removes the departure time from a packed criteria
     * @param criteria a packaged criteria to be modified
     * @return the packaged criteria in argument without a departure time
     */
    public static long withoutDepMins(long criteria) {
        return bitsErasure(criteria,BIT_INDEX_OF_DEPARTURE_TIME,NUMBER_OF_BITS_FOR_TIMES);
    }

    /**
     * Add a departure time to a packed criteria
     * @param criteria a packaged criteria to be modified
     * @param depMins1 the departure time to be added
     * @return the packaged criteria in argument with the departure time in argument
     */
    public static long withDepMins(long criteria, int depMins1) {
        long resetedCriteria = bitsErasure(criteria, BIT_INDEX_OF_DEPARTURE_TIME,
                NUMBER_OF_BITS_FOR_TIMES);
        long depMinsComp = CONSTANT_FOR_TAKING_DEPARTURE_TIME_COMPLEMENT - depMins1 - TIME_OFFSET;
        return depMinsComp << BIT_INDEX_OF_DEPARTURE_TIME | resetedCriteria;
    }

    /**
     * Add 1 to the number of changes in the criteria
     * @param criteria a packaged criteria to be modified
     * @return the packaged criteria with the number of changes increased by 1
     * @throws ArithmeticException if increasing the number of changes causes an overflow
     */
    public static long withAdditionalChange(long criteria) {
        if (changes(criteria) == MAXIMUM_CHANGES_POSSIBLE)
            throw new ArithmeticException("overflow in the number of changes");
        return criteria + (1L << BIT_INDEX_OF_CHANGES);
    }

    /**
     * Changes the payload of a given criteria
     * @param criteria a packaged criteria to be modified
     * @param payload1 the payload to be added
     * @return the packaged criteria with the payload included
     */
    public static long withPayload(long criteria, int payload1) {
        long resetedCriteria = bitsErasure(criteria,BIT_INDEX_OF_PAYLOAD,
                NUMBER_OF_BITS_FOR_PAYLOAD);
        return resetedCriteria | Integer.toUnsignedLong(payload1);
    }

    /**
     * Create of mask of a given length
     * @param length the length of the mask
     * @return a long in which the bits of index between 0 and "length" (excluded) are all 1
     * @throws IllegalArgumentException when the length of the mask is not valid for a long (i.e. if not between 0
     * and 64 included)
     */
    private static long mask(int length) {
        Preconditions.checkArgument(0 <= length && length <= Long.SIZE);
        return length == Long.SIZE ? LONG_WITH_ONE_IN_EVERY_BITS : (1L << length) - 1;
    }

    /**
     * Turns into 0 all bits at a certain position
     * @param number the number to be modified
     * @param startIndex the index of where the transformation into 0 should begin to be applied
     * @param length the number of bits that should be transformed after the startIndex
     * @return the number but with all bits of index [startIndex, starIndex + length] turned into 0
     * @throws IllegalArgumentException if the startIndex is not valid (i.e. if not between 0 and 64
     * included)
     */
    private static long bitsErasure(long number, int startIndex, int length) {
        Preconditions.checkArgument(0 <= startIndex && startIndex <= Long.SIZE);
        long mask = mask(length) << startIndex;
        return number & ~mask;
    }

}
