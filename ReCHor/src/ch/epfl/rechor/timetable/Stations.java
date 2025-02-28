package ch.epfl.rechor.timetable;

public interface Stations extends Indexed {

    //return the name of the station for a given index
    //throw IndexOutOfBoundsException for invalid index
    abstract String name(int id);

    //return the longitude (in degree) of the station for a given index
    //throw IndexOutOfBoundsException for invalid index
    abstract double longitude(int id);

    //return the latitude (in degree) of the station for a given index
    //throw IndexOutOfBoundsException for invalid index
    abstract double latitude(int id);
}
