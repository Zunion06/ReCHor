package ch.epfl.rechor;

public final class Preconditions {
    private Preconditions() {}

    /**
     * Checks the argument and throws IllegalArgumentException if false
     * @param shouldBeTrue assertion that should be true if the program works correctly
     * @throws IllegalArgumentException if shouldBeTrue is false
     */
    public static void checkArgument(boolean shouldBeTrue){
        if(!shouldBeTrue) throw new IllegalArgumentException();
    }

    /**
     * Checks if a long has the right amount of bit
     * @param number the number (a long) that will be checked
     * @param nbOfBitsItShouldHave the number of bits the number should have
     * @throws IllegalArgumentException if the number has more bits than what it should have
     */
    public static void checkBitLength(long number, int nbOfBitsItShouldHave) {
        checkArgument(number >> nbOfBitsItShouldHave == 0);
    }

    /**
     * Checks if an int has the right amount of bit
     * @param number the number (an int) that will be checked
     * @param nbOfBitsItShouldHave the number of bits the number should have
     * @throws IllegalArgumentException if the number has more bits than what it should be
     */
    public static void checkBitLength(int number, int nbOfBitsItShouldHave) {
        checkArgument(number >> nbOfBitsItShouldHave == 0);
    }
}