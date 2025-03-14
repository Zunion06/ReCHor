package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.Bits32_24_8;
import ch.epfl.rechor.journey.Vehicle;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MyBufferedPlatformTest {
    HexFormat hexFormat = HexFormat.ofDelimiter(" ");
    byte[] bytes = hexFormat.parseHex("00 00 00 00 00 01 00 00 00 00 00 01");
    ByteBuffer buffer = ByteBuffer.wrap(bytes);

    @Test
    void BufferedPlatformWorksOnExample() {
        List<String> stringList = List.of("1", "70");
        BufferedPlatforms bufferedPlatforms = new BufferedPlatforms(stringList,buffer);

        assertEquals("1",bufferedPlatforms.name(0));
        assertEquals("70",bufferedPlatforms.name(1));
        assertEquals("1",bufferedPlatforms.name(2));

        assertEquals(0,bufferedPlatforms.stationId(0));
        assertEquals(0,bufferedPlatforms.stationId(1));
        assertEquals(1,bufferedPlatforms.stationId(2));

        assertEquals(3,bufferedPlatforms.size());
    }

    @Test
    void BufferedPlatformThrowsIndexOutOfBound() {
        HexFormat hexFormat = HexFormat.ofDelimiter(" ");
        byte[] bytes = hexFormat.parseHex("00 03 00 00 00 01 00 00 00 00 00 01");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        List<String> stringList = List.of("1", "70");
        BufferedPlatforms bufferedPlatforms = new BufferedPlatforms(stringList,buffer);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            bufferedPlatforms.name(0);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            bufferedPlatforms.name(3);
        });

    }

    @Test
    void BufferedPlatformThrowsOnWeirdCase() {
        HexFormat hexFormat = HexFormat.ofDelimiter(" ");
        byte[] bytes = hexFormat.parseHex("");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        List<String> stringList = List.of("1", "70");
        BufferedPlatforms bufferedPlatforms = new BufferedPlatforms(stringList,buffer);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            bufferedPlatforms.name(0);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            bufferedPlatforms.name(3);
        });

    }
}
