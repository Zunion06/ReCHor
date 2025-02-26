package ch.epfl.rechor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.StringJoiner;

public final class IcalBuilder {
    StringBuilder builder = new StringBuilder(75);
    ArrayList<Component> components = new ArrayList<Component>();
    String CRLF = "\r\n ";

    public IcalBuilder add(Name name, String value){
        if(value.length()>=74-name.toString().length()){
            StringJoiner joiner = new StringJoiner("");
            joiner.add(value.substring(0,72-name.toString().length())+CRLF);
            value=value.substring(72-name.toString().length());
            for(int i=(72-name.toString().length()); value.length()>75;i+=73){
                joiner.add(value.substring(0,73)+CRLF);
                value=value.substring(73);
            }
            value=joiner.add(value).toString();
        }
        builder.append(name.toString()).append(":").append(value).append("\n");
        return this;
    }

    public IcalBuilder add(Name name, LocalDateTime dateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
        builder.append(name.toString()).append(":").append(formatter.format(dateTime)).append("\n");
        return this;
    }

    public IcalBuilder begin(Component component){
        components.add(component);
        builder.append("BEGIN:").append(component.toString()).append("\n");
        return this;
    }

    public IcalBuilder end(){
        if(components.isEmpty()){throw new IllegalArgumentException();}
        builder.append("END:").append(components.getLast().toString()).append("\n");
        components.removeLast();
        return this;
    }

    public String build(){
        if(!components.isEmpty()){throw new IllegalArgumentException();}
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
