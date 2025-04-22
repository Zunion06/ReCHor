package ch.epfl.rechor;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class StopIndex {
    private final List<String> stopNames;
    private final Map<String,String> alternativeNameMap;

    public StopIndex(List<String> stopNames, Map<String,String> alternativeNameMap) {
        this.stopNames=stopNames;
        this.alternativeNameMap=alternativeNameMap;
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
        Stream<String> stream = queryP.splitAsStream(" ")
                ;


        return null;
    }

}
