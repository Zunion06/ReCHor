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
        legs=List.copyOf(legs); //make a copy to be immuable
        Objects.requireNonNull(legs); //check for null list
        Leg firstStep=legs.getFirst();
        Leg lastStep=legs.getLast();
        for(int i=0; i<legs.size()-2;i++){
            if(legs.get(i) instanceof Leg.Foot){//check that foot steps alternate with transport
                Preconditions.checkArgument(!(legs.get(i+1) instanceof Leg.Transport));
            }
            if(legs.get(i) instanceof Leg.Transport){//check that transport steps alternate with foot
                Preconditions.checkArgument(!(legs.get(i+1) instanceof Leg.Foot));
            }
            //if(firstStep.getDate)
        }

    }
    public Stop depStop() {
        return null;
    }

    public Stop arrStop(){
        return null;
    }

    public LocalDateTime depTime(){
        return null;
    }

    public LocalDateTime arrTime(){
        return null;
    }

    public Duration duration(){
        return null;
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
                Preconditions.checkArgument(arrTime.isAfter(depTime)||
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
            }
            public List<IntermediateStop> intermediateStops(){
                return List.of();
            }

            public boolean isTransfer(){
                return(depStop.equals(arrStop));
            }
        }
    }
}
