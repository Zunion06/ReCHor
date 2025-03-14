package ch.epfl.rechor;

public final class Preconditions {
    private Preconditions() {}

    /**
     * @param shouldBeTrue assertion that should be true if the program works correctly
     * @throws IllegalArgumentException if shouldBeTrue is false
     */
    public static void checkArgument(boolean shouldBeTrue){
        if(!shouldBeTrue) throw new IllegalArgumentException();
    }

    public static void checkBitLength(long number, int nbOfBitsItShouldHave) {
        checkArgument(number >> nbOfBitsItShouldHave == 0);
    }

    public static void checkBitLength(int number, int nbOfBitsItShouldHave) {
        //bitLengthCheck(Integer.toUnsignedLong(number), nbOfBitsItShouldHave); could be possible
        checkArgument(number >> nbOfBitsItShouldHave == 0); // is better in my opinion
    }
}