package ch.epfl.rechor.journey;

import ch.epfl.rechor.FormatterFr;
import ch.epfl.rechor.IcalBuilder;

import java.time.LocalDateTime;
import java.util.StringJoiner;
import java.util.UUID;

import static ch.epfl.rechor.IcalBuilder.Component.VCALENDAR;
import static ch.epfl.rechor.IcalBuilder.Component.VEVENT;
import static ch.epfl.rechor.IcalBuilder.Name.*;

/**
 * used to convert a journey into the ICalendar format
 * @author Matthias Gibon
 */
public class JourneyIcalConverter {
    private JourneyIcalConverter(){}

    /**
     * create a String following the ICalendar format of the journey
     * @param journey Journey that will be converted to the Icalendar form
     * @return a string containing the visual representation of the journey according to the Icalendar norms
     */
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
