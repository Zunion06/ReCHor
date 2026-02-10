package dev.zunion.rechor.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ch.epfl.rechor.StopIndex;
import ch.epfl.rechor.journey.Router;
import ch.epfl.rechor.timetable.TimeTable;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test") // Loads src/test/resources/application-test.properties
class TimeTableConfigTest {

    @Autowired
    private TimeTable timeTable;

    @Autowired
    private StopIndex stopIndex;

    @Test
    void context_ShouldLoadRequiredBeans() {
        // This replaces the contextRunner logic. 
        // If the test reaches this point, Spring successfully created the beans.
        assertThat(timeTable).isNotNull();
        assertThat(stopIndex).isNotNull();
        assertThat(timeTable.stations().size()).isGreaterThan(0);
    }

    @Test
    void verifyTestTimelineBoundaries() {
        Router router = new Router(timeTable);
        
        // 1. Verify March 18, 2025 works (The specific date in your test data)
        LocalDate testDate = LocalDate.of(2025, Month.MARCH, 18);
        
        // We use station ID 1 (or any valid index for your test set)
        assertThatCode(() -> {
            router.profile(testDate, 1);
        }).as("Router should handle the valid test date without crashing")
          .doesNotThrowAnyException();

        // 2. Verify a different date fails (Proves we aren't using production data)
        LocalDate wrongDate = LocalDate.of(2025, Month.DECEMBER, 25);
        
        assertThatThrownBy(() -> router.profile(wrongDate, 1));
    }
}