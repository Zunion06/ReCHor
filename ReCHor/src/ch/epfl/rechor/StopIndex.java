package ch.epfl.rechor;

import java.util.List;
import java.util.Map;

public final class StopIndex {
    private final List<String> stopNames;
    private final Map<String,String> alternativeNameMap;

    public StopIndex(List<String> stopNames, Map<String,String> alternativeNameMap) {
        this.stopNames=stopNames;
        this.alternativeNameMap=alternativeNameMap;
    }

    public List<String> stopsMatching(String query, int maxNumberName) {



        return null;
    }

}
