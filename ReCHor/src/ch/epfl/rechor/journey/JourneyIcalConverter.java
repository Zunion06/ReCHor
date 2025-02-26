package ch.epfl.rechor.journey;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.IcalBuilder;

import java.time.LocalDateTime;
import java.util.StringJoiner;
import java.util.UUID;

import static ch.epfl.rechor.IcalBuilder.Component.VCALENDAR;
import static ch.epfl.rechor.IcalBuilder.Component.VEVENT;
import static ch.epfl.rechor.IcalBuilder.Name.*;

public class JourneyIcalConverter {
    private JourneyIcalConverter(){}

    //doit être static?
    public static String toIcalendar(Journey journey){
        IcalBuilder builder = new IcalBuilder();

        StringJoiner joiner = new StringJoiner("\\n");
        for(int i=0;i<journey.legs().size();i++){
            Journey.Leg step=journey.legs().get(i);
            switch (step) {
                case Journey.Leg.Foot f ->
                        joiner.add(FormatterFr.formatLeg((Journey.Leg.Foot) step));
                case Journey.Leg.Transport t ->
                        joiner.add(FormatterFr.formatLeg((Journey.Leg.Transport) step));
            }
        }

        builder.begin(VCALENDAR)
                .add(VERSION,"2.0")
                .add(PRODID,"ReCHor")
                .begin(VEVENT)
                .add(UID, UUID.randomUUID().toString())
                .add(DTSTAMP, LocalDateTime.now())
                .add(DTSTART,journey.depTime())
                .add(DTEND,journey.arrTime())
                .add(SUMMARY, journey.depStop().name()+" → "+journey.arrStop().name())
                .add(DESCRIPTION, joiner.toString())
                .end()
                .end()
                ;

        return builder.build();
    }
}
