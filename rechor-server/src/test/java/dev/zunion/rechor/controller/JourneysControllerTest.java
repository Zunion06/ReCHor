package dev.zunion.rechor.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;

import ch.epfl.rechor.journey.Journey;
import dev.zunion.rechor.controller.JourneysController;
import dev.zunion.rechor.controller.JourneysController.TimeType;
import dev.zunion.rechor.service.JourneyService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JourneysController.class) // Only loads the web layer
class JourneysControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JourneyService journeyService; // Inject the fake we define below

    @TestConfiguration
    static class JourneyTestConfig {
        @Bean
        @Primary
        public JourneyService journeyService() {
            // We create a subclass manually so Mockito doesn't have to "modify" the class
            return new JourneyService(null, null) {
                @Override
                public List<Journey> getRecommendedJourneys(String from, String to, 
                    LocalDate date, LocalTime time, TimeType type, int limit) {
                    return List.of(); // Return empty list for validation tests
                }
            };
        }
    }

    @Test
    void findJourneys_ShouldReturn400_WhenLimitIsBelowMin() throws Exception {
        mockMvc.perform(get("/journeys")
                .param("from", "Lausanne")
                .param("to", "Bern")
                .param("date", "2025-03-20")
                .param("time", "10:00")
                .param("type", "DEPARTURE")
                .param("limit", "0")) // Violates @Min(1)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value(containsString("must be at least 1")));
    }

    @Test
    void findJourneys_ShouldReturn400_WhenLimitIsAboveMax() throws Exception {
        mockMvc.perform(get("/journeys")
                .param("from", "Lausanne")
                .param("to", "Bern")
                .param("date", "2025-03-20")
                .param("time", "10:00")
                .param("type", "DEPARTURE")
                .param("limit", "100")) // Violates @Max(50)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value(containsString("cannot exceed 50")));
    }

    @Test
    void findJourneys_ShouldReturn422_WhenDateIsBeforeMin() throws Exception {
        mockMvc.perform(get("/journeys")
                .param("from", "Lausanne")
                .param("to", "Bern")
                .param("date", "2025-03-16") // One day before MIN_DATE
                .param("time", "10:00")
                .param("type", "DEPARTURE")
                .param("limit", "10"))
            .andExpect(status().isUnprocessableEntity()) // 422
            .andExpect(jsonPath("$.status").value(422))
            .andExpect(jsonPath("$.message").value(containsString("No data available")));
    }

    @Test
    void findJourneys_ShouldReturn422_WhenDateIsAfterMax() throws Exception {
        mockMvc.perform(get("/journeys")
                .param("from", "Lausanne")
                .param("to", "Bern")
                .param("date", "2025-06-02") // One day after MAX_DATE
                .param("time", "10:00")
                .param("type", "DEPARTURE")
                .param("limit", "10"))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.status").value(422))
            .andExpect(jsonPath("$.message").value(containsString("No data available")));
    }
}