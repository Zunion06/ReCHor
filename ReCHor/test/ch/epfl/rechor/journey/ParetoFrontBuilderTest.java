package ch.epfl.rechor.journey;

import org.junit.jupiter.api.Test;

public class ParetoFrontBuilderTest {

    @Test
    void ParetoFrontBuilderAddWorks() {
        ParetoFront.Builder builder = new ParetoFront.Builder();

        long tuple1 = PackedCriteria.pack(8*60,3,0);
        long tuple2 = PackedCriteria.pack(8*60,4,0);
        long tuple6 = PackedCriteria.pack(8*60+4,1,0);
        long tuple3 = PackedCriteria.pack(8*60+1,2,0);
        long tuple4 = PackedCriteria.pack(8*60+2,1,0);
        long tuple5 = PackedCriteria.pack(8*60+3,0,0);

        System.out.println(builder.add(tuple1).build());
        System.out.println(builder.add(tuple2).build());
        System.out.println(builder.add(tuple6).build());
        System.out.println(builder.add(tuple3).build());
        System.out.println(builder.add(tuple4).build());
        System.out.println(builder.add(tuple5).build());



    }

}
