package ch.epfl.rechor;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public sealed interface Json permits Json.JArray, Json.JObject, Json.JString, Json.JNumber{
    public record JArray(List<Json> list) implements Json {
        @Override
        public String toString() {
            StringJoiner joiner = new StringJoiner("[","]",",");
            for (Json element : list)
                joiner.add(element.toString());
            return joiner.toString();
        }
    }

    public record JObject(Map<JString,Json> map) implements Json {
        @Override
        public String toString() {
            StringJoiner joiner = new StringJoiner("{ "," }",", ");
            for (Json key : map.keySet()){
                String string = key.toString() + ": " + map.get(key).toString();
                joiner.add(string);
            }
            return joiner.toString();
        }
    }

    public record JString(String string) implements Json {
        @Override
        public String toString() {
            return "\"" + string + "\"";
        }
    }

    public record JNumber(double number) implements Json {
        @Override
        public String toString() {
            return String.valueOf(number);
        }
    }
}
