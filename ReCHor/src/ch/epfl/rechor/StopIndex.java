package ch.epfl.rechor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    public StopIndex(List<String> stopNames, Map<String,String> alternativeNameMap) {
        this.stopNames=stopNames;
        this.alternativeNameMap=alternativeNameMap;
    }

    public List<String> stopsMatching(String query, int maxNumberName) {
        if (query == null || query.isEmpty()) {
            return stopNames;
        }
        String[] subQueries = Pattern.compile(" ").split(query);

        int caseInsensitiveFlags = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;

        List<Pattern> queries = Arrays.stream(subQueries).map(
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

        return stopNames.stream()
                .filter(s -> {
                    for (Pattern q : queries)
                            if (!q.matcher(s).find())
                                return false;
                    return true;
                }).sorted((a,b)-> {
                    int aScore = scoreCalculator(a,queries,subQueries);
                    int bScore = scoreCalculator(b,queries,subQueries);
                    return bScore-aScore;
                })
                .limit(maxNumberName)
                .toList();
    }


    private static int scoreCalculator(String stopName, List<Pattern> queries, String[] subQueries){
        int score = 0;
        int startIndex, endIndex;
        int tempScore;
        Matcher matcher;
        Iterator<String> it = Arrays.stream(subQueries).iterator();
        for (Pattern queryPat : queries) {
            matcher = queryPat.matcher(stopName);
            if (matcher.find()) {
                String queryStr = it.next();
                tempScore = stopName.isEmpty() ?
                        0 :
                        queryStr.length() * 100 / stopName.length();
                startIndex = matcher.start();
                endIndex = matcher.end();
                if (startIndex == 0 || !Character.isLetter(stopName.charAt(startIndex - 1)))
                    tempScore *= 4;
                if (endIndex == stopName.length() || !Character.isLetter(stopName.charAt(endIndex)))
                    tempScore *= 2;
                score += tempScore;
            }
        }
//        System.out.println("name : "+stopName+"\tscore : "+score);
        return score;
    }

}
