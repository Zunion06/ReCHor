package ch.epfl.rechor.timetable;

public interface StationAliases extends Indexed {

    //return the alias of the station from a given id
    //throw IndexOutOfBoundsException for invalid index
    abstract String alias(int id);

    //return the name of the station from a given id
    //throw IndexOutOfBoundsException for invalid index
    abstract String stationName(int id);
}
