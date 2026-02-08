package epfl.rechor;

/**
 * used to throw given exceptions in some cases
 * @author Matthias Gibon (397827)
 */
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
}