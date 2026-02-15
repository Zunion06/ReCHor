package dev.zunion.rechor.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import ch.epfl.rechor.journey.Journey;
import ch.epfl.rechor.journey.JourneyExtractor;
import ch.epfl.rechor.journey.Profile;
import ch.epfl.rechor.journey.Router;
import dev.zunion.rechor.controller.JourneysController.TimeType;

@Service
public class JourneyService {

    private final Router router;
    private final List<String> nameList;

    // Cache record to serve as a composite key
    private record RouterKey(int arrId, LocalDate date) {}

    // Strategy: Cache up to 100 list of journeys, expire them after 15 mins of inactivity
    private final Cache<RouterKey, Profile> profileCache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterAccess(15, TimeUnit.MINUTES)
            .build();

    private final Cache<String, List<Journey>> resultStore = Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(30, TimeUnit.MINUTES)
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

        RouterKey key = new RouterKey(arrStationId, date);
        Profile profile = profileCache.get(key, k -> {
            return router.profile(date, arrStationId);
        });
        List<Journey> journeys = JourneyExtractor.journeys(profile, depStationId);

        LocalDateTime referenceDateTime = LocalDateTime.of(date, time);

        return switch (type) {
            case DEPARTURE -> journeys.stream()
                    .filter(j -> !j.depTime().isBefore(referenceDateTime))
                    .limit(limit)
                    .toList();

            case ARRIVAL -> journeys.stream()
                    .filter(j -> !j.arrTime().isAfter(referenceDateTime))
                    .sorted(Comparator.comparingLong(j ->
                            Math.abs(Duration.between(j.depTime(), referenceDateTime).toMillis())
                    ))
                    .limit(limit)
                    .toList();
        };

    }

    private int getStationIndex(String name) {
        int index = nameList.indexOf(name);
        if (index < 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Station not found: " + name);
        }
        return index;
    }

    public String storeResults(List<Journey> journeys) {
        String id = UUID.randomUUID().toString();
        resultStore.put(id, journeys);
        return id;
    }

    public List<Journey> getStoredJourneys(String searchId) {
        List<Journey> journeys = resultStore.getIfPresent(searchId);
        if (journeys == null) {
            throw new ResponseStatusException(HttpStatus.GONE, "Search session expired. Please search again.");
        }
        return journeys;
    }
}