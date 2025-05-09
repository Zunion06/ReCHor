package ch.epfl.rechor;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static java.nio.file.Files.readAllLines;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class MyStopIndexTest {
    @Test
    void StopMatchingWorks(){
        assertDoesNotThrow(() -> {
            List<String> list = List.copyOf(readAllLines(Path.of("timetable").resolve("strings.txt"),
                    StandardCharsets.ISO_8859_1));

            List<String> list2 = List.of("Mézières FR, village",
                    "Mézières VD, village",
                    "Mézery-près-Donneloye, village",
                    "Charleville-Mézières");

            Map<String,String> map = Map.of("Lausanne" , "losane");

            StopIndex stopIndex = new StopIndex(list,map);

            List<String> l = stopIndex.stopsMatching("laus ol",10);
            l.forEach(System.out::println);

        });

//        System.out.println(new MyBits32_24_8Test());

    }
}
