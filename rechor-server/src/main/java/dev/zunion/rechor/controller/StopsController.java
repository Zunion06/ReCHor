package dev.zunion.rechor.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.epfl.rechor.StopIndex;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@Validated // Required for @Min/@Max 
public class StopsController {

    private final StopIndex stopIndex;

    private static final String DEFAULT_LIMIT = "30";

    public StopsController(StopIndex stopIndex) {
        this.stopIndex = stopIndex;
    }

    @GetMapping("/stops")
    public List<String> stops(
            @RequestParam String query,
            @RequestParam(defaultValue = DEFAULT_LIMIT)
            @Min(value = 1, message = "Limit must be at least 1")
            @Max(value = 50, message = "Limit cannot exceed 50") int limit
    ) {
        return stopIndex.stopsMatching(query, limit);
    }
}