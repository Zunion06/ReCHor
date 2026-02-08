package epfl.rechor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class MyPackedRangeTest {
    private final int startInclusive = 1234;
    private final int endExclusive = 1278;
    private final int pack = PackedRange.pack(startInclusive,endExclusive);

    @Test
    void PackedRangePackWorksOnNonTrivialCase() {
        assertEquals(0b00000000000001001101001000101100, pack);
    }

    // Should it really works ?
    @Test
    void PackedRangePackWorksWhenStartEqualsEnd() {
        int number = 1;
        assertEquals(0b00000000000000000000000100000000, PackedRange.pack(number,number));
    }

    @Test
    void PackedRangePackThrowsWhenStartInclusiveIsTooBig() {
        int wrongStartInclusive = 1 << 24;
        assertThrows(IllegalArgumentException.class, () -> {
            PackedRange.pack(wrongStartInclusive,wrongStartInclusive+1);
        });
    }

    @Test
    void PackedRangePackThrowsWhenLengthIsTooBig() {
        int wrongEndExclusive = startInclusive + 256;
        assertThrows(IllegalArgumentException.class, () -> {
            PackedRange.pack(startInclusive,wrongEndExclusive);
        });
    }

    @Test
    void PackedRangePackThrowsWhenStartIsAfterEnd() {
        assertThrows(IllegalArgumentException.class, () -> {
            PackedRange.pack(endExclusive,startInclusive);
        });
    }

    @Test
    void PackedRangeLengthWorks() {
        assertEquals(44, PackedRange.length(pack));
    }

    @Test
    void PackedRangeStartInclusiveWorks() {
        assertEquals(1234, PackedRange.startInclusive(pack));
    }

    @Test
    void PackedRangeEndExclusiveWorks() {
        assertEquals(1278, PackedRange.endExclusive(pack));
    }

}
