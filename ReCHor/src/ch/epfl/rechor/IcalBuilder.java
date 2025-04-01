package ch.epfl.rechor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * used to create step by step the ICalendar format of a journey
 *
 * @author Matthias Gibon
 */
public final class IcalBuilder {
    private final StringBuilder builder = new StringBuilder();
    private final ArrayList<Component> components = new ArrayList<>();
    private final String CRLF = "\r\n ";

    /**
     * add a Name with a description at the end of the current ICalendar format of the journey (name:value)
     *
     * @param name  type of name to be added
     * @param value description of the name
     * @return the builder itself
     */
    public IcalBuilder add(Name name, String value) {

        //if the text is smaller than 75 characters
        if (value.length() < 75 - name.toString().length()) {
            builder.append(name).append(":").append(value).append("\r\n");
            return this;

        }

        builder.append(name).append(":").append(value, 0, 74 - name.toString().length()).append(CRLF);
        value = value.substring(74 - name.toString().length());
        while (value.length() > 74) {
            builder.append(value, 0, 74).append(CRLF);
            value = value.substring(74);
        }
        builder.append(value).append("\r\n");

        return this;
    }

    /**
     * add a name with a date at the end of the current ICalendar format of the journey (name:yyyyMMddTHHmmss)
     *
     * @param name     type of name to be added
     * @param dateTime date used as description
     * @return the builder itself
     */
    public IcalBuilder add(Name name, LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
        builder.append(name.toString()).append(":").append(formatter.format(dateTime)).append("\r\n");
        return this;
    }

    /**
     * begin a component at the end of the current ICalendar format of the journey (begin:component)
     *
     * @param component type of component to be added
     * @return the builder itself
     */
    public IcalBuilder begin(Component component) {
        components.add(component);
        builder.append(Name.BEGIN).append(":").append(component.toString()).append("\r\n");
        return this;
    }

    /**
     * end the last added component at the end of the current ICalendar format of the journey (end:...)
     *
     * @return the builder itself
     * @throws IllegalArgumentException if no available components can be ended
     */
    public IcalBuilder end() {
        Preconditions.checkArgument(!components.isEmpty());
        builder.append(Name.END).append(":").append(components.getLast().toString()).append("\r\n");
        components.removeLast();
        return this;
    }

    /**
     * convert the current ICalendar representation of the Journey into a string
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
