package dev.zunion.rechor.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.JourneyGeoJsonConverter;
import dev.zunion.rechor.service.JourneyService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@RestController
@Validated 
public class MapController {

    private final JourneyService journeyService;

    public MapController(JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    @GetMapping("/map") 
    public RedirectView toMap(
            @RequestParam
            @Size(min = 36, max = 36, message = "searchId must be a valid UUID string of 36 characters")
            String searchId,

            @RequestParam
            @Min(value = 0, message = "index must be at least 0")
            @Max(value = 50, message = "index cannot be greater than 50")
            int index
    ) {
        Journey journey = journeyService.getStoredJourneys(searchId).get(index);
        
        String baseUrl = "https://umap.osm.ch/fr/map/?data=";
        String geoJson = JourneyGeoJsonConverter.toGeoJson(journey);
        String encodedGeoJson = URLEncoder.encode(geoJson, StandardCharsets.UTF_8);
        String fullUrl = baseUrl + encodedGeoJson;

        return new RedirectView(fullUrl);
    }
}