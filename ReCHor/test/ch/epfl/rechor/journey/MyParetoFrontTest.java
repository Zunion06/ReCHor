package ch.epfl.rechor.journey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class MyParetoFrontTest {

    @Test
    public void paretoGetTest(){
        long tuple1 = PackedCriteria.pack(8*60,3,0);
        long tuple2 = PackedCriteria.pack(8*60,4,0);
        long tuple6 = PackedCriteria.pack(8*60+4,1,0);
        long tuple3 = PackedCriteria.pack(8*60+1,2,0);
        long tuple4 = PackedCriteria.pack(8*60+2,1,0);
        long tuple5 = PackedCriteria.pack(8*60+3,0,0);

        ParetoFront.Builder builder = new ParetoFront.Builder();
        long[] expected = {tuple1,tuple3,tuple4,tuple5};

        ParetoFront paretofront=builder.add(tuple1)
                .add(tuple2)
                .add(tuple3)
                .add(tuple4)
                .add(tuple5)
                .add(tuple6)
                .build()
                ;

        assertEquals(tuple5,paretofront.get(8*60+3,0));
        assertEquals(tuple3,paretofront.get(8*60+1,2));

        assertThrows(NoSuchElementException.class, () -> {
            paretofront.get(8*60,2);
        });
        assertThrows(NoSuchElementException.class, () -> {
            paretofront.get(0,3);
        });
    }

    @Test
    public void paretoForEachTest(){

        long tuple1 = PackedCriteria.pack(8*60,3,0);
        long tuple2 = PackedCriteria.pack(8*60,4,0);
        long tuple6 = PackedCriteria.pack(8*60+4,1,0);
        long tuple3 = PackedCriteria.pack(8*60+1,2,0);
        long tuple4 = PackedCriteria.pack(8*60+2,1,0);
        long tuple5 = PackedCriteria.pack(8*60+3,0,0);

        ParetoFront.Builder builder = new ParetoFront.Builder();
        long[] expected = {tuple1,tuple3,tuple4,tuple5};

        ParetoFront paretofront=builder.add(tuple1)
                .add(tuple2)
                .add(tuple3)
                .add(tuple4)
                .add(tuple5)
                .add(tuple6)
                .build()
                ;

        ArrayList<Long> customArray = new ArrayList<>();
        //System.out.println("pre op : "+paretofront.tupleTable[0]);
        paretofront.forEach(x -> customArray.add(x+1));
        //System.out.println("post op : "+ customArray.getFirst());

        ArrayList<Long> copyArray = new ArrayList<>();
        paretofront.forEach(copyArray::add);
        for(int i=0;i<paretofront.size();i++){
            assertEquals(paretofront.toArray()[i],copyArray.get(i));
            assertEquals(paretofront.toArray()[i],customArray.get(i)-1);
            assertNotEquals(paretofront.toArray()[i],customArray.get(i));
        }

    }

    //############################## test Alienor ##########################

    private ParetoFront paretoFront;

    @BeforeEach
    void setUp() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(60, 2, 100)  // Arrivée 60 min, 2 changements, payload 100
                .add(80, 1, 150)  // Arrivée 80 min, 1 changement, payload 150
                .add(100, 0, 200); // Arrivée 100 min, 0 changement, payload 200
        paretoFront = builder.build();
    }


    @Test
    void testSize() {
        assertEquals(3, paretoFront.size());
    }


    @Test
    void testGet() {
        assertDoesNotThrow(() -> paretoFront.get(60, 2));
        assertDoesNotThrow(() -> paretoFront.get(80, 1));
        assertDoesNotThrow(() -> paretoFront.get(100, 0));
    }


    @Test
    void testGetThrowsExceptionForMissingElement() {
        assertThrows(NoSuchElementException.class, () -> paretoFront.get(90, 1));
    }

    @Test
    void testForEach() {
        List<Long> collected = new ArrayList<>();
        paretoFront.forEach(collected::add);
        assertEquals(3, collected.size());
    }


}
