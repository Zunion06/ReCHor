package dev.zunion.rechor.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;
import java.util.stream.IntStream;

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
import static dev.zunion.rechor.HelperProfile.testPath;
import dev.zunion.rechor.controller.JourneysController.TimeType;

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
        LocalDate date = LocalDate.of(2025, Month.MARCH, 18);
        LocalTime time = LocalTime.of(8, 0);

        List<Journey> resultsDep = journeyService.getRecommendedJourneys(
                "Bern", "Lausanne", date, time, TimeType.DEPARTURE, 3);

        assertThat(resultsDep).isNotNull();
        for (Journey j : resultsDep) {
            assertThat(j.depTime().toLocalTime()).isAfterOrEqualTo(time);
        }

        List<Journey> resultsArr = journeyService.getRecommendedJourneys(
                "Bern", "Lausanne", date, time, TimeType.ARRIVAL, 3);
        assertThat(resultsArr).isNotNull();
        for (Journey j : resultsArr) {
            assertThat(j.arrTime().toLocalTime()).isBeforeOrEqualTo(time);
        }
    }

    @Test
    void getRecommendedJourneys_ShouldReturnJourneysClosestToTimeArg_WhenRequestIsValid() {
        LocalDate dateDep = LocalDate.of(2025, Month.MARCH, 18);
        LocalTime timeDep = LocalTime.of(8, 0);
        List<Journey> resultsDep = journeyService.getRecommendedJourneys(
                "Bern", "Lausanne", dateDep, timeDep, TimeType.DEPARTURE, 3);
        assertThat(resultsDep).isNotNull();

        LocalDateTime dateTimeArr = resultsDep.getLast().arrTime();
        LocalDate dateArr = dateTimeArr.toLocalDate();
        LocalTime timeArr = dateTimeArr.toLocalTime();
        List<Journey> resultsArr = journeyService.getRecommendedJourneys(
                "Bern", "Lausanne", dateArr, timeArr, TimeType.ARRIVAL, 3);
        assertThat(resultsArr).isNotNull();

        // With those conditions Set(resultsDep) == Set(resultsArr)
        // aka. the two list contains the same elements
        for (Journey j : resultsDep) {
            assertThat(resultsArr.contains(j));
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