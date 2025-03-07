package ch.epfl.rechor.journey;

import org.junit.jupiter.api.Test;
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
        long[] step6 = {tuple1,tuple3,tuple4,tuple5};

        builder.add(tuple1);

    }
}
