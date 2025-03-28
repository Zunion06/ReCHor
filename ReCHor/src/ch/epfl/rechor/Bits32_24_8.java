package ch.epfl.rechor;

public final class Bits32_24_8 {
    private Bits32_24_8() {}

    /**
     * Package the integer of 3 bytes and the integer of 1 byte together in a format int
     * @param bits24 an integer of 3 bytes
     * @param bits8 an integer of 1 byte
     * @return a packaged an int with the integer of 3 bytes and the integer of 1 byte
     * @throws IllegalArgumentException if bits24 has more than 24 bits of information, if bits8 has more than 8 bits of information
     */
    public static int pack(int bits24, int bits8){
        Preconditions.checkArgument(bits24 >> 24 == 0);
        Preconditions.checkArgument(bits8 >> 8 == 0);
        return bits24 << 8 | bits8;
    }

    public static int unpack24(int bits32){
        return bits32 >>> 8;
    }

    public static int unpack8(int bits32){
        int mask = 0xFF;
        return bits32 & mask;
    }
}
