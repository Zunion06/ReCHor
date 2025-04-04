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

/**
 * used to format values into a String text
 * @author Matthias Gibon (397827)
 */
public final class FormatterFr {
    /**
     * format a duration into a string
     * @param duration Duration to be formated (in seconds)
     * @return duration formated as a String ( ...h...min or ...min)
     */
    public static String formatDuration(Duration duration) {
        if (((int) duration.getSeconds()) / 3600 > 0 ) {
            long h=duration.toHours();
            return (h + " h " + duration.minusHours(h).toMinutes() + " min");
        }
        return (((int) duration.getSeconds() / 60) + " min");
    }

    /**
     * format a time into a string
     * @param dateTime date to be formated
     * @return datetime formated as a String (...h...min)
     * @throws NullPointerException if dateTime is null
     */
    public static String formatTime(LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime);
        DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.HOUR_OF_DAY)
                .appendLiteral('h')
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .toFormatter();
        fmt.format(dateTime);
        return (fmt.format(dateTime));
    }

    /**
     * format a platform into a string
     * @param stop platform to be formated
     * @return stop formated as a String
     * @throws NullPointerException if stop is null
     */
    public static String formatPlatformName(Stop stop) {
        Objects.requireNonNull(stop);
        StringBuilder builder = new StringBuilder(100);
        if(stop.platformName()!=null&&!stop.platformName().isEmpty()) {
            if (isDigit(stop.platformName().charAt(0))) {
                builder.append("voie ");
            } else if (isLetter(stop.platformName().charAt(0))) {
                builder.append("quai ");
            }
            builder.append(stop.platformName());
        }
        return(builder.toString());
    }

    /**
     * format a footLeg into a string
     * @param footLeg step of the journey by foot to be formated
     * @return footLeg formated as a string
     * @throws NullPointerException if footLeg is null
     */
    public static String formatLeg(Journey.Leg.Foot footLeg){
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

    /**
     * format a transport leg into a string
     * @param leg step of the journey by transports to be formated
     * @return leg formated as a string
     * @throws NullPointerException if leg is null
     */
    public static String formatLeg(Journey.Leg.Transport leg){
        Objects.requireNonNull(leg);
        StringBuilder builder = new StringBuilder(100);
        builder.append(formatTime(leg.depTime())).append(' ');
        builder.append(leg.depStop().name());
        if(leg.depStop().platformName()!=null && !leg.depStop().platformName().isEmpty()){
            builder.append(" (").append(formatPlatformName(leg.depStop())).append(')');
        }
        builder.append(" → ");
        builder.append(leg.arrStop().name()).append(" (arr. ");
        builder.append(formatTime(leg.arrTime()));
        if(leg.arrStop().platformName()!=null && !leg.arrStop().platformName().isEmpty()) {
            builder.append(' ').append(formatPlatformName(leg.arrStop()));
        }
        builder.append(')');

        return(builder.toString());
    }

    /**
     * format a transport leg's destination into a string
     * @param transportLeg step of the journey via transport to be formated
     * @return a String formating the direction of the step
     * @throws NullPointerException if transportLeg is null
     */
    public static String formatRouteDestination(Journey.Leg.Transport transportLeg){
        Objects.requireNonNull(transportLeg);
        StringBuilder builder = new StringBuilder();
        builder.append(transportLeg.route()).append(" Direction ").append(transportLeg.destination());
        return(builder.toString());
    }
}
