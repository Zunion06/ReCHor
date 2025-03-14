package ch.epfl.rechor.timetable.mapped;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MyBufferedStationsTest {

    public BufferedStations dummyStation(){
        ArrayList<String> stringList = new ArrayList<>();
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
    public void StationsLongitudeTest(){

        BufferedStations bufferedStations = dummyStation();

//        assertEquals("name 0", bufferedStations.longitude(0));
//        assertEquals("name 1",bufferedStations.longitude(1));
//        assertEquals("name 2",bufferedStations.longitude(2));
        System.out.println(bufferedStations.structuredBuffer.getS32(1,0));
        System.out.println(bufferedStations.longitude(0));
    }
}
