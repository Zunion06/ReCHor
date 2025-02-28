package ch.epfl.rechor.journey;

import java.util.function.LongConsumer;

public class ParetoFront {
    long[] tupleTable;
    public static final ParetoFront EMPTY = new ParetoFront();

    private ParetoFront(){}

    private ParetoFront(long[] tupleTable){
        this.tupleTable=tupleTable;
    }

    // Returns the size of the Pareto front (number of optimization tuples).
    public int size(){
        return tupleTable.length;
    }

    // Returns the packed optimization criteria for the given arrival time and changes, or throws NoSuchElementException if not found.
    public long get(int arrMins, int changes){
        return -1;
    }

    // Calls the given LongConsumer action for each criterion in the Pareto front, in order.
    public void forEach(LongConsumer action){
    }



}
