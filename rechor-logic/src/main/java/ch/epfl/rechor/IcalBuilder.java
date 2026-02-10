package ch.epfl.rechor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import ch.epfl.rechor.Preconditions;

/**
 * used to create step by step the ICalendar format of a journey.
 *
 * @author Matthias Gibon (397827)
 */
public final class IcalBuilder {
    private final StringBuilder builder = new StringBuilder();
    private final ArrayList<Component> components = new ArrayList<>();
    private final String CRLF = "\r\n";
    private final String CRLF_SPACE = "\r\n ";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

    private final static int SUBSTRING_MAX_SIZE = 74;

    /**
     * add a Name with a description at the end of the current ICalendar
     * format of the journey (name:value).
     *
     * @param name  type of name to be added
     * @param value description of the name
     * @return the builder itself
     */
    public IcalBuilder add(Name name, String value) {

        //if the text is smaller than 75 characters
        if (value.length() <= SUBSTRING_MAX_SIZE - name.toString().length()) {
            builder.append(name).append(":").append(value).append(CRLF);
            return this;

        }

        builder.append(name)
                .append(":")
                .append(value, 0, SUBSTRING_MAX_SIZE - name.toString().length())
                .append(CRLF_SPACE);

        value = value.substring(SUBSTRING_MAX_SIZE - name.toString().length());

        while (value.length() > SUBSTRING_MAX_SIZE) {
            builder.append(value, 0, SUBSTRING_MAX_SIZE).append(CRLF_SPACE);
            value = value.substring(SUBSTRING_MAX_SIZE);
        }
        builder.append(value).append(CRLF);

        return this;
    }

    /**
     * add a name with a date at the end of the current ICalendar
     * format of the journey (name:yyyyMMddTHHmmss).
     *
     * @param name     type of name to be added
     * @param dateTime date used as description
     * @return the builder itself
     */
    public IcalBuilder add(Name name, LocalDateTime dateTime) {
        builder.append(name.toString())
                .append(":")
                .append(FORMATTER.format(dateTime))
                .append(CRLF);
        return this;
    }

    /**
     * begin a component at the end of the current ICalendar format of the journey (begin:component).
     *
     * @param component type of component to be added
     * @return the builder itself
     */
    public IcalBuilder begin(Component component) {
        components.add(component);
        builder.append(Name.BEGIN)
                .append(":")
                .append(component.toString())
                .append(CRLF);
        return this;
    }

    /**
     * end the last added component at the end of the current ICalendar format of the journey (end:...).
     *
     * @return the builder itself
     * @throws IllegalArgumentException if no available components can be ended
     */
    public IcalBuilder end() {
        Preconditions.checkArgument(!components.isEmpty());
        builder.append(Name.END)
                .append(":")
                .append(components.getLast().toString())
                .append(CRLF);
        components.removeLast();
        return this;
    }

    /**
     * convert the current ICalendar representation of the Journey into a string.
     *
     * @return a String following the ICalendar format representing the journey created
     * @throws IllegalArgumentException if every started components have not ended
     */
    public String build() {
        Preconditions.checkArgument(components.isEmpty());
        return builder.toString();
    }

    /**
     * Represents the possible components of an iCalendar file.
     */
    public enum Component {
        VCALENDAR,
        VEVENT
    }

    /**
     * Represents the possible components of an iCalendar file.
     */
    public enum Name {
        BEGIN,
        END,
        PRODID,
        VERSION,
        UID,
        DTSTAMP,
        DTSTART,
        DTEND,
        SUMMARY,
        DESCRIPTION
    }
}
