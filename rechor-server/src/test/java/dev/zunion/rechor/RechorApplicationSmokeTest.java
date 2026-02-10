package dev.zunion.rechor;


import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ch.epfl.rechor.timetable.TimeTable;

@SpringBootTest
class RechorApplicationSmokeTest {

    @Autowired
    private TimeTable timeTable;

    @Test
    void contextLoads() {
        // If this method is reached, it means all @Configuration 
        // classes (including TimeTableConfig) worked perfectly.
        assertThat(timeTable).isNotNull();
    }
}