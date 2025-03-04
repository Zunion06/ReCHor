package ch.epfl.rechor.journey;

import java.util.NoSuchElementException;
import java.util.StringJoiner;
import java.util.function.LongConsumer;

import static ch.epfl.rechor.journey.PackedCriteria.arrMins;
import static ch.epfl.rechor.journey.PackedCriteria.changes;

public class ParetoFront {
    long[] tupleTable;
    public static final ParetoFront EMPTY = new ParetoFront(new long[0]);

    private ParetoFront(long[] tupleTable){
        this.tupleTable=tupleTable;
    }

    // Returns the size of the Pareto front (number of optimization tuples).
    public int size(){
        return tupleTable.length;
    }

    // Returns the packed optimization criteria for the given arrival time and changes, or throws NoSuchElementException if not found.
    public long get(int arrMins, int changes){
        for (long tuple : tupleTable) {
            if ((arrMins(tuple) == arrMins) && (changes(tuple) == changes)) {
                return tuple;
            }
        }
        throw new NoSuchElementException();
    }

    // Calls the given LongConsumer action for each criterion in the Pareto front, in order.
    public void forEach(LongConsumer action){
        for (long tuple : tupleTable) {
            action.accept(tuple);
        }
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();
        for (long tuple : tupleTable) {
            builder.append(tuple).append("\n");
        }
        return builder.toString();
    }



}
