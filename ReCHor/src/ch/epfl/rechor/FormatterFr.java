package ch.epfl.rechor;

import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.Stop;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;

public final class FormatterFr {
    static String formatDuration(Duration duration) {
        if (((int) duration.getSeconds()) % 3600 > 0) {
            return (((int) duration.getSeconds()) / 3600 + " h " + ((int) duration.getSeconds() / 60) + " min");
        }
        return (((int) duration.getSeconds() / 60) + " min");
    }

    static String formatTime(LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime);
        return (dateTime.getHour() + "h" + dateTime.getMinute());
    }

    static String formatPlatformName(Stop stop) {
        Objects.requireNonNull(stop);
        if (isDigit(stop.plateformName().charAt(0))) {
            return ("voie " + stop.plateformName());
        } else if (isLetter(stop.plateformName().charAt(0))) {
            return ("quai " + stop.plateformName());
        }
        Preconditions.checkArgument(true);
        return(null);
    }

    static String formatLeg(Journey.Leg.Foot footLeg){
        Objects.requireNonNull(footLeg);
        if(footLeg.isTransfer()){
            return("changement ("+formatDuration(footLeg.duration())+")");
        } else {
            return("trajet à pied ("+formatDuration((footLeg.duration()))+")");
        }
    }

    static String formatLeg(Journey.Leg.Transport leg){
        Objects.requireNonNull(leg);
        StringBuilder format = new StringBuilder(50);
        StringBuilder.

        if ((leg.depStop().plateformName()!=null)&&(leg.arrStop().plateformName()!=null)) {
            return(leg.depTime()+" "+leg.depStop()+" ("+leg.depStop().plateformName()+") → "
                    +leg.arrStop()+" (arr. "+leg.arrTime()+" "+leg.arrStop().plateformName()+")");
        }
        return(leg.depTime()+" "+leg.depStop()+" → "+leg.arrStop()+" (arr. "+leg.arrTime()+")");
    }

    static String formatRouteDestination(Journey.Leg.Transport transportLeg){
        Objects.requireNonNull(transportLeg);
        return(transportLeg.route()+" Direction "+transportLeg.destination());
    }
}
