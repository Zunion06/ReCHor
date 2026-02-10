package dev.zunion.rechor.controller;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.epfl.rechor.StopIndex;
import ch.epfl.rechor.timetable.CachedTimeTable;
import ch.epfl.rechor.timetable.StationAliases;
import ch.epfl.rechor.timetable.Stations;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import static dev.zunion.rechor.HelperProfile.testPath;

@WebMvcTest(StopsController.class)
@ActiveProfiles("test")
class StopsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StopIndex stopIndex; // This will now be our "Fake" bean from below

    @TestConfiguration
    @SuppressWarnings("unused")
    static class StopIndexTestConfig {
        @Bean
        @Primary // Ensures this "Fake" is used instead of the real one in TimeTableConfig
        public StopIndex stopIndex() {
            Path timetablePath = testPath("timetable");
            TimeTable timeTable;
            try {
                timeTable = new CachedTimeTable(
                        FileTimeTable.in(timetablePath));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
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

            return new StopIndex(list,map);
        }
    }

    @Test
    void stops_ShouldReturn200AndList_WhenRequestIsValid() throws Exception {
        mockMvc.perform(get("/stops")
                .param("query", "Lau")
                .param("limit", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(10)))
            .andExpect(jsonPath("$", hasItem("Lausanne")));
    }

    @Test
    void stops_ShouldReturn400_WhenLimitIsZero() throws Exception {
        mockMvc.perform(get("/stops")
                .param("query", "Lau")
                .param("limit", "0"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("at least 1")));
    }

    @Test
    void stops_ShouldReturn400_WhenLimitIsTooHigh() throws Exception {
        mockMvc.perform(get("/stops")
                .param("query", "Lau")
                .param("limit", "51"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("cannot exceed 50")));
    }

    @Test
    void stops_ShouldReturn400_WhenQueryIsMissing() throws Exception {
        mockMvc.perform(get("/stops")
                .param("limit", "10"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("query")));
    }

    @Test
    void stops_ShouldUseDefaultLimit_WhenLimitNotProvided() throws Exception {
        mockMvc.perform(get("/stops")
                .param("query", "Lau"))
            .andExpect(status().isOk());
    }
}