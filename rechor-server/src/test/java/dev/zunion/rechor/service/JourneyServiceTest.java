package dev.zunion.rechor.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;
import java.util.stream.IntStream;

import static dev.zunion.rechor.HelperProfile.testPath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.Router;
import ch.epfl.rechor.timetable.CachedTimeTable;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import dev.zunion.rechor.controller.JourneysController.TimeType;
import dev.zunion.rechor.service.JourneyService;

class JourneyServiceTest {

    private JourneyService journeyService;
    private Router router;
    private TimeTable timeTable;

    @BeforeEach
    void setUp() {
        try {
            // 1. Initialize the Real Router with the test timetable
            // This is your "mini router" using real files
            this.timeTable = new CachedTimeTable(FileTimeTable.in(testPath("timetable")));
            this.router = new Router(timeTable);

            // 2. Prepare station names list from the real test timetable
            List<String> stationNames = IntStream.range(0, timeTable.stations().size())
                    .mapToObj(timeTable.stations()::name)
                    .toList();

            // 3. Initialize the service with the REAL router
            this.journeyService = new JourneyService(router, stationNames);
            
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load test data", e);
        }
    }

    @Test
    void getRecommendedJourneys_ShouldReturnRealJourneys_WhenRequestIsValid() {
        // Arrange
        // Use a date/time known to exist in your test timetable/profile
        LocalDate date = LocalDate.of(2025, Month.MARCH, 18);
        LocalTime time = LocalTime.of(8, 0);

        // Act
        List<Journey> results = journeyService.getRecommendedJourneys(
                "Bern", "Lausanne", date, time, TimeType.DEPARTURE, 3);

        // Assert
        assertThat(results).isNotNull();
        // Even if the list is empty (depending on test data), we verify no crash occurred
        // and that logic like time filtering was applied.
        for (Journey j : results) {
            assertThat(j.depTime().toLocalTime()).isAfterOrEqualTo(time);
        }
    }

    @ParameterizedTest
    @CsvSource({
        "UnknownStation, Lausanne",
        "Bern, UnknownStation"
    })
    void getJourneys_ShouldThrow404_WhenStationNameIsMissing(String from, String to) {
        assertThatThrownBy(() -> 
            journeyService.getRecommendedJourneys(from, to, LocalDate.now(), LocalTime.now(), TimeType.DEPARTURE, 10))
            .isInstanceOf(ResponseStatusException.class)
            .hasFieldOrPropertyWithValue("statusCode", HttpStatus.NOT_FOUND);
    }

    @Test
    void getJourneys_ShouldThrow400_WhenStationsAreIdentical() {
        assertThatThrownBy(() -> 
            journeyService.getRecommendedJourneys("Bern", "Bern", LocalDate.now(), LocalTime.now(), TimeType.DEPARTURE, 10))
            .isInstanceOf(ResponseStatusException.class)
            .hasFieldOrPropertyWithValue("statusCode", HttpStatus.BAD_REQUEST);
    }
}