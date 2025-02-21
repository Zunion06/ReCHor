package ch.epfl.rechor.journey;

import ch.epfl.rechor.Preconditions;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public record Journey(List<Leg> legs) {
    public Journey{
        legs=List.copyOf(legs); //make a copy to be immutable
        Preconditions.checkArgument(!legs.isEmpty());
        Objects.requireNonNull(legs); //check for null list
        for(int i=1; i<legs.size()-1;i++){
            if(legs.get(i) instanceof Leg.Foot){//check that foot steps alternate with transport
                Preconditions.checkArgument(!(legs.get(i-1) instanceof Leg.Foot));
            }
            Preconditions.checkArgument(legs.get(i-1).arrTime().isBefore(legs.get(i).depTime())||
                    legs.get(i-1).arrTime().isEqual(legs.get(i).depTime()));
            Preconditions.checkArgument(legs.get(i-1).arrStop().equals(legs.get(i).depStop()));
        }
//salut
    }
    public Stop depStop() {
        return legs.getFirst().depStop();
    }

    public Stop arrStop(){
        return legs.getLast().arrStop();
    }

    public LocalDateTime depTime(){
        return legs.getFirst().depTime();
    }

    public LocalDateTime arrTime(){
        return legs.getLast().arrTime();
    }

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

        default Duration duration(){
            return Duration.between(depTime(),arrTime());
        }

        public record IntermediateStop (Stop stop,LocalDateTime arrTime,LocalDateTime depTime) {
            public IntermediateStop{
                Objects.requireNonNull(stop);
                Preconditions.checkArgument(arrTime.isBefore(depTime)||
                        arrTime.isEqual(depTime));
            }
        }
        public record Transport (Stop depStop, LocalDateTime depTime, Stop arrStop,
                                 LocalDateTime arrTime, List<IntermediateStop> intermediateStops,
                                 Vehicle vehicle,String route,String destination) implements Leg {
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
            public Foot{
                Objects.requireNonNull(depStop);
                Objects.requireNonNull(depTime);
                Objects.requireNonNull(arrStop);
                Objects.requireNonNull(arrTime);
                Preconditions.checkArgument(depTime.isBefore(arrTime)||
                        depTime.isEqual(arrTime));
            }
            public List<IntermediateStop> intermediateStops(){
                return List.of();
            }

            public boolean isTransfer(){
                return(depStop.name().equals(arrStop.name()));
            }
        }
    }
}
