package ch.epfl.rechor.timetable;

public interface Trips extends Indexed{

    //return the index of the line for the id of a given trip
    //throw IndexOutOfBoundsException for invalid index
    abstract int routeId(int id);

    //return the name of the final destination of a given trip
    //throw IndexOutOfBoundsException for invalid index
    abstract String destination(int id);

}
