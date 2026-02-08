package epfl.rechor.journey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import epfl.rechor.Json;
/**
 * used to convert a journey into a GeoJson String describing the steps
 * @author Guillaume Pommier (401065)
 */
public final class JourneyGeoJsonConverter {
    private JourneyGeoJsonConverter() {}

    /**
     * create a GeoJSON file representing a journey
     * @param journey the journey to be converted
     * @return a String representing the GeoJson journey
     */
    public static String toGeoJson(Journey journey) {
        List<Json> coordinatesList = new ArrayList<>();
        Stop firstStop = journey.legs().getFirst().depStop();

        Json.JArray coordonates = new Json.JArray(
                List.of(
                        new Json.JNumber(firstStop.longitude()),
                        new Json.JNumber(firstStop.latitude())
                ));
        Json.JArray nextCoordonates;

        coordinatesList.add(coordonates);
        for (Journey.Leg leg : journey.legs()) {
            for (Journey.Leg.IntermediateStop intermediateStop : leg.intermediateStops()) {
                nextCoordonates = new Json.JArray(
                        List.of(
                                new Json.JNumber(intermediateStop.stop().longitude()),
                                new Json.JNumber(intermediateStop.stop().latitude())
                        ));
                if (!coordonates.toString().equals(nextCoordonates.toString())) {
                    coordonates = nextCoordonates;
                    coordinatesList.add(coordonates);
                }
            }
            nextCoordonates = new Json.JArray(
                    List.of(
                            new Json.JNumber(leg.arrStop().longitude()),
                            new Json.JNumber(leg.arrStop().latitude())
                    ));

            if (!coordonates.toString().equals(nextCoordonates.toString())) {
                coordonates = nextCoordonates;
                coordinatesList.add(coordonates);
            }
        }
        return new Json.JObject(Map.of(
                new Json.JString("coordinates"),
                new Json.JArray(coordinatesList),
                new Json.JString("type"),
                new Json.JString("LineString")
        )).toString();
    }
}
