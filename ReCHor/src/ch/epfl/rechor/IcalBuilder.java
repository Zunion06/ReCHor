package ch.epfl.rechor;

import java.time.LocalDateTime;

public final class IcalBuilder {
    StringBuilder builder = new StringBuilder();

    IcalBuilder add(Name name, String value){
        builder.append()
    }

    IcalBuilder add(Name name, LocalDateTime dateTime){

    }

    IcalBuilder begin(Component component){

    }

    IcalBuilder end(){

    }

    String build(){

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
