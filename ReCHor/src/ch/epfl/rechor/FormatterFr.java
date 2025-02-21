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
            long h=duration.toHours();
            return (h + " h " + duration.minusHours(h).toMinutes() + " min");
        }
        return (((int) duration.getSeconds() / 60) + " min");
    }

    static String formatTime(LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime);
        DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.HOUR_OF_DAY)
                .appendLiteral('h')
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .toFormatter();
        fmt.format(dateTime);
        return (fmt.format(dateTime).toString());
    }

    static String formatPlatformName(Stop stop) {
        Objects.requireNonNull(stop);
        StringBuilder builder = new StringBuilder(100);
        if(stop.plateformName()!=null&&!stop.plateformName().isEmpty()) {
            if (isDigit(stop.plateformName().charAt(0))) {
                builder.append("voie ");
            } else if (isLetter(stop.plateformName().charAt(0))) {
                builder.append("quai ");
            }
            builder.append(stop.plateformName());
        }
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
        builder.append(")");
        return(builder.toString());
    }

    static String formatLeg(Journey.Leg.Transport leg){
        Objects.requireNonNull(leg);
        StringBuilder builder = new StringBuilder(100);
        builder.append(formatTime(leg.depTime())).append(" ");
        builder.append(leg.depStop().name());
        if(leg.depStop().plateformName()!=null){
            builder.append(" (").append(formatPlatformName(leg.depStop())).append(")");
        }
        builder.append(" → ");
        builder.append(leg.arrStop().name()).append(" (arr. ");
        builder.append(formatTime(leg.arrTime()));
        if(leg.arrStop().plateformName()!=null) {
            builder.append(" ").append(formatPlatformName(leg.arrStop()));
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
