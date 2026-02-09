package server.controller;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import epfl.rechor.StopIndex;

@WebMvcTest(StopsController.class)
class StopsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StopIndex stopIndex;

    // --- Happy Path ---

    @Test
    void stops_ShouldReturn200AndList_WhenRequestIsValid() throws Exception {
        // Arrange: Mock the service to return a sample list
        when(stopIndex.stopsMatching("Lau", 2))
            .thenReturn(List.of("Lausanne", "Lausanne-Flon"));

        // Act & Assert
        mockMvc.perform(get("/stops")
                .param("query", "Lau")
                .param("limit", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0]").value("Lausanne"));
    }

    // --- Validation Failures (The 400 Cases) ---

    @Test
    void stops_ShouldReturn400_WhenLimitIsZero() throws Exception {
        mockMvc.perform(get("/stops")
                .param("query", "Lau")
                .param("limit", "0")) // Violates @Min(1)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("Limit must be at least 1")));
    }

    @Test
    void stops_ShouldReturn400_WhenLimitIsTooHigh() throws Exception {
        mockMvc.perform(get("/stops")
                .param("query", "Lau")
                .param("limit", "51")) // Violates @Max(50)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("Limit cannot exceed 50")));
    }

    @Test
    void stops_ShouldReturn400_WhenQueryIsMissing() throws Exception {
        mockMvc.perform(get("/stops")
                .param("limit", "10")) // "query" is required by default
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("query")));
    }

    // --- Edge Cases ---

    @Test
    void stops_ShouldUseDefaultLimit_WhenLimitNotProvided() throws Exception {
        mockMvc.perform(get("/stops")
                .param("query", "Lau"))
            .andExpect(status().isOk());
        
        // Verify that the default "30" was passed to the service
        org.mockito.Mockito.verify(stopIndex).stopsMatching("Lau", 30);
    }
}