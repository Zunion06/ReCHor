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
@Tag(name = "Calendar", description = "Export journeys as iCalendar files")
public class CalendarController {

    private final JourneyService journeyService;

    public CalendarController(JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    @Operation(
        summary = "Download journey as ICS calendar file",
        description = "Returns an iCalendar (.ics) file representing a selected journey from a previously executed search."
    )

    @ApiResponses(value = {

        @ApiResponse(
            responseCode = "200",
            description = "ICS calendar file successfully generated",
            content = @Content(
                mediaType = "text/calendar",
                schema = @Schema(
                    example = """
                        BEGIN:VCALENDAR
                        VERSION:2.0
                        PRODID:ReCHor
                        BEGIN:VEVENT
                        UID:c2c68e8c-7c0a-4de3-8744-0e247c93a2d7
                        DTSTAMP:20260306T153949
                        DTSTART:20250530T131700
                        DTEND:20250530T142600
                        SUMMARY:Lausanne → Bern
                        DESCRIPTION:13h17 Lausanne (voie 1) → Bern (arr. 14h26 voie 3)
                        END:VEVENT
                        END:VCALENDAR
                    """
                )
            )
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