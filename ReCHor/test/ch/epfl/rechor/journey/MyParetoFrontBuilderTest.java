package ch.epfl.rechor.journey;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MyParetoFrontBuilderTest {

    @Test
    void ParetoFrontBuilderAddWorksOnNonTrivialCases() {
        ParetoFront.Builder builder = new ParetoFront.Builder();

        long tuple1 = PackedCriteria.pack(8*60,3,0);
        long tuple2 = PackedCriteria.pack(8*60,4,0);
        long tuple6 = PackedCriteria.pack(8*60+4,1,0);
        long tuple3 = PackedCriteria.pack(8*60+1,2,0);
        long tuple4 = PackedCriteria.pack(8*60+2,1,0);
        long tuple5 = PackedCriteria.pack(8*60+3,0,0);

        long[] step1 = {tuple1};
        long[] step2 = {tuple1};
        long[] step3 = {tuple1,tuple6};
        long[] step4 = {tuple1,tuple3,tuple6};
        long[] step5 = {tuple1,tuple3,tuple4};
        long[] step6 = {tuple1,tuple3,tuple4,tuple5};

        assertArrayEquals(step1,builder.add(tuple1).build().tupleTable);
        assertArrayEquals(step2,builder.add(tuple2).build().tupleTable);
        assertArrayEquals(step3,builder.add(tuple6).build().tupleTable);
        assertArrayEquals(step4,builder.add(tuple3).build().tupleTable);
        assertArrayEquals(step5,builder.add(tuple4).build().tupleTable);
        assertArrayEquals(step6,builder.add(tuple5).build().tupleTable);
    }

    @Test
    void ParetoFrontBuilderAddWorksOnWeirdCases() {
        ParetoFront.Builder builder = new ParetoFront.Builder();

        long tuple1 = PackedCriteria.pack(8*60,3,0);
        long tuple2 = PackedCriteria.pack(8*60,3,1);

        System.out.println(builder.add(tuple1).build());
        System.out.println(builder.add(tuple2).build());
    }

}
