package ch.epfl.rechor.journey;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import ch.epfl.rechor.journey.PackedCriteria;
import ch.epfl.rechor.journey.ParetoFront;

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
//            assertEquals(paretofront.toArray()[i],copyArray.get(i));
//            assertEquals(paretofront.toArray()[i],customArray.get(i)-1);
//            assertNotEquals(paretofront.toArray()[i],customArray.get(i));
        }

    }
}
