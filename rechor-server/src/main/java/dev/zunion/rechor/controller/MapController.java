package dev.zunion.rechor.controller;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.JourneyGeoJsonConverter;
import dev.zunion.rechor.GlobalExceptionHandler.ApiError;
import dev.zunion.rechor.service.JourneyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@RestController
@Validated 
@Tag(name = "Map", description = "Visualize journeys on umap")
public class MapController {

    private final JourneyService journeyService;

    public MapController(JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    @Operation(
        summary = "View journey on map",
        description = "Redirects the client to an OpenStreetMap-based viewer displaying the selected journey route."
    )

    @ApiResponses(value = {

        @ApiResponse(
            responseCode = "302",
            description = "Redirect to the generated map view"
        ),

        @ApiResponse(
            responseCode = "400",
            description = "Bad request (invalid parameters or validation failure)",
            content = @Content(schema = @Schema(implementation = ApiError.class))
        ),

        @ApiResponse(
            responseCode = "404",
            description = "Search ID or journey not found",
            content = @Content(schema = @Schema(implementation = ApiError.class))
        ),

        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ApiError.class))
        )
    })

    @GetMapping("/map")
    public ResponseEntity<Void> toMap(
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

        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(fullUrl))
            .build();
    }
}