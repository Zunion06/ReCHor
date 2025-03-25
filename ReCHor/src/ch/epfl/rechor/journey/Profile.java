package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.Connections;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.Trips;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public record Profile(TimeTable timeTable, LocalDate date, int arrStationId, List<ParetoFront> stationFront) {
    /**
     * table containing the timeTable, date, arrival station and paretoFront list for each stops of a travel
     * @param timeTable list of transports and their dates
     * @param date date of the profile
     * @param arrStationId arrival station
     * @param stationFront list of ParetoFront for each stationId
     */
    public Profile(TimeTable timeTable, LocalDate date, int arrStationId, List<ParetoFront> stationFront){
        this.timeTable=timeTable;
        this.date=date;
        this.arrStationId=arrStationId;
        this.stationFront=List.copyOf(stationFront);
    }

    /**
     * gives the connection for the date of the profile
     * @return the connection at the date of the profile
     */
    public Connections connections(){
        return timeTable.connectionsFor(date);
    }

    /**
     * gives thetrip for the date of the profile
     * @return the trip at the date of the profile
     */
    public Trips trips(){
        return timeTable.tripsFor(date);
    }

    /**
     * gives the pareto frontier for a given station index
     * @param stationId index of the given station
     * @return the paretoFront of the given station
     * @throws IndexOutOfBoundsException if index is invalid
     */
    public ParetoFront forStation(int stationId){
        return stationFront.get(stationId);
    }

    public final static class Builder {
        TimeTable timeTable;
        LocalDate date;
        int arrStationId;
        ParetoFront.Builder[] stationBuilderFront;
        ParetoFront.Builder[] tripBuilderFront;

        /**
         * create an instance of Profile Builder with the given information in order to create a profile
         * @param timeTable timeTable containing the information of the travel and dates
         * @param date date of the future profile
         * @param arrStationId arrival station of the future profile
         */
        public Builder(TimeTable timeTable, LocalDate date, int arrStationId){
            this.timeTable=timeTable;
            this.date=date;
            this.arrStationId=arrStationId;
            this.stationBuilderFront=new ParetoFront.Builder[timeTable.stations().size()];
            this.tripBuilderFront=new ParetoFront.Builder[timeTable.tripsFor(date).size()];
        }

        /**
         * gives the pareto builder associated to the given station index
         * @param stationId station index of the builder
         * @throws IndexOutOfBoundsException for invalid index
         */
        public ParetoFront.Builder forStation(int stationId){
            return stationBuilderFront[stationId];
        }

        /**
         * associate the paretoBuilder to the pareto front for the given station index
         * @param stationId station index of the builder
         * @param builder builder to associated
         * @throws IndexOutOfBoundsException for invalid index
         */
        public void setForStation(int stationId, ParetoFront.Builder builder){
            stationBuilderFront[stationId]=builder;
        }

        /**
         * gives the pareto builder associated to the given trip index
         * @param tripId trip index of the builder
         * @throws IndexOutOfBoundsException for invalid index
         */
        public ParetoFront.Builder forTrip(int tripId){
            return tripBuilderFront[tripId];
        }

        /**
         * associate the paretoBuilder to the pareto front for the given trip index
         * @param tripId trip index of the builder
         * @param builder builder to associated
         * @throws IndexOutOfBoundsException for invalid index
         */
        public void setForTrip(int tripId, ParetoFront.Builder builder){
            tripBuilderFront[tripId]=builder;
        }

        /**
         * Create an instance of Profile with informations given to the builder (frontier builder who haven't been initialised will create empty frontiers)
         * @return an instance of Profile with the information given to the builder
         */
        public Profile build(){
            List<ParetoFront> stationFront=new ArrayList<>();
            for(ParetoFront.Builder builder : stationBuilderFront){
                if(builder==null){stationFront.add(ParetoFront.EMPTY);}
                else{stationFront.add(builder.build());}
            }
            return new Profile(timeTable,date,arrStationId,stationFront);
        }


    }
}
