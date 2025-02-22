package ch.epfl.rechor;

import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.Stop;
import ch.epfl.rechor.journey.Vehicle;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Bits32_24_8Test {
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
}
