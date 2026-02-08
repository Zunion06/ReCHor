package epfl.rechor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Guillaume Pommier (401065)
 */
public final class StopIndex {
    private static final Map<String, String> equivalentLetters = Map.of(
            "a", "[aâà]",
            "e", "[eéèêë]",
            "i", "[iíìîï]",
            "o", "[oóòôö]",
            "u", "[uúùûü]",
            "c", "[cç]");
    private final Pattern delimiters = Pattern.compile("[aeiouc]");
    private final List<String> stopNames;
    private final Map<String,String> alternativeNameMap;

    /**
     * Constructs a StopIndex with a list of main stop names and a mapping
     * of alternative names to their corresponding main names.
     *
     * @param stopNames the list of main stop names to index
     * @param alternativeNameMap a map from alternative names to main names
     */
    public StopIndex(List<String> stopNames, Map<String,String> alternativeNameMap) {
        this.stopNames= new ArrayList<>(stopNames);
        this.alternativeNameMap= new HashMap<>(alternativeNameMap);
    }

    /**
     * Finds the most relevant stop names for a given query
     * @param query the user input to match stop names against
     * @param maxNumberName the maximum number of matching names to return
     * @return a list of matched stop names, sorted by relevance
     */
    public List<String> stopsMatching(String query, int maxNumberName) {
        if (query == null || query.isEmpty()) {
            return stopNames.subList(0,maxNumberName);
        }
        String[] subQueries = Pattern.compile(" ").split(query);

        int caseInsensitiveFlags = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;

        List<Pattern> formattedSubQueries = Arrays.stream(subQueries).map(
                    subQuery -> Pattern.compile(
                            Arrays.stream(delimiters.splitWithDelimiters(subQuery, 0))
                                    .map(characters -> equivalentLetters.getOrDefault(
                                                characters,
                                                Pattern.quote(characters)
                                        )
                                    ).collect(Collectors.joining()),
                            subQuery.codePoints().anyMatch(Character::isUpperCase) ?
                                        0 : caseInsensitiveFlags
                    )).toList();

        return Stream.concat(
                stopNames.stream()
                        .map(stopName ->
                                new ScoredStopName(stopName,formattedSubQueries,
                                        subQueries,false))
                        .filter(match -> match.score!=0),
                alternativeNameMap.keySet().stream()
                        .map(stopName ->
                                new ScoredStopName(stopName,formattedSubQueries,
                                        subQueries,true))
                        .filter(match -> match.score!=0)
                )
                .sorted((a,b) -> b.score-a.score)
                .map(match -> match.stopName)
                .distinct()
                .limit(maxNumberName)
                .toList();
    }

    /**
     * Calculates a relevance score for a given stop name against the query
     * @param stopName the stopName which we will calculate the score
     * @param formattedSubQueries the list of formatted subqueries as pattern
     * @param subQueries the array of the unformatted subqueries
     * @return the score of a stop name given a query
     */
    private static int scoreCalculator(String stopName, List<Pattern> formattedSubQueries, String[] subQueries){
        int score = 0;
        int startIndex, endIndex;
        int tempScore;
        Matcher matcher;
        for (int i = 0 ; i < subQueries.length ; i++) {
            matcher = formattedSubQueries.get(i).matcher(stopName);
            if (matcher.find()) {
                tempScore = subQueries[i].length() * 100 / stopName.length();
                startIndex = matcher.start();
                endIndex = matcher.end();
                if (startIndex == 0 || !Character.isLetter(stopName.charAt(startIndex - 1)))
                    tempScore *= 4;
                if (endIndex == stopName.length() || !Character.isLetter(stopName.charAt(endIndex)))
                    tempScore *= 2;
                score += tempScore;
            }
            else
                return 0;
        }
        return score;
    }

    /**
     * Utilitarian class used to stock the score of a stop name and better sort the
     * @author Guillaume Pommier (401065)
     */
    private final class ScoredStopName {
        private final String stopName;
        private final int score;

        /**
         * Instantiate a ScoredStopName
         * @param stopName the stopName which we will calculate the score
         * @param formattedSubQueries the list of formatted subqueries as pattern
         * @param subQueries the array of the unformatted subqueries
         * @param isAlternativeName tells if the stop name should be considered an alternative name
         */
        private ScoredStopName(String stopName, List<Pattern> formattedSubQueries, String[] subQueries, boolean isAlternativeName) {
            this.stopName = isAlternativeName ? alternativeNameMap.get(stopName) : stopName;
            score = scoreCalculator(stopName,formattedSubQueries,subQueries);
        }
    }
}
