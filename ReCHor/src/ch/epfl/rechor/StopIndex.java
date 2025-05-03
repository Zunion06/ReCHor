package ch.epfl.rechor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StopIndex {
    private final List<String> stopNames;
    private final Map<String,String> alternativeNameMap;

    public StopIndex(List<String> stopNames, Map<String,String> alternativeNameMap) {
        this.stopNames=stopNames;
        this.alternativeNameMap=alternativeNameMap;
    }

    public List<String> stopsMatching(String query, int maxNumberName) {

        Pattern vowels = Pattern.compile("[aeuoi]");
        String[] subQueries = Pattern.compile(" ").split(query);

        int flags = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;

        List<Pattern> queries = Arrays.stream(subQueries).map(
                    s -> {
                        StringBuilder builder = new StringBuilder();
                        Arrays.stream(vowels.splitWithDelimiters(s, 0))
                                .forEach(c -> {
                                    switch (c) {
                                        case "a" -> builder.append("[aáàâä]");
                                        case "e" -> builder.append("[eéèêë]");
                                        case "i" -> builder.append("[iíìîï]");
                                        case "o" -> builder.append("[oóòôö]");
                                        case "u" -> builder.append("[uúùûü]");
                                        case "c" -> builder.append("[cç]");
                                        default -> builder.append(Pattern.quote(c));
                                    }});

//                        s.chars().forEach(c-> {
//                                    switch (c) {
//                                        case 'a' -> builder.append("[aáàâä]");
//                                        case 'e' -> builder.append("[eéèêë]");
//                                        case 'i' -> builder.append("[iíìîï]");
//                                        case 'o' -> builder.append("[oóòôö]");
//                                        case 'u' -> builder.append("[uúùûü]");
//                                        case 'c' -> builder.append("[cç]");
//                                        default -> builder.append(Pattern.quote(String.valueOf((char)c)));
//                                    }
//                                });
                        return Pattern.compile(builder.toString(),flags);
                    }).toList();

//        queries.forEach(System.out::println);


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
        Matcher matcher = null;
        int redondance = 1;
        Iterator<String> it = Arrays.stream(subQueries).iterator();
        for (Pattern queryPat : queries) {
            matcher = queryPat.matcher(stopName);
            if (matcher.find()) {
//                redondance++;
                String queryStr = it.next();
//                System.out.println("queryStr : "+queryStr);
//                System.out.println("queryPat : " + queryPat);
//                System.out.println("stopname : " + stopName);
                tempScore = queryStr.length() * 100 / stopName.length();
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
        return score*redondance;
    }

}
