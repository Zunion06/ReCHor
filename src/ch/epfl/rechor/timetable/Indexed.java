package ch.epfl.rechor.timetable;

/**
 * destined to be extended by every interface indexing elements to be able to implement the abstract method size().
 * @author Matthias Gibon (397827)
 */
public interface Indexed {

    /**
     * gives the size of the index list used (number of elements)
     * @return the number of indexed elements in the index list
     */
    public abstract int size();


}
