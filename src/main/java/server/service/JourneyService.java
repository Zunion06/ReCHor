package server.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import epfl.rechor.journey.Journey;
import epfl.rechor.journey.JourneyExtractor;
import epfl.rechor.journey.Profile;
import epfl.rechor.journey.Router;
import server.controller.JourneysController.TimeType;

@Service
public class JourneyService {

    private final Router router;
    private final List<String> nameList;

    // Cache record to serve as a composite key
    private record JourneyKey(int depId, int arrId, LocalDate date) {}

    // Strategy: Cache up to 100 list of journeys, expire them after 15 mins of inactivity
    private final Cache<JourneyKey, List<Journey>> journeyCache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterAccess(15, TimeUnit.MINUTES)
            .build();

    public JourneyService(Router router, List<String> nameList) {
        this.router = router;
        this.nameList = nameList;
    }

    public List<Journey> getRecommendedJourneys(String from, String to, LocalDate date, 
                                               LocalTime time, TimeType type, int limit) {
        
        int depStationId = getStationIndex(from);
        int arrStationId = getStationIndex(to);

        if (depStationId == arrStationId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stations must be different");
        }

        JourneyKey key = new JourneyKey(depStationId, arrStationId, date);
        List<Journey> journeys = journeyCache.get(key, k -> {
            Profile profile = router.profile(date, arrStationId);
            return JourneyExtractor.journeys(profile, depStationId);
        });

        LocalDateTime referenceDateTime = LocalDateTime.of(date, time);

        return switch (type) {
            case DEPARTURE -> journeys.stream()
                    .filter(j -> !j.depTime().isBefore(referenceDateTime))
                    .limit(limit)
                    .toList();
            case ARRIVAL -> journeys.stream()
                    .filter(j -> !j.arrTime().isAfter(referenceDateTime))
                    .limit(limit)
                    .toList();
        };
    }

    private int getStationIndex(String name) {
        int index = Collections.binarySearch(nameList, name);
        if (index < 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Station not found: " + name);
        }
        return index;
    }
}