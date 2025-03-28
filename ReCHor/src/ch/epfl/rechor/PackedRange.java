package ch.epfl.rechor;

public final class PackedRange {
    private PackedRange() {}

    public static int pack(int startInclusive, int endExclusive) {
        Preconditions.checkArgument(startInclusive<=endExclusive);
        int length = endExclusive - startInclusive;
        return Bits32_24_8.pack(startInclusive,length);
    }

    public static int length(int interval){
        return Bits32_24_8.unpack8(interval);
    }

    public static int startInclusive(int interval){
        return Bits32_24_8.unpack24(interval);
    }

    public static int endExclusive(int interval){
        return startInclusive(interval) + length(interval);
    }
}
