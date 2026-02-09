package server.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import epfl.rechor.StopIndex;
import epfl.rechor.journey.Router;
import epfl.rechor.timetable.CachedTimeTable;
import epfl.rechor.timetable.StationAliases;
import epfl.rechor.timetable.Stations;
import epfl.rechor.timetable.TimeTable;
import epfl.rechor.timetable.mapped.FileTimeTable;

@Configuration
public class TimeTableConfig {

    @Value("${rechor.data.dir}") // Useful to separate the main and test timetables 
    private String dataPath;
    @Bean
    public TimeTable timeTable() throws IOException {
        return new CachedTimeTable(FileTimeTable.in(Path.of(dataPath)));
    }

    @Bean
    public Stations stations(TimeTable timeTable){
        return timeTable.stations();
    }
    
    @Bean
    public List<String> nameList(TimeTable timeTable, Stations stations){
        return IntStream.range(0, stations.size())
                .mapToObj(stations::name)
                .toList();
    }

    @Bean
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
    public Router router(TimeTable timeTable) {
        return new Router(timeTable);
    }
}
