package ch.epfl.rechor;

public final class Bits32_24_8 {
    private Bits32_24_8() {}

    public static int pack(int bits24, int bits8){
        Preconditions.checkBitLength(bits24,24);
        Preconditions.checkBitLength(bits8,8);
        return bits24<<8 | bits8;
    }

    public static int unpack24(int bits32){
        return bits32 >>> 8;
    }

    public static int unpack8(int bits32){
        int mask = mask(8);
        return bits32 & mask;
    }

    //###################### ADDED METHODS ######################

    public static int mask(int nbOfOne) {
        Preconditions.checkArgument(isLegalForInt(nbOfOne));
        return nbOfOne == Integer.SIZE ? -1 : (1 << nbOfOne) - 1; // -1 means 32 bits of 1
        // I did not use "return (int) longMask(nbOfOne)" as there would be an unnecessary check with isLegalForLong(nbOfOne);
    }

    public static long longMask(int nbOfOne) {
        Preconditions.checkArgument(isLegalForLong(nbOfOne));
        return nbOfOne == Long.SIZE ? -1L : (1L << nbOfOne) - 1; // -1L means 64 bits of 1
    }

    public static long bitsErasure(long number, int startIndex, int length) {
        Preconditions.checkArgument(isLegalForLong(startIndex));
        Preconditions.checkArgument(isLegalForLong(startIndex+length));
//        Preconditions.checkArgument(0 < length);
        long mask = longMask(length) << startIndex;
        return number & ~mask;
    }

    // is that needed ? --> Nope (just keeping it as comment, 'cause it does not hurt)
//ui    public static int bitsErasure(int number, int startIndex, int length) {
//        Preconditions.checkArgument(isLegalForInt(startIndex));
//        Preconditions.checkArgument(isLegalForInt(startIndex+length));
//        Preconditions.checkArgument(0 < length);
//        int mask = mask(length - 1) << startIndex;
//        return number & ~mask;
//    }

    private static boolean isLegalForLong(int index) {
        return 0 <= index && index <= Long.SIZE;
    }

    private static boolean isLegalForInt(int index) {
        return 0 <= index && index <= Integer.SIZE;
    }

}
