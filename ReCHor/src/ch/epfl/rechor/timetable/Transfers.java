package ch.epfl.rechor.timetable;

public interface Transfers extends Indexed{

    //return the index of the departing station for a given transfer id
    //throw IndexOutOfBoundsException for invalid index
    abstract int depStationId(int id);

    //return the length (in minutes) for a given transfer id
    //throw IndexOutOfBoundsException for invalid index
    abstract int minutes(int id);

    //return the packed interval following the conventions used in PackedRange for a given transfer id
    //throw IndexOutOfBoundsException for invalid index
    abstract int arrivingAt(int stationId);

    //return the length (in minute) of a transfer between two stations of given id
    //throws NoSuchElementException if no transfer is possible between both stations
    //throw IndexOutOfBoundsException for invalid index
    abstract int minutesBetween(int depStationId, int arrStationId);
}
