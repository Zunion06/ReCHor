package dev.zunion.rechor.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.JourneyExtractor;
import ch.epfl.rechor.timetable.CachedTimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import dev.zunion.rechor.HelperProfile;
import static dev.zunion.rechor.HelperProfile.testPath;
import dev.zunion.rechor.service.JourneyService;

@WebMvcTest(MapController.class)
class MapControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JourneyService journeyService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public JourneyService journeyService() {
            return new JourneyService(null, null) {
                @Override
                public List<Journey> getStoredJourneys(String searchId) {
                    Journey dummyJourney = null;
                    try {
                        dummyJourney = JourneyExtractor.journeys(
                            HelperProfile.profile(),
                            HelperProfile.stationId(new CachedTimeTable(FileTimeTable.in(testPath("timetable"))), "Lausanne")
                        ).get(0);
                    } catch (IOException e) {}
                    return List.of(dummyJourney);
                }
            };
        }
    }

    @Test
    void toMap_ShouldReturn400_WhenIndexBelowMin() throws Exception {
        String validUUID = UUID.randomUUID().toString();

        mockMvc.perform(get("/map")
                .param("searchId", validUUID)
                .param("index", "-1"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("index must be at least 0")));
    }

    @Test
    void toMap_ShouldReturn400_WhenIndexAboveMax() throws Exception {
        String validUUID = UUID.randomUUID().toString();

        mockMvc.perform(get("/map")
                .param("searchId", validUUID)
                .param("index", "51"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("index cannot be greater than 50")));
    }

    @Test
    void toMap_ShouldReturn400_WhenSearchIdInvalidLength() throws Exception {
        String invalidUUID = "123"; // Too short

        mockMvc.perform(get("/map")
                .param("searchId", invalidUUID)
                .param("index", "0"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("searchId must be a valid UUID string of 36 characters")));
    }

    @Test
    void toMap_ShouldReturnRedirect_WhenValidParams() throws Exception {
        String validUUID = UUID.randomUUID().toString();

        mockMvc.perform(get("/map")
                .param("searchId", validUUID)
                .param("index", "0"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("https://umap.osm.ch/fr/map/?data=*"));
    }
}
