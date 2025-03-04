package ch.epfl.rechor.journey;

import java.util.NoSuchElementException;
import java.util.StringJoiner;
import java.util.function.LongConsumer;

import static ch.epfl.rechor.journey.PackedCriteria.*;

public class ParetoFront {
    private long[] tupleTable;
    public static final ParetoFront EMPTY = new ParetoFront(new long[0]);
    private int size;

    private ParetoFront(long[] tupleTable){
        this.tupleTable=tupleTable;
    }

    // Returns the size of the Pareto front (number of optimization tuples).
    public int size(){
        return size;
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
            builder.append('(');
            if (PackedCriteria.hasDepMins(tuple)) {
                builder.append(PackedCriteria.depMins(tuple))
                        .append(',');
            }
            builder.append(PackedCriteria.arrMins(tuple))
                    .append(',')
                    .append(PackedCriteria.changes(tuple))
                    .append(')')
                    .append('\n');
        }
        return builder.toString();
    }


    public static class Builder {
        private long[] tupleTable;
        private int size;

        public Builder(){
            tupleTable=new long[0];
        }

        public Builder(Builder that){
            this.tupleTable=that.tupleTable;
            this.size=that.size;
        }

        public boolean isEmpty(){
            return tupleTable.length==0;
        }

        public Builder clear(){
            tupleTable=new long[tupleTable.length];
            return this;
        }

        public Builder add(long packedTuple){
            //???
            return this;
        }

        public Builder add(int arrMins, int changes, int payload){
            add(pack(arrMins,changes,payload));
            return this;
        }

        public Builder addAll(Builder that){
            for(int i=0;i<that.size;i++){
                add(that.tupleTable[i]);
            }
            return this;
        }

        public boolean fullyDominates(Builder that, int depMins){
            //???
            return false;
        }

        public void forEach(LongConsumer action){
            //how to call the forEach method of ParetoFront???
        }

        public ParetoFront build(){
            return new ParetoFront(tupleTable);
        }

    }

}
