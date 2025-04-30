package ch.epfl.rechor;

import java.util.List;
import java.util.Map;
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

    public static void main(String[] args) {
        System.out.println("a : "+((int)'a'));
        System.out.println("z : "+((int)'z'));
        System.out.println("A : "+((int)'A'));
        System.out.println("Z : "+((int)'Z'));
        for (int i = 91; i < 97; i++) {
            System.out.println(String.valueOf((char) i)+ " : "+i);
        }
    }

    public List<String> stopsMatching(String query, int maxNumberName) {
        StringBuilder builder = new StringBuilder();

        boolean hasUpperCaseLetter = query.codePoints()
                .peek(c-> {
                    switch (c) {
                        case 'a' -> builder.append("[aáàâä]");
                        case 'e' -> builder.append("[eéèêë]");
                        case 'i' -> builder.append("[iíìîï]");
                        case 'o' -> builder.append("[oóòôö]");
                        case 'u' -> builder.append("[uúùûü]");
                        case 'c' -> builder.append("[cç]");
                        case ' ' -> builder.append(' ');
                        default -> builder.append(Pattern.quote(String.valueOf(c)));
                    }
                })
                .anyMatch(c -> 'A'<= c && c <='Z');
        int flag = Pattern.UNICODE_CASE | (hasUpperCaseLetter ? 0 : Pattern.CASE_INSENSITIVE);
        Pattern queryP = Pattern.compile(builder.toString(),flag);
        String[] queries = queryP.split(" ");

        List<String> possibleStops = new java.util.ArrayList<>(stopNames.stream()
                .filter(s -> {
                    for (String q : queries) {
                        if (Pattern.compile(q).matcher(s).find())
                            return true;
                    }
                    return false;
                })
                .toList())
                ;
        possibleStops.sort((a,b)-> {
            int aScore = scoreCalculator(a,queries);
            int bScore = scoreCalculator(b,queries);
            return aScore-bScore;
        });

        return possibleStops;
    }

    private static boolean isNotLetter(char c){
        return !(('a'<= c && c <= 'z') || ('A'<= c && c <= 'Z'));
    }

    private static int scoreCalculator(String stopName, String[] queries){
        int score = 0;
        int startIndex, endIndex;
        int tempScore;
        for (String q : queries)
            if (Pattern.compile(q).matcher(stopName).find()){
                tempScore = q.length() * 100 / stopName.length();
                startIndex = Pattern.compile(q).matcher(stopName).start();
                endIndex = Pattern.compile(q).matcher(stopName).end();
                if (startIndex == 0 || isNotLetter(stopName.charAt(startIndex - 1)))
                    tempScore *= 4;
                if (endIndex == stopName.length() - 1 || isNotLetter(stopName.charAt(endIndex + 1)))
                    tempScore *= 2;
                score += tempScore;
            }
        return score;
    }

}
