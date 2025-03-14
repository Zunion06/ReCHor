package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Test;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;


public class MyBufferedStationsTest {
    ArrayList<String> stringList = new ArrayList<>();
    public BufferedStations dummyStation(){
        stringList.add("name 0");
        stringList.add("name 1");
        stringList.add("name 2");

        HexFormat hexFormat = HexFormat.ofDelimiter(" ");
        byte[] bytes = hexFormat.parseHex("00 00 04 b6 ca 14 21 14 1f a1 00 01 04 dc cc 12 21 18 da 03 00 02 04 dc cc 12 21 18 da 03");
        ByteBuffer buffer = ByteBuffer.wrap(bytes);



        return new BufferedStations(stringList,buffer);
    }

    @Test
    public void StationsNameTest(){

        BufferedStations bufferedStations = dummyStation();
        //name test
        assertEquals("name 0", bufferedStations.name(0));
        assertEquals("name 1",bufferedStations.name(1));
        assertEquals("name 2",bufferedStations.name(2));
    }

    @Test
    public void StationsLongitudeTest() {

        BufferedStations bufferedStations = dummyStation();

        assertEquals(6.629092, bufferedStations.longitude(0),1e-6);
        assertEquals(6.837875, bufferedStations.longitude(1),1e-6);
        assertEquals(6.837875, bufferedStations.longitude(2),1e-6);
//        System.out.println(bufferedStations.structuredBuffer.getS32(1,0));
//        //System.out.println(bufferedStations.structuredBuffer.getS32(1,0)*360*);
//        System.out.println(bufferedStations.longitude(0));
    }

    @Test
    public void StationsLatitudeTest() {

        BufferedStations bufferedStations = dummyStation();

        assertEquals(46.516792, bufferedStations.latitude(0),1e-5);
        assertEquals(46.542764, bufferedStations.latitude(1),1e-5);
        assertEquals(46.542764, bufferedStations.latitude(2),1e-5);
    }

    @Test
    public void StationSizeTest(){
        BufferedStations bufferedStations = dummyStation();
        assertEquals(3,bufferedStations.size());

    }

    @Test
    public void invalidIndexTest(){
        BufferedStations bufferedStations = dummyStation();

        assertThrows(IndexOutOfBoundsException.class, () -> bufferedStations.name(3));
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedStations.longitude(3));
        assertThrows(IndexOutOfBoundsException.class, () -> bufferedStations.latitude(3));
    }
}
