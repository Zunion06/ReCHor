package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public record Journey(List<Leg> legs) {
    /**
     * @param legs list of steps to be taken for the journey
     * @throws IllegalArgumentException if legs is empty, if Foot steps do not
     * alternate with Transport steps, if the start time and end time do not align
     * @throws NullPointerException if legs is null
     */
    public Journey{
        legs=List.copyOf(legs); //make a copy to be immutable
        Preconditions.checkArgument(!legs.isEmpty());
        Objects.requireNonNull(legs); //check for null list
        for(int i=1; i<legs.size();i++){
            if(legs.get(i) instanceof Leg.Foot){//check that foot steps alternate with transport
                Preconditions.checkArgument(!(legs.get(i-1) instanceof Leg.Foot));
            }
            if(legs.get(i) instanceof Leg.Transport){
                Preconditions.checkArgument(!(legs.get(i-1) instanceof Leg.Transport));
            }
            Preconditions.checkArgument(legs.get(i-1).arrTime().isBefore(legs.get(i).depTime())||
                    legs.get(i-1).arrTime().isEqual(legs.get(i).depTime()));
            Preconditions.checkArgument(legs.get(i-1).arrStop().equals(legs.get(i).depStop()));
        }
    }

    /**
     * @return the first Stop of the journey
     */
    public Stop depStop() {
        return legs.getFirst().depStop();
    }

    /**
     * @return the last Stop of the journey
     */
    public Stop arrStop(){
        return legs.getLast().arrStop();
    }

    /**
     * @return at what time the journey end
     */
    public LocalDateTime depTime(){
        return legs.getFirst().depTime();
    }

    /**
     * @return at what time the journey starts
     */
    public LocalDateTime arrTime(){
        return legs.getLast().arrTime();
    }

    /**
     * @return the duration of the journey (between its start and end)
     */
    public Duration duration(){
        return Duration.between(legs.getFirst().depTime(),
                legs.getLast().arrTime());
    }

    public sealed interface Leg {

        abstract Stop depStop();

        abstract LocalDateTime depTime();

        abstract Stop arrStop();

        abstract LocalDateTime arrTime();

        abstract List<IntermediateStop> intermediateStops();

        /**
         * @return the duration of the journey step
         */
        default Duration duration(){
            return Duration.between(depTime(),arrTime());
        }

        public record IntermediateStop (Stop stop,LocalDateTime arrTime,LocalDateTime depTime) {
            /**
             * @param stop given Intermediate Stop
             * @param arrTime arrival time at the stop
             * @param depTime depart time from the stop
             * @throws NullPointerException if stop is null
             * @throws IllegalArgumentException if arrTime is before or equal to depTime
             */
            public IntermediateStop{
                Objects.requireNonNull(stop);
                Preconditions.checkArgument(arrTime.isBefore(depTime)||
                        arrTime.isEqual(depTime));
            }
        }
        public record Transport (Stop depStop, LocalDateTime depTime, Stop arrStop,
                                 LocalDateTime arrTime, List<IntermediateStop> intermediateStops,
                                 Vehicle vehicle,String route,String destination) implements Leg {
            /**
             * @param depStop departing stop
             * @param depTime departing time
             * @param arrStop arrival stop
             * @param arrTime arrival time
             * @param intermediateStops list of intermediate stops before arrival
             * @param vehicle type of vehicle used
             * @param route name of the route taken
             * @param destination name of the destination
             * @throws NullPointerException if one of the arguments is null
             * @throws IllegalArgumentException if arrival time is before or equal to the departing time
             */
            public Transport{
               Objects.requireNonNull(depStop);
               Objects.requireNonNull(depTime);
               Objects.requireNonNull(arrStop);
               Objects.requireNonNull(arrTime);
               Objects.requireNonNull(vehicle);
               Objects.requireNonNull(route);
               Objects.requireNonNull(destination);
               Preconditions.checkArgument(arrTime.isAfter(depTime)||
                        arrTime.isEqual(depTime));
               intermediateStops=List.copyOf(intermediateStops);
            }
        }
        public record Foot(Stop depStop,LocalDateTime depTime,Stop arrStop,LocalDateTime arrTime) implements Leg {
            /**
             * @param depStop departing stop
             * @param depTime departing time
             * @param arrStop arrival stop
             * @param arrTime arrival time
             * @throws NullPointerException if any of the parameters are null
             * @throws IllegalArgumentException if arrival time is before or equal to the departing time
             */
            public Foot{
                Objects.requireNonNull(depStop);
                Objects.requireNonNull(depTime);
                Objects.requireNonNull(arrStop);
                Objects.requireNonNull(arrTime);
                Preconditions.checkArgument(depTime.isBefore(arrTime)||
                        depTime.isEqual(arrTime));
            }

            /**
             * @return an empty list because travel by Foot does not have intermediate stops
             */
            public List<IntermediateStop> intermediateStops(){
                return List.of();
            }

            /**
             * @return true if the step is a transfer
             */
            public boolean isTransfer(){
                return(depStop.name().equals(arrStop.name()));
            }
        }
    }
}
