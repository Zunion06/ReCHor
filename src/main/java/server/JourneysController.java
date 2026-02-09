package server;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import epfl.rechor.journey.Journey;
import epfl.rechor.journey.JourneyExtractor;
import epfl.rechor.journey.Profile;
import epfl.rechor.journey.Router;

@RestController
public class JourneysController {

    private final Router router;
    private final List<String> nameList;

    private static final LocalDate MIN_DATE = LocalDate.of(2025, 3, 17);
    private static final LocalDate MAX_DATE = LocalDate.of(2025, 6, 1);

    public JourneysController(Router router, List<String> nameList) {
        this.router = router;
        this.nameList = nameList;
    }

    @GetMapping("/journeys")
    public List<Journey> findJourneys(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        if (date.isBefore(MIN_DATE) || date.isAfter(MAX_DATE)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "No data available for the requested date: " + date);
        }

        int depStationId = getStationIndex(from);
        int arrStationId = getStationIndex(to);

        Profile profile = router.profile(date, arrStationId);
        return JourneyExtractor.journeys(profile, depStationId);
    }

    private int getStationIndex(String stationName) throws ResponseStatusException {
        int index = Collections.binarySearch(nameList, stationName);
        // int index = nameList.indexOf(stationName);
        if (index < 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Station not found: " + stationName);
        }
        return index;
    }
}
