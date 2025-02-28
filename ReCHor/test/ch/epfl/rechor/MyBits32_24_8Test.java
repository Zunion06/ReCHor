package ch.epfl.rechor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyBits32_24_8Test {
    @Test
    void Bits32_24_8PackWorksWithBitsComposedOfOnes() {
        int bits24 = 0b111111111111111111111111; // 24 chars
        int bits8 = 0b11111111; // 8 chars
        int expectedBits = 0b11111111111111111111111111111111;
        assertEquals(expectedBits, Bits32_24_8.pack(bits24,bits8));
    }

    @Test
    void Bits32_24_8PackWorksWithNonTrivialAttributes() {
        int bits24 = 0b0101010010101110011101; // 22 chars
        int bits8 = 0b111110; // 6 chars
        int expectedBits = 0b00010101001010111001110100111110;
        assertEquals(expectedBits, Bits32_24_8.pack(bits24,bits8));
    }

    @Test
    void Bits32_24_8PackThrowsWhenBits8HasTheWrongSize() {
        int bits24 = 0b110101010010101110011101; // 24 chars
        int bits8 = 0b111110101; // 9 chars
        assertThrows(IllegalArgumentException.class, () -> {
            Bits32_24_8.pack(bits24,bits8);
        });
    }

    @Test
    void Bits32_24_8PackThrowsWhenBits24HasTheWrongSize() {
        int bits24 = 0b1101010100101011100111010; // 25 chars
        int bits8 = 0b11111010; // 8 chars
        assertThrows(IllegalArgumentException.class, () -> {
            Bits32_24_8.pack(bits24,bits8);
        });
    }

    @Test
    void Bits32_24_8Unpack24WorksWithZeroAtTheEnd() {
        int bits32 = 0b00010101001010111001110100111110;
        int expectedReturn = 0b000101010010101110011101;
        assertEquals(expectedReturn, Bits32_24_8.unpack24(bits32));
    }

    @Test
    void Bits32_24_8Unpack24WorksWithOneAtTheEnd() {
        int bits32 = 0b10010101001010111001110100111110;
        int expectedReturn = 0b100101010010101110011101;
        assertEquals(expectedReturn, Bits32_24_8.unpack24(bits32));
    }

    @Test
    void Bits32_24_8Unpack8Works() {
        int bits32 = 0b00010101001010111001110100111110;
        int expectedReturn = 0b00111110;
        assertEquals(expectedReturn, Bits32_24_8.unpack8(bits32));
    }

    @Test
    void DoesErasureBitsWorks() {
        long allOne = -1L;
        long actualReturn = Bits32_24_8.bitsErasure(allOne, 32, 1);
        long expectedReturn = -1L << 6 | 0b000001;
        System.out.println(Long.toBinaryString(actualReturn));
//        assertEquals(expectedReturn,actualReturn);
    }

    //####################### Test d'aliénor ##################################

    @Test
    void testPackValidCases() {
        assertEquals(0x000000FF, Bits32_24_8.pack(0x000000, 0xFF));
        assertEquals(0x00FFFFFF, Bits32_24_8.pack(0x00FFFF, 0xFF));
        assertEquals(0x12345678, Bits32_24_8.pack(0x123456, 0x78));
        assertEquals(0xFFFFFF00, Bits32_24_8.pack(0xFFFFFF, 0x00));
        assertEquals(0xABCDEF12, Bits32_24_8.pack(0xABCDEF, 0x12));
        assertEquals(0x00000000, Bits32_24_8.pack(0x000000, 0x00));
        assertEquals(0x00FF00FF, Bits32_24_8.pack(0x00FF00, 0xFF));
    }

    @Test
    void testPackInvalidCases() {
        assertThrows(IllegalArgumentException.class, () -> Bits32_24_8.pack(0x01000000, 0x00)); // bits24 dépasse 24 bits
        assertThrows(IllegalArgumentException.class, () -> Bits32_24_8.pack(0x000000, 0x0100)); // bits8 dépasse 8 bits
        assertThrows(IllegalArgumentException.class, () -> Bits32_24_8.pack(0xFFFFFF, 0x1FF)); // bits8 dépasse 8 bits
        assertThrows(IllegalArgumentException.class, () -> Bits32_24_8.pack(0x1000000, 0xFF)); // bits24 dépasse 24 bits
        assertThrows(IllegalArgumentException.class, () -> Bits32_24_8.pack(-1, 0xFF)); // bits24 négatif
        assertThrows(IllegalArgumentException.class, () -> Bits32_24_8.pack(0xFFFFFF, -1)); // bits8 négatif
    }


}
