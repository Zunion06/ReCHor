package ch.epfl.rechor.journey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static ch.epfl.rechor.journey.PackedCriteria.*;

public class MyParetoFrontBuilderTest {

    @Test
    void ParetoFrontBuilderAddWorksOnNonTrivialCases() {
        ParetoFront.Builder builder = new ParetoFront.Builder();

        long tuple1 = pack(8*60,3,0);
        long tuple2 = pack(8*60,4,0);
        long tuple6 = pack(8*60+4,1,0);
        long tuple3 = pack(8*60+1,2,0);
        long tuple4 = pack(8*60+2,1,0);
        long tuple5 = pack(8*60+3,0,0);

        long[] step1 = {tuple1};
        long[] step2 = {tuple1};
        long[] step3 = {tuple1,tuple6};
        long[] step4 = {tuple1,tuple3,tuple6};
        long[] step5 = {tuple1,tuple3,tuple4};
        long[] step6 = {tuple1,tuple3,tuple4,tuple5};

        assertArrayEquals(step1,builder.add(tuple1).build().toArray());
//        System.out.println(1);
        assertArrayEquals(step2,builder.add(tuple2).build().toArray());
//        System.out.println(2);
        assertArrayEquals(step3,builder.add(tuple6).build().toArray());
//        System.out.println(3);
        assertArrayEquals(step4,builder.add(tuple3).build().toArray());
//        System.out.println(4);
        assertArrayEquals(step5,builder.add(tuple4).build().toArray());
//        System.out.println(5);
        assertArrayEquals(step6,builder.add(tuple5).build().toArray());
    }

    @Test
    void ParetoFrontBuilderAddWorksOnWeirdCases() {
//        ParetoFront.Builder builder = new ParetoFront.Builder();
//
//        long tuple1 = PackedCriteria.pack(8*60,3,0);
//        long tuple2 = PackedCriteria.pack(8*60,3,1);
//
//        System.out.println(builder.add(tuple1).build());
//        System.out.println(builder.add(tuple2).build());
    }

    @Test
    void ParetoFrontBuilderFullyDominatesWorks() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        ParetoFront.Builder builder2 = new ParetoFront.Builder();

        builder1.add(8*60,9*60,5,0)
                .add(8*60-1,9*60,2,0);

        builder2.add(12*60,4,0);

//        assertTrue();

    }

    //################################## test tibo et ali ##################

    private ParetoFront.Builder builder;


    @BeforeEach
    void setUp() {
        builder = new ParetoFront.Builder();
    }


    @Test
    void testIsEmptyInitially() {
        assertTrue(builder.isEmpty());
    }


    @Test
    void testAddAndSize() {
        builder.add(70, 1, 120);
        assertFalse(builder.isEmpty());
    }


    @Test
    void testClear() {
        builder.add(70, 1, 120).clear();
        assertTrue(builder.isEmpty());
    }


    @Test
    void testBuild() {
        builder.add(70, 1, 120).add(90, 2, 140);
        ParetoFront front = builder.build();
        assertEquals(2, front.size());
    }


    @Test
    void testForEach() {
        builder.add(70, 1, 120).add(90, 2, 140);
        List<Long> collected = new ArrayList<>();
        builder.forEach(collected::add);
        assertEquals(2, collected.size());
    }


    @Test
    void testEmptyParetoFront2() {
        ParetoFront emptyFront = ParetoFront.EMPTY;
        assertEquals(0, emptyFront.size(), "Une frontière vide doit avoir une taille de 0");
        assertThrows(NoSuchElementException.class, () -> emptyFront.get(480, 2), "get() sur une frontière vide doit lever une exception");
    }


    @Test
    void testAddAndSize2() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(480, 2, 100);
        builder.add(500, 1, 200);
        ParetoFront front = builder.build();
        assertEquals(2, front.size(), "La frontière doit contenir 2 tuples après ajout");
    }






    @Test
    void testForEach2() {
        ParetoFront.Builder builder = new ParetoFront.Builder();
        builder.add(480, 2, 100);
        builder.add(500, 1, 200);
        ParetoFront front = builder.build();


        List<Long> results = new ArrayList<>();
        front.forEach(results::add);
        assertEquals(2, results.size(), "forEach doit parcourir tous les éléments");
    }


    @Test
    void testFullyDominates2() {
        ParetoFront.Builder builder1 = new ParetoFront.Builder();
        builder1.add(480, 2, 100);
        builder1.add(500, 1, 200);


        ParetoFront.Builder builder2 = new ParetoFront.Builder();
        builder2.add(490, 3, 150);


        assertTrue(builder1.fullyDominates(builder2, 450), "La première frontière doit dominer la seconde");
    }
}


