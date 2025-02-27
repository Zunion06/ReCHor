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
        //if(value.length()>=75-name.toString().length()){
        //            StringJoiner joiner = new StringJoiner("");
        //            joiner.add(value.substring(0,74-name.toString().length())+CRLF);
        //            value=value.substring(74-name.toString().length());
        //            for(int i=(74-name.toString().length()); value.length()>75;i+=74){
        //                joiner.add(value.substring(0,73)+CRLF);
        //                value=value.substring(73);
        //            }
        //            value=joiner.add(value).toString();
        //        }
        //        builder.append(name.toString()).append(":").append(value).append("\n");
        //        return this;

        if(value.length() < 75-name.toString().length()){
            builder.append(name.toString()).append(":").append(value).append("\n");
            return this;

        }

        builder.append(name.toString()).append(":").append(value.substring(0,74-name.toString().length())).append(CRLF);
        value=value.substring(74-name.toString().length());
        int characterIndex=74-name.toString().length();
        while(value.length()>74){
            builder.append(value.substring(0,74)).append(CRLF);
            value=value.substring(74);
        }
        builder.append(value).append("\n");

        return this;
    }

    public IcalBuilder add(Name name, LocalDateTime dateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
        builder.append(name.toString()).append(":").append(formatter.format(dateTime)).append("\n");
        return this;
    }

    public IcalBuilder begin(Component component){
        components.add(component);
        builder.append(Name.BEGIN).append(":").append(component.toString()).append("\n");
        return this;
    }

    public IcalBuilder end(){
        if(components.isEmpty()){throw new IllegalArgumentException();}
        builder.append(Name.END).append(":").append(components.getLast().toString()).append("\n");
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
