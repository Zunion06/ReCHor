package ch.epfl.rechor;

import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.Stop;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Objects;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;

public final class FormatterFr {
    static String formatDuration(Duration duration) {
        if (((int) duration.getSeconds()) / 3600 > 0 ) {
            int h=(((int) duration.getSeconds()) / 3600);
            return (h + " h " + (duration.getSeconds()-h*3600) + " min");
        }
        return (((int) duration.getSeconds() / 60) + " min");
    }

    static String formatTime(LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime);
        DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.HOUR_OF_DAY)
                .appendLiteral('h')
                .appendValue(ChronoField.MINUTE_OF_HOUR)
                .toFormatter();
        return (fmt.toString());
    }

    static String formatPlatformName(Stop stop) {
        Objects.requireNonNull(stop);
        StringBuilder builder=new StringBuilder(100);
        if (isDigit(stop.plateformName().charAt(0))) {
            builder.append("voie ");
        } else if (isLetter(stop.plateformName().charAt(0))) {
            builder.append("quai ");
        }
        builder.append(stop.plateformName());
        return(builder.toString());
    }

    static String formatLeg(Journey.Leg.Foot footLeg){
        Objects.requireNonNull(footLeg);
        StringBuilder builder=new StringBuilder(100);
        if(footLeg.isTransfer()) {
            builder.append("changement (");
        }
        else {
            builder.append("trajet à pied (");
        }
         builder.append(formatDuration(footLeg.duration()));
        return(builder.toString());
    }

    static String formatLeg(Journey.Leg.Transport leg){
        Objects.requireNonNull(leg);
        StringBuilder builder = new StringBuilder(100);
        builder.append(leg.depTime()).append(" ");
        if(leg.depStop().plateformName()!=null){
            builder.append(leg.depStop()).append(" (").append(leg.depStop().plateformName()).append(")");
        }
        builder.append(" → ");
        builder.append(leg.arrStop()).append(" (arr. ");
        builder.append(leg.arrTime());
        if(leg.arrStop().plateformName()!=null) {
            builder.append(" ").append(leg.arrStop().plateformName());
        }
        builder.append(")");

        return(builder.toString());
    }

    static String formatRouteDestination(Journey.Leg.Transport transportLeg){
        Objects.requireNonNull(transportLeg);
        StringBuilder builder = new StringBuilder();
        builder.append(transportLeg.route()).append(" Direction ").append(transportLeg.destination());
        return(builder.toString());
    }
}
