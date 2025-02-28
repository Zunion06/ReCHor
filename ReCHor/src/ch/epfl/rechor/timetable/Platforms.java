package ch.epfl.rechor.timetable;

import ch.epfl.rechor.journey.Vehicle;

public interface Platforms extends Indexed{

    //return the type of vehicle on the line of the given index
    //throw IndexOutOfBoundsException for invalid index
    abstract Vehicle vehicle(int id);

    //return the name of the line of the given index
    //throw IndexOutOfBoundsException for invalid index
    abstract String name(int id);
}
