package dev.zunion.rechor.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.annotation.JsonCreator;

import ch.epfl.rechor.journey.Journey;
import dev.zunion.rechor.service.JourneyService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@Validated
public class JourneysController {

    private final JourneyService journeyService;

    private final boolean isMini = Boolean.parseBoolean(System.getenv("RECHOR_MINI")) 
                                || Boolean.getBoolean("RECHOR_MINI");

    private static final LocalDate MIN_DATE = LocalDate.of(2025, 3, 17);
    private static final LocalDate MAX_DATE_FULL = LocalDate.of(2025, 6, 1);
    private static final LocalDate MAX_DATE_MINI = LocalDate.of(2025, 3, 18);
    
    private static final String DEFAULT_LIMIT = "10";

    /**
     * Enum for time-based search types. 
     */
    public enum TimeType {
        DEPARTURE,
        ARRIVAL;
    
        @JsonCreator
        public static TimeType from(String value) {
            return TimeType.valueOf(value.toUpperCase());
        }
    }

    @ControllerAdvice
    public class EnumExceptionHandler {

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<String> handleEnumError(MethodArgumentTypeMismatchException e) {
            if (e.getRequiredType() == TimeType.class) {
                return ResponseEntity.badRequest()
                        .body("Invalid time type. Use DEPARTURE or ARRIVAL");
            }
            throw e;
        }
    }

    public JourneysController(JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    @GetMapping("/journeys")
    public List<Journey> findJourneys(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
            @RequestParam(defaultValue = "DEPARTURE") TimeType type,
            @RequestParam(defaultValue = DEFAULT_LIMIT)
            @Min(value = 1, message = "Limit must be at least 1")
            @Max(value = 50, message = "Limit cannot exceed 50") int limit
    ) {
        LocalDate effectiveMax = isMini ? MAX_DATE_MINI : MAX_DATE_FULL;

        if (date.isBefore(MIN_DATE) || date.isAfter(effectiveMax)) {
            String message = isMini 
                ? "MINI MODE: Only dates between 2025-03-17 and 2025-03-18 are supported."
                : "No data available for the requested date: " + date;
                
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, message);
        }

        return journeyService.getRecommendedJourneys(from, to, date, time, type, limit);
    }
}