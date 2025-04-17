package ch.epfl.rechor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;

public sealed interface Json permits Json.JArray, Json.JObject, Json.JString, Json.JNumber{
    public record JArray(List<Json> list) implements Json {
        @Override
        public String toString() {
            StringJoiner joiner = new StringJoiner(",","[","]");
            for (Json element : list)
                joiner.add(element.toString());
            return joiner.toString();
        }
    }

    public record JObject(Map<JString,Json> map) implements Json {
        @Override
        public String toString() {
            StringJoiner joiner = new StringJoiner(",","{","}");
            for (Json key : map.keySet()){
                String string = key.toString() + ":" + map.get(key).toString();
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
        private static final DecimalFormat df = new DecimalFormat(
                "0.0####", //format that gives between 1 and 5 decimals after the point and rounding the number
                new DecimalFormatSymbols(Locale.ENGLISH) //puts '.' between decimals instead of ','
        );
        @Override
        public String toString() {
            return df.format(number);
        }
    }
}
