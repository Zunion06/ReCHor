package ch.epfl.rechor.timetable;

public interface Connections extends Indexed{

    // Returns the index of the departure stop for the given connection index
    //throw IndexOutOfBoundsException for invalid index
    abstract int depStopId(int id);

    // Returns the departure time in minutes after midnight for the given connection index
    //throw IndexOutOfBoundsException for invalid index
    abstract int depMins(int id);

    // Returns the index of the arrival stop for the given connection index
    //throw IndexOutOfBoundsException for invalid index
    abstract int arrStopId(int id);

    // Returns the arrival time in minutes after midnight for the given connection index
    //throw IndexOutOfBoundsException for invalid index
    abstract int arrMins(int id);

    // Returns the index of the trip to which the given connection belongs
    //throw IndexOutOfBoundsException for invalid index
    abstract int tripId(int id);

    // Returns the position of the given connection in its trip (first connection is 0)
    //throw IndexOutOfBoundsException for invalid index
    abstract int tripPos(int id);

    // Returns the index of the next connection in the trip, or the first if it's the last
    //throw IndexOutOfBoundsException for invalid index
    abstract int nextConnectionId(int id);
}
