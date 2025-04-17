package ch.epfl.rechor.journey;

import ch.epfl.rechor.Json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JourneyGeoJsonConverter {
    private JourneyGeoJsonConverter() {};

    public static Json toGeoJson(Journey journey) {
        Map<Json.JString,Json> objectMap = new HashMap<>();
        objectMap.put(
                new Json.JString("type"),
                new Json.JString("LineString")
        );
        List<Json> coordinatesList = new ArrayList<>();
        Stop firstStop = journey.legs().getFirst().depStop();
        coordinatesList.add(new Json.JArray(
                List.of(
                        new Json.JNumber(firstStop.longitude()),
                        new Json.JNumber(firstStop.latitude())
                )));
        for (Journey.Leg leg : journey.legs()) {
            for (Journey.Leg.IntermediateStop intermediateStop : leg.intermediateStops()) {
                coordinatesList.add(new Json.JArray(
                        List.of(
                                new Json.JNumber(intermediateStop.stop().longitude()),
                                new Json.JNumber(intermediateStop.stop().latitude())
                        )));
            }
            coordinatesList.add(new Json.JArray(
                    List.of(
                            new Json.JNumber(leg.arrStop().longitude()),
                            new Json.JNumber(leg.arrStop().latitude())
                    )));
        }
        objectMap.put(
                new Json.JString("coordinates"),
                new Json.JArray(coordinatesList)
        );
        return new Json.JObject(objectMap);
    }

}
