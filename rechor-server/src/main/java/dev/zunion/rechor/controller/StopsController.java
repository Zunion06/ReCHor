package dev.zunion.rechor.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.epfl.rechor.StopIndex;
import dev.zunion.rechor.GlobalExceptionHandler.ApiError;
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
@Tag(name = "Stops", description = "Search for transport stops")
public class StopsController {

    private final StopIndex stopIndex;

    private static final String DEFAULT_LIMIT = "30";

    public StopsController(StopIndex stopIndex) {
        this.stopIndex = stopIndex;
    }

    @Operation(
        summary = "Search stops",
        description = "Returns a list of stops whose names match the provided query string. Useful for autocomplete when searching for departure or arrival stops."
    )

    @ApiResponses(value = {

        @ApiResponse(
            responseCode = "200",
            description = "List of matching stops returned successfully",
            content = @Content(schema=@Schema(
                example = """
                [
                    "Aach (Hegau), Aachquelle",
                    "Aach (Hegau), Autohaus Gohm",
                    "Aach (Hegau), Rathaus",
                    "Aach-Linz, Bodenseestrasse",
                    "Aach-Linz, Löwen"
                ]
                """
            ))
        ),

        @ApiResponse(
            responseCode = "400",
            description = "Bad request (invalid parameters or validation failure)",
            content = @Content(schema = @Schema(implementation = ApiError.class))
        ),

        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ApiError.class))
        )
    })

    @GetMapping("/stops")
    public List<String> stops(
            @RequestParam(defaultValue = "")
            @Size(max = 30, message = "Query must not exceed 30 characters")
            String query,

            @RequestParam(defaultValue = DEFAULT_LIMIT)
            @Min(value = 1, message = "Limit must be at least 1")
            @Max(value = 50, message = "Limit cannot exceed 50") 
            int limit
    ) {
        return stopIndex.stopsMatching(query, limit);
    }
}