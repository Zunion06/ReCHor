package dev.zunion.rechor.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.JourneyIcalConverter;
import dev.zunion.rechor.service.JourneyService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@RestController
@Validated // Required for @Min/@Max 
public class CalendarController {

    private final JourneyService journeyService;

    public CalendarController(JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    @GetMapping("/ics") 
    public ResponseEntity<String> getICS(
            @RequestParam
            @Size(min = 36, max = 36, message = "searchId must be a valid UUID string of 36 characters")
            String searchId,

            @RequestParam
            @Min(value = 0, message = "index must be at least 0")
            @Max(value = 50, message = "index cannot be greater than 50")
            int index
    ) {
        Journey journey = journeyService.getStoredJourneys(searchId).get(index);
        String icsContent = JourneyIcalConverter.toIcalendar(journey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/calendar"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=journey.ics");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(icsContent);
    }
}