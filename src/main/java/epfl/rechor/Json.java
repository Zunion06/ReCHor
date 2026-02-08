package epfl.rechor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;

/**
 * An interface use to indicate data which is formatted in JSON
 * @author Guillaume Pommier (401065)
 */
public sealed interface Json permits Json.JArray, Json.JObject, Json.JString, Json.JNumber {
    /**
     * Class describing an Array formatted in JSON
     */
    public record JArray(List<Json> list) implements Json {
        /**
         * Convert JArray into a String
         * @return a String representing an Array in JSON
         */
        @Override
        public String toString() {
            StringJoiner joiner = new StringJoiner(",", "[", "]");
            list.forEach(json -> joiner.add(json.toString()));
            return joiner.toString();
        }
    }

    /**
     * Class describing a Map formatted in JSON
     */
    public record JObject(Map<JString, Json> map) implements Json {
        /**
         * Convert JObject into a String
         * @return a String representing a map in JSON
         */
        @Override
        public String toString() {
            StringJoiner joiner = new StringJoiner(",", "{", "}");
            for (Json key : map.keySet()) {
                String string = key.toString() + ":" + map.get(key).toString();
                joiner.add(string);
            }
            return joiner.toString();
        }
    }

    /**
     * Class describing a String formatted in JSON
     */
    public record JString(String string) implements Json {
        /**
         * Convert JSting into a String
         * @return a String representing a String in JSON
         */
        @Override
        public String toString() {
            return "\"" + string + "\"";
        }
    }

    /**
     * Class describing a floating point Number formatted in JSON
     */
    public record JNumber(double number) implements Json {
        private static final DecimalFormat df = new DecimalFormat(
                "0.0####", //format that always gives 1 digit after the point and
                // at most 5 digits if the number requires it. Round off the number if it requires
                // more than 5 digits
                new DecimalFormatSymbols(Locale.ENGLISH) //puts a point '.' between decimals
                // instead of a coma ','
        );

        /**
         * Convert JNumber into a String
         * @return a String representing a floating point number in JSON
         */
        @Override
        public String toString() {
            return df.format(number);
        }
    }
}
