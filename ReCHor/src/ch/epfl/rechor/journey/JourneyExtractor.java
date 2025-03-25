package ch.epfl.rechor.journey;

import java.util.ArrayList;
import java.util.List;

public final class JourneyExtractor {
    private JourneyExtractor() {}

    public static List<Journey> journeys(Profile profile, int depStationId) {

        long[] pf = new long[];
        profile.forStation(depStationId).forEach();


        return List.of();
    }

}
