package ch.epfl.rechor.timetable;

import ch.epfl.rechor.journey.Vehicle;

public interface Routes extends Indexed{

    //return the type of vehicle on a line of given id
    //throw IndexOutOfBoundsException for invalid index
    abstract Vehicle vehicle(int id);

    //return the name of the route of the given id
    //throw IndexOutOfBoundsException for invalid index
    abstract String name(int id);
}
