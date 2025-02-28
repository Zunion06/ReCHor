package ch.epfl.rechor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.StringJoiner;

public final class IcalBuilder {
    private final StringBuilder builder = new StringBuilder();
    private final ArrayList<Component> components = new ArrayList<Component>();
    private final String CRLF = "\r\n ";

    public IcalBuilder add(Name name, String value){

        //if the text is smaller than 75 characters
        if(value.length() < 75-name.toString().length()){
            builder.append(name.toString()).append(":").append(value).append("\r\n");
            return this;

        }

        builder.append(name.toString()).append(":").append(value.substring(0,74-name.toString().length())).append(CRLF);
        value=value.substring(74-name.toString().length());
        int characterIndex=74-name.toString().length();
        while(value.length()>74){
            builder.append(value.substring(0,74)).append(CRLF);
            value=value.substring(74);
        }
        builder.append(value).append("\r\n");

        return this;
    }

    public IcalBuilder add(Name name, LocalDateTime dateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
        builder.append(name.toString()).append(":").append(formatter.format(dateTime)).append("\r\n");
        return this;
    }

    public IcalBuilder begin(Component component){
        components.add(component);
        builder.append(Name.BEGIN).append(":").append(component.toString()).append("\r\n");
        return this;
    }

    public IcalBuilder end(){
        Preconditions.checkArgument(!components.isEmpty());
        builder.append(Name.END).append(":").append(components.getLast().toString()).append("\r\n");
        components.removeLast();
        return this;
    }

    public String build(){
        Preconditions.checkArgument(components.isEmpty());
        return builder.toString();
    }

    public enum Component{
        VCALENDAR,
        VEVENT
        ;
    }

    public enum Name{
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
        ;
    }
}
