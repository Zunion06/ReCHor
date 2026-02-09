package server.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import epfl.HelperProfile;
import static epfl.HelperProfile.testPath;
import epfl.rechor.journey.Journey;
import epfl.rechor.journey.Profile;
import epfl.rechor.journey.Router;
import epfl.rechor.timetable.TimeTable;
import epfl.rechor.timetable.mapped.FileTimeTable;
import server.controller.JourneysController.TimeType;

@ExtendWith(MockitoExtension.class)
class JourneyServiceTest {

    @Mock
    private Router router;

    private JourneyService journeyService;
    private Profile testProfile;
    private TimeTable timeTable;

    @BeforeEach
    void setUp() throws IOException {
        // 1. Load the real test timetable for name indexing
        timeTable = FileTimeTable.in(testPath("timetable"));
        List<String> stationNames = IntStream.range(0, timeTable.stations().size())
                .mapToObj(timeTable.stations()::name)
                .toList();

        // 2. Load the actual test profile using your provided logic
        testProfile = HelperProfile.profile();

        // 3. Initialize service
        journeyService = new JourneyService(router, stationNames);
    }

    @Test
    void getRecommendedJourneys_ShouldReturnCorrectNumberFromTestProfile() {
        // Arrange
        LocalDate date = LocalDate.of(2025, Month.MARCH, 18);
        LocalTime time = LocalTime.of(8, 0); // Reference time for filtering
        int arrId = HelperProfile.stationId(timeTable, "Lausanne");
        int depId = HelperProfile.stationId(timeTable, "Bern"); 
        
        // Mock the router to return our pre-loaded test profile
        when(router.profile(date, arrId)).thenReturn(testProfile);

        // for type=DEPARTURE
        List<Journey> depResults = journeyService.getRecommendedJourneys(
                "Bern", "Lausanne", date, time, TimeType.DEPARTURE, 3);

        assertThat(depResults).isNotNull();
        assertThat(depResults.size()).isLessThanOrEqualTo(3);
        assertThat(depResults);
        
        // Verify time filtering (no journeys before 08:00)
        for (Journey j : depResults) {
            assertThat(j.depTime().toLocalTime()).isAfterOrEqualTo(time);
        }

        // for type=ARRIVAL
        List<Journey> arrResults = journeyService.getRecommendedJourneys(
                "Bern", "Lausanne", date, time, TimeType.ARRIVAL, 3);

        assertThat(arrResults).isNotNull();
        assertThat(arrResults.size()).isLessThanOrEqualTo(3);
        
        // Verify time filtering (no journeys after 08:00)
        for (Journey j : arrResults) {
            assertThat(j.depTime().toLocalTime()).isBeforeOrEqualTo(time);
        }
    }

    /**
     * Test 404: Station Not Found
     * We use @ParameterizedTest to check both 'from' and 'to' in one go.
     */
    @ParameterizedTest
    @CsvSource({
        "UnknownStation, Lausanne",
        "Bern, UnknownStation"
    })
    void getJourneys_ShouldThrow404_WhenStationNameIsMissing(String from, String to) {
        assertThatThrownBy(() -> 
            journeyService.getRecommendedJourneys(from, to, LocalDate.now(), LocalTime.now(), TimeType.DEPARTURE, 10))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(ex -> {
                ResponseStatusException rse = (ResponseStatusException) ex;
                assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            });
    }

    /**
     * Test 400: Same Source and Destination
     */
    @Test
    void getJourneys_ShouldThrow400_WhenStationsAreIdentical() {
        assertThatThrownBy(() -> 
            journeyService.getRecommendedJourneys("Bern", "Bern", LocalDate.now(), LocalTime.now(), TimeType.DEPARTURE, 10))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(ex -> {
                ResponseStatusException rse = (ResponseStatusException) ex;
                assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            });
    }

    /**
     * Test 500: Unexpected Router Failure
     * This tests how the service handles its dependencies crashing.
     */
    @Test
    void getJourneys_ShouldThrow500_WhenRouterCrashes() {
        // Force the mock to throw an unexpected runtime exception
        when(router.profile(any(), anyInt())).thenThrow(new RuntimeException("I wanna crash"));

        assertThatThrownBy(() -> 
            journeyService.getRecommendedJourneys("Bern", "Lausanne", LocalDate.now(), LocalTime.now(), TimeType.DEPARTURE, 10))
            .isExactlyInstanceOf(RuntimeException.class)
            .hasMessage("I wanna crash");
    }    
}