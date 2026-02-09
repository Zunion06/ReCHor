package epfl.rechor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

import epfl.rechor.timetable.CachedTimeTable;
import epfl.rechor.timetable.StationAliases;
import epfl.rechor.timetable.Stations;
import epfl.rechor.timetable.TimeTable;
import epfl.rechor.timetable.mapped.FileTimeTable;

public class MyStopIndexTest {
    @Test
    void StopMatchingWorks(){
        assertDoesNotThrow(() -> {
//            List<String> list = List.copyOf(readAllLines(Path.of("timetable").resolve("strings.txt"),
//                    StandardCharsets.ISO_8859_1));
            Path timetablePath = Path.of("src/main/resources/timetable");
            TimeTable timeTable = new CachedTimeTable(
                    FileTimeTable.in(timetablePath));
            Stations stations = timeTable.stations();

            List<String> list = new ArrayList<>();
            for (int i = 0; i < stations.size(); i++) {
                list.add(stations.name(i));
            }
            StationAliases stationsAliases = timeTable.stationAliases();
            Map<String,String> map = new HashMap<>();
            for (int i = 0; i < stationsAliases.size(); i++) {
                map.put(stationsAliases.alias(i),stationsAliases.stationName(i));
            }

            List<String> list2 = List.of("Mézières FR, village",
                    "Mézières VD, village",
                    "Mézery-près-Donneloye, village",
                    "Charleville-Mézières");

            StopIndex stopIndex = new StopIndex(list,map);

            List<String> l = stopIndex.stopsMatching("Zurich wi",10);
            l.forEach(System.out::println);

        });

//        System.out.println(new MyBits32_24_8Test());

    }
}
