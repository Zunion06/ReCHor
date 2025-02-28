package ch.epfl.rechor.timetable;

import java.time.LocalDate;

public interface TimeTable {

    // Returns the indexed stations of the timetable.
    abstract Stations stations();

    // Returns the indexed alternative names of stations in the timetable.
    abstract StationAliases stationAliases();

    // Returns the indexed platforms/tracks of the timetable.
    abstract Platforms platforms();

    // Returns the indexed routes of the timetable.
    //abstract Routes routes();

    // Returns the indexed transfers of the timetable.
    abstract Transfers transfers();

    // Returns the indexed trips active on the given date.
    abstract Trips tripsFor(LocalDate date);

    // Returns the indexed connections active on the given date.
    abstract Connections connectionsFor(LocalDate date);

    // Returns true if the given stop index is a station (not a platform).
    default boolean isStationId(int stopId){
        return false;//might need to be changed
    }

    // Returns true if the given stop index is a platform (not a station).
    default boolean isPlatformId(int stopId){
        return false;//might need to be changed
    }

    // Returns the station index of the given stop (same if the stop is a station).
    default int stationId(int stopId){
        return -1;//might need to be changed
    }

    // Returns the platform name of the given stop, or null if it is a station.
    default String platformName(int stopId){
        return null;//might need to be changed
    }
}
