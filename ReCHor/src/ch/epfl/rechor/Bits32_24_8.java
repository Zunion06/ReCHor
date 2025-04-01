package ch.epfl.rechor;

/**
 * utilitarian class used to pack and unpack information into an integer of 32 bits
 * @author Guillaume Pommier
 */
public final class Bits32_24_8 {
    private Bits32_24_8() {}

    /**
     * Package the integer of 24 bits and the integer of 8 bits together in a format int
     * @param bits24 an integer written on 24 bits
     * @param bits8 an integer written on 8 bits
     * @return a packaged an int with the integer of 24 bits and the integer of 8 bits
     * @throws IllegalArgumentException if bits24 has more than 24 bits of information, if bits8 has more than 8 bits of information
     */
    public static int pack(int bits24, int bits8){
        Preconditions.checkArgument(bits24 >> 24 == 0);
        Preconditions.checkArgument(bits8 >> 8 == 0);
        return bits24 << 8 | bits8;
    }

    /**
     * Gives the last 24 bits an int
     * @param bits32 a packaged integer in which the last 24 bits and the first 8 has meaning
     * @return the last 24 bits the int given as parameter
     */
    public static int unpack24(int bits32){
        return bits32 >>> 8;
    }

    /**
     * Gives the last 8 bits an int
     * @param bits32 a packaged integer in which the last 24 bits and the first 8 has meaning
     * @return the last 8 bites the int given as parameter
     */
    public static int unpack8(int bits32){
        return Byte.toUnsignedInt((byte) bits32);
    }
}
