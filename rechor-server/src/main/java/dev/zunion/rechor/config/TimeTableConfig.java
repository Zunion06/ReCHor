package dev.zunion.rechor.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.epfl.rechor.StopIndex;
import ch.epfl.rechor.journey.Router;
import ch.epfl.rechor.timetable.CachedTimeTable;
import ch.epfl.rechor.timetable.StationAliases;
import ch.epfl.rechor.timetable.Stations;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;

@Configuration
public class TimeTableConfig {

    @Value("${rechor.data.dir}") // Useful to separate the main and test timetables 
    private String dataPath;
    
    @Bean
    public TimeTable timeTable() throws IOException {
        return new CachedTimeTable(FileTimeTable.in(Path.of(dataPath + "/timetable")));
    }

    @Bean
    @org.springframework.context.annotation.Lazy
    public Stations stations(TimeTable timeTable){
        return timeTable.stations();
    }
    
    @Bean
    @org.springframework.context.annotation.Lazy
    public List<String> nameList(TimeTable timeTable, Stations stations){
        return IntStream.range(0, stations.size())
                .mapToObj(stations::name)
                .toList();
    }

    @Bean
    @org.springframework.context.annotation.Lazy
    public StopIndex stopIndex(TimeTable timeTable, List<String> nameList) {
        StationAliases stationAliases = timeTable.stationAliases();

        Map<String, String> alternativeNames = new HashMap<>();
        for (int i = 0; i < stationAliases.size(); i++) {
            alternativeNames.put(
                    stationAliases.alias(i),
                    stationAliases.stationName(i)
            );
        }

        return new StopIndex(nameList, alternativeNames);
    }

    @Bean
    @org.springframework.context.annotation.Lazy
    public Router router(TimeTable timeTable) {
        return new Router(timeTable);
    }
}
