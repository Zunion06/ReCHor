package epfl.rechor;

/**
 * utilitarian class used to pack an interval into an integer of 32 bits, and unpack the relevant information
 * @author Guillaume Pommier (401065)
 */
public final class PackedRange {
    private PackedRange() {}

    /**
     * Package an interval in a format int
     * @param startInclusive (an integer written on 3 bytes) the start of the interval
     * @param endExclusive (an integer written on 1 byte) the end of the interval (this number is excluded of the interval)
     * @return a packaged an int that symbolized this interval : [startInclusive;endExclusive)
     * @throws IllegalArgumentException if startInclusive has more than 24 bits of information, if endExclusive has more than 8 bits of information
     */
    public static int pack(int startInclusive, int endExclusive) {
        Preconditions.checkArgument(startInclusive<=endExclusive);
        int length = endExclusive - startInclusive;
        return Bits32_24_8.pack(startInclusive,length);
    }

    /**
     * Gives the length of a packed interval
     * @param interval a packed interval
     * @return the length of the interval given as parameter
     */
    public static int length(int interval){
        return Bits32_24_8.unpack8(interval);
    }

    /**
     * Gives the number starting a packed interval
     * @param interval a packed interval
     * @return the number starting the interval given as parameter
     */
    public static int startInclusive(int interval){
        return Bits32_24_8.unpack24(interval);
    }

    /**
     * Gives the number ending a packed interval
     * @param interval a packed interval
     * @return the number ending the interval given as parameter
     */
    public static int endExclusive(int interval){
        return startInclusive(interval) + length(interval);
    }
}
