package dev.zunion.rechor.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.annotation.JsonCreator;

import ch.epfl.rechor.journey.Journey;
import dev.zunion.rechor.GlobalExceptionHandler.ApiError;
import dev.zunion.rechor.service.JourneyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@Tag(name = "Journeys", description = "Search for recommended journeys between two stops")
public class JourneysController {
    private final JourneyService journeyService;

    private final boolean isMini = Boolean.parseBoolean(System.getenv("RECHOR_MINI")) 
                                || Boolean.getBoolean("RECHOR_MINI");

    private static final LocalDate MIN_DATE_FULL = LocalDate.of(2025, 3, 17);
    private static final LocalDate MIN_DATE_MINI = LocalDate.of(2025, 5, 30);
    private static final LocalDate MAX_DATE_FULL = LocalDate.of(2025, 6, 1);
    private static final LocalDate MAX_DATE_MINI = LocalDate.of(2025, 5, 31);
    
    private static final String DEFAULT_LIMIT = "10";

    @Schema(description = "Type of time constraint for the search")
    public enum TimeType {
        DEPARTURE,
        ARRIVAL;
    
        @JsonCreator
        public static TimeType from(String value) {
            return TimeType.valueOf(value.toUpperCase());
        }
    }

    @Schema(description = "Response returned after a journey search")
    public record JourneyResponse(

        @Schema(description = "Unique identifier for this search",
                example = "c2b7f5f2-8e4d-4cbe-a1d2-6e5a33b5c8d1")
        String searchId,

        @ArraySchema(
        arraySchema = @Schema(
            description = "List of journeys found",
            example = """
            [
                {
                "legs": [
                    {
                    "depStop": {
                        "name": "Ecublens VD, EPFL",
                        "platformName": "",
                        "longitude": 6.5661409590393305,
                        "latitude": 46.522195944562554
                    },
                    "depTime": "2025-05-30T13:02:00",
                    "arrStop": {
                        "name": "Lausanne, Flon",
                        "platformName": "",
                        "longitude": 6.630576923489571,
                        "latitude": 46.52056993916631
                    },
                    "arrTime": "2025-05-30T13:15:00",
                    "intermediateStops": [
                        {
                        "stop": {
                            "name": "Ecublens VD, UNIL-Sorge",
                            "platformName": "",
                            "longitude": 6.573578976094723,
                            "latitude": 46.52245092205703
                        },
                        "arrTime": "2025-05-30T13:03:00",
                        "depTime": "2025-05-30T13:03:00"
                        }
                    ],
                    "vehicle": "METRO",
                    "route": "m1",
                    "destination": "Lausanne, Flon"
                    }
                ]
                }
            ]
        """
        ),
        schema = @Schema(implementation=Object.class)
    )
        List<Journey> journeys
    ) {}


    public JourneysController(JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    @Operation(
        summary = "Search journeys",
        description = "Returns recommended journeys between two stops for a given date and time."
    )

    @ApiResponses(value = {

        @ApiResponse(
            responseCode = "200",
            description = "Journeys successfully retrieved",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = JourneyResponse.class)
            )
        ),

        @ApiResponse(
            responseCode = "400",
            description = "Bad request (invalid parameters or validation failure)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )
        ),

        @ApiResponse(
            responseCode = "404",
            description = "Endpoint not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )
        ),

        @ApiResponse(
            responseCode = "422",
            description = "Requested date is outside the available dataset",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )
        ),

        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )
        )
    })

    @GetMapping("/journeys")
        public JourneyResponse findJourneys(
            @Parameter(
                description = "Departure stop name",
                example = "Lausanne"
            )
            @RequestParam
            @Size(max = 30, message = "The departure stop's name must not exceed 30 characters")
            String from,

            @Parameter(
                description = "Arrival stop name",
                example = "Bern"
            )
            @RequestParam 
            @Size(max = 30, message = "The arrival stop's name must not exceed 30 characters")
            String to,

            @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
            LocalDate date,
            
            @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) 
            LocalTime time,
            
            @Parameter(
                description = "Defines if the provided time is a departure or arrival time",
                example = "DEPARTURE"
            )
            @RequestParam(defaultValue = "DEPARTURE") 
            TimeType type,
            
            @Parameter(
                description = "Maximum number of journeys returned",
                example = "10"
            )
            @RequestParam(defaultValue = DEFAULT_LIMIT)
            @Min(value = 1, message = "Limit must be at least 1")
            @Max(value = 50, message = "Limit cannot exceed 50") 
            int limit
    ) {
        LocalDate effectiveMax = isMini ? MAX_DATE_MINI : MAX_DATE_FULL;
        LocalDate effectiveMin = isMini ? MIN_DATE_MINI : MIN_DATE_FULL;

        if (date.isBefore(effectiveMin) || date.isAfter(effectiveMax)) {
            String message = isMini 
                ? "MINI MODE: Only dates between 2025-05-30 and 2025-05-31 are supported."
                : "No data available for the requested date: " + date;
                
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, message);
        }

        List<Journey> results = journeyService.getRecommendedJourneys(from, to, date, time, type, limit);
    
        String id = journeyService.storeResults(results);

        return new JourneyResponse(id, results);
    }
}