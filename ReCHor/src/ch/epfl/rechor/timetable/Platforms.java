package ch.epfl.rechor.timetable;

import ch.epfl.rechor.journey.Vehicle;

public interface Platforms extends Indexed{

    //return the name of the platform of the given index
    //throw IndexOutOfBoundsException for invalid index
    abstract String name(int id);

    //return the station index of the given platform index
    //throw IndexOutOfBoundsException for invalid index
    abstract int stationId(int id);
}
