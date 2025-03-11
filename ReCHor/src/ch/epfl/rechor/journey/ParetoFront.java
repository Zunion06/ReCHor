package ch.epfl.rechor.journey;

import java.util.NoSuchElementException;
import java.util.function.LongConsumer;

import static ch.epfl.rechor.journey.PackedCriteria.*;

public class ParetoFront {
    private final long[] tupleTable;
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
        for (int i=0;i<size();i++) {
            if ((arrMins(tupleTable[i]) == arrMins) && (changes(tupleTable[i]) == changes)) {
                return tupleTable[i];
            }
        }
        throw new NoSuchElementException();
    }

    // Calls the given LongConsumer action for each criterion in the Pareto front, in order.
    public void forEach(LongConsumer action){
        for (int i=0;i<size();i++) {
            action.accept(tupleTable[i]);
        }
    }

    public long[] toArray() {
        return tupleTable;
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();
        for (long tuple : tupleTable) {
            builder.append('(');
            if (hasDepMins(tuple)) {
                int depMins = depMins(tuple);
                builder.append(depMins/60)
                        .append('h')
                        .append(depMins%60)
                        .append(',');
            }
            int arrMins = arrMins(tuple);
            builder.append(arrMins/60)
                    .append('h')
                    .append(arrMins%60)
                    .append(',')
                    .append(changes(tuple))
                    .append(')')
                    .append('\n');

            builder.append(payload(tuple));
        }
        return builder.toString();
    }


    public static class Builder {
        private long[] tupleTable;
        private int size;

        public Builder(){
            tupleTable=new long[2];
        }

        public Builder(Builder that){
            this.tupleTable=that.tupleTable;
            this.size=that.size;
        }

        public boolean isEmpty(){
            return size==0;
        }

        public Builder clear(){
            tupleTable=new long[tupleTable.length];
            this.size=0;
            return this;
        }

        public Builder add(long packedTuple){
            int placementIndex = 0;
            for (int i=0;i<size;i++){
                if (dominatesOrIsEqual(tupleTable[i],packedTuple)){
//                    System.out.println("loul");
//                    System.out.println(arrMins(packedTuple)+" "+arrMins(tupleTable[i]));
//                    System.out.println(changes(packedTuple)+" "+changes(tupleTable[i]));
//                    System.out.println(dominatesOrIsEqual(tupleTable[i],packedTuple));
                    return this;
                }
                if (packedTuple < tupleTable[i]){
                    break;
                }
                placementIndex=i+1;
            }
//            System.out.println(placementIndex);
            size = reordering(placementIndex,packedTuple);
            if (size<tupleTable.length){
                System.arraycopy(tupleTable, placementIndex,tupleTable,placementIndex+1,size-placementIndex);
                tupleTable[placementIndex]=packedTuple;
            }
            else {
                int newTupleTableLength = tupleTable.length + tupleTable.length / 2;
                long[] newTable = new long[newTupleTableLength];
                System.arraycopy(tupleTable, 0,newTable,0,placementIndex);
                System.arraycopy(tupleTable, placementIndex,newTable,placementIndex+1,size-placementIndex);
                newTable[placementIndex]=packedTuple;
                tupleTable = newTable;
            }
            size++;
            return this;
        }

        private int reordering(int startIndex,long packedTuple){
            int dst = startIndex;
            for (int src = startIndex; src < size; src += 1) {
                if (dominatesOrIsEqual(packedTuple,tupleTable[src])) {
                    //System.out.println("loul");
                    continue;
                }
                if (dst != src) tupleTable[dst] = tupleTable[src];
                dst += 1;
            }
            return dst;
        }

        public Builder add(int arrMins, int changes, int payload){
            add(pack(arrMins,changes,payload));
            return this;
        }

        public Builder add(int depMins, int arrMins, int changes, int payload){
            long tuple = withDepMins(pack(arrMins,changes,payload),depMins);
            add(tuple);
            return this;
        }

        public Builder addAll(Builder that){
            for(int i=0;i<that.size;i++){
                add(that.tupleTable[i]);
            }
            return this;
        }

        // pas terrible le code --> à refaire mieux
        public boolean fullyDominates(Builder that, int depMins){
            if (that.size==0) return true;
            boolean domination;
            for (int j=0; j<size; j++) {
                domination = false;
                for (int i=0; i<that.size;i++ ) {
                    if (dominatesOrIsEqual(tupleTable[i], withDepMins(that.tupleTable[j],depMins))) {
                        domination = true;
                    }
                }
                if (!domination) return false;
            }
            return true;
        }

        // /!\ c'est du copié collé
        public void forEach(LongConsumer action){
            for (int i=0;i<size;i++) {
                action.accept(tupleTable[i]);
            }
        }

        public ParetoFront build(){
            long[] finalTable = new long[size];
            System.arraycopy(tupleTable,0,finalTable,0,size);
            return new ParetoFront(finalTable);
        }

    }

}
