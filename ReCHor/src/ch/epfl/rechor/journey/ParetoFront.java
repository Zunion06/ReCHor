package ch.epfl.rechor.journey;

import java.util.NoSuchElementException;
import java.util.function.LongConsumer;

import static ch.epfl.rechor.journey.PackedCriteria.*;

/**
 * used to create a Pareto Frontier to compare tuple between each other
 * @author Matthias Gibon (397827)
 * @author Guillaume Pommier (401065)
 */
public final class ParetoFront {
    private final long[] tupleTable;
    private final static int SECONDS_IN_MINUTE = 60;
    /**
     * empty pareto frontier used to avoid using null
     */
    public static final ParetoFront EMPTY = new ParetoFront(new long[0]);

    /**
     * create an instance of ParetoFront with the list of tuple as the tuple table
     * @param tupleTable table to be set as the tuple table
     */
    private ParetoFront(long[] tupleTable){
        this.tupleTable=tupleTable;
    }

    /**
     * gives the size of the Pareto front (number of optimization tuples).
     * @return the size of the Pareto front (number of optimization tuples).
     */
    public int size(){
        return tupleTable.length;
    }

    /**
     * search a tuple in the frontier
     * @param arrMins arrival time of the tuple
     * @param changes number of changes of the tuple
     * @return the packed optimization criteria for the given arrival time and changes,
     * or throws NoSuchElementException if not found.
     * @throws NoSuchElementException if tuple is not found
     */
    public long get(int arrMins, int changes){
        for (int i=0;i<size();i++) {
            if ((arrMins(tupleTable[i]) == arrMins) && (changes(tupleTable[i]) == changes)) {
                return tupleTable[i];
            }
        }
        throw new NoSuchElementException(); //if no tuple were found
    }

    /**
     * Calls the given LongConsumer action for each element in the Pareto front, in order.
     * @param action action to be done for each tuple of the frontier
     */
    public void forEach(LongConsumer action){
        for (int i=0;i<size();i++) {
            action.accept(tupleTable[i]);
        }
    }

    /**
     * convert the frontier into a text format for testing
     * @return the frontier as a String
     */
    public String toString(){
        StringBuilder builder = new StringBuilder();
        for (long tuple : tupleTable) {
            builder.append('(');
            if (hasDepMins(tuple)) {
                int depMins = depMins(tuple);
                builder.append(depMins / SECONDS_IN_MINUTE)
                        .append('h')
                        .append(depMins % SECONDS_IN_MINUTE)
                        .append(',');
            }
            int arrMins = arrMins(tuple);
            builder.append(arrMins / SECONDS_IN_MINUTE)
                    .append('h')
                    .append(arrMins % SECONDS_IN_MINUTE)
                    .append(',')
                    .append(changes(tuple))
                    .append(')')
                    .append('\n')
                    .append(' ')
                    .append(payload(tuple));
        }
        return builder.toString();
    }


    /**
     * used to build step by step a Pareto Frontier
     * @author Guillaume Pommier
     */
    public static class Builder {
        private long[] tupleTable;
        private int size;

        /**
         * create an instance of builder with a base tuple table length of 2
         */
        public Builder(){
            tupleTable=new long[2];
        }

        /**
         * create an instance of Builder copying the values of another builder
         * @param that builder whose values will be copied
         */
        public Builder(Builder that){
            this.tupleTable=that.tupleTable;
            this.size=that.size;
        }

        /**
         * checks if the number of element in the table is equal to 0
         * @return true if the tuple table is empty
         */
        public boolean isEmpty(){
            return size==0;
        }

        /**
         * removes all elements of the builder
         * @return the builder itself
         */
        public Builder clear(){
            tupleTable=new long[tupleTable.length];
            this.size=0;
            return this;
        }

        /**
         * add a tuple to the frontier and remove it or other tuple if needed
         * @param packedTuple tuple to be added
         * @return the builder itself modified according to what is the tuple in parameter
         */
        public Builder add(long packedTuple){
            int placementIndex = 0;
            for (int i=0;i<size;i++){
                if (dominatesOrIsEqual(tupleTable[i],packedTuple)){
                    return this;
                }
                if (packedTuple < tupleTable[i]){
                    break;
                }
                placementIndex=i+1;
            }
            size = reordering(placementIndex,packedTuple);
            if (size<tupleTable.length){
                System.arraycopy(tupleTable, placementIndex,tupleTable,placementIndex+1,
                        size-placementIndex);
                tupleTable[placementIndex]=packedTuple;
            }
            else {
                int newTupleTableLength = tupleTable.length + tupleTable.length / 2;
                long[] newTable = new long[newTupleTableLength];
                System.arraycopy(tupleTable, 0,newTable,0,placementIndex);
                System.arraycopy(tupleTable, placementIndex,newTable,placementIndex+1,
                        size-placementIndex);
                newTable[placementIndex]=packedTuple;
                tupleTable = newTable;
            }
            size++;
            return this;
        }

        /**
         * Eliminates elements dominated by the new Tuple can be done by iterating over those that
         * follow the
         * insertion position and copying those that are not dominated downward so that they are
         * placed right after
         * the insertion position
         * @param startIndex the index to which to start the iteration (useful to save time)
         * @param packedTuple the new packed Tuple we want to insert
         * @return the new effective size of the tupleTable
         */
        private int reordering(int startIndex,long packedTuple){
            int dst = startIndex;
            for (int src = startIndex; src < size; src ++) {
                if (dominatesOrIsEqual(packedTuple,tupleTable[src])) {
                    continue;
                }
                if (dst != src) tupleTable[dst] = tupleTable[src];
                dst += 1;
            }
            return dst;
        }

        /**
         * add a tuple to the table with the given packed values
         * @param arrMins arrival time of the tuple
         * @param changes number of changes of the tuple
         * @param payload payload of the tuple
         * @return the builder itself
         */
        public Builder add(int arrMins, int changes, int payload){
            return add(pack(arrMins,changes,payload));
        }

        /**
         * add a tuple to the table with the given packed values
         * @param depMins depart time of the tuple
         * @param arrMins arrival time of the tuple
         * @param changes number of changes of the tuple
         * @param payload payload of the tuple
         * @return the builder itself
         */
        public Builder add(int depMins, int arrMins, int changes, int payload){
            long tuple = withDepMins(pack(arrMins,changes,payload),depMins);
            return add(tuple);
        }

        /**
         * add all the tuple of that in the build
         * @param that builder whose elements will all be copied
         * @return the builder itself
         */
        public Builder addAll(Builder that){
            for(int i=0;i<that.size;i++){
                add(that.tupleTable[i]);
            }
            return this;
        }

        /**
         * Check if each tuple in the given frontier, once its departure time is set to the given
         * one, is dominated by
         * or equal to at least one tuple in the receiver
         * @param that the Builder we want to compare to the receiver
         * @param depMins the departure time to be set on the tuple of "that"
         * @return False if there exist a tuple of "that" that, for all tuples of the receiver,
         * is not dominated or equal,
         * returns True otherwise
         */
        public boolean fullyDominates(Builder that, int depMins){
            if (that.size==0) return true;
            boolean domination;
            for (int j=0; j<that.size; j++) {
                domination = false;
                for (int i=0; i<size;i++ ) {
                    if (dominatesOrIsEqual(tupleTable[i], withDepMins(that.tupleTable[j],
                            depMins))) {
                        domination = true;
                    }
                }
                if (!domination) return false;
            }
            return true;
        }


        /**
         * Calls the given LongConsumer action for each element in the builder, in order.
         * @param action action to be done for each tuple of the builder
         */
        public void forEach(LongConsumer action){
            for (int i=0;i<size;i++) {
                action.accept(tupleTable[i]);
            }
        }

        /**
         * create an instance of ParetoFront with the tuple given to the builder
         * @return an instance of ParetoFront with the tuple given to the builder
         */
        public ParetoFront build(){
            long[] finalTable = new long[size];
            System.arraycopy(tupleTable,0,finalTable,0,size);
            return new ParetoFront(finalTable);
        }

    }

}
