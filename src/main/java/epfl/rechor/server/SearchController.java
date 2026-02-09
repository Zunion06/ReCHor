package epfl.rechor.server;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import epfl.rechor.StopIndex;

@RestController
public class SearchController {

    private static final String MAX_NUMBER_OF_NAMES = "50";
    private final StopIndex stopIndex;

    public SearchController(StopIndex stopIndex) {
        this.stopIndex = stopIndex;
    }

    @GetMapping("/search")
    public List<String> search(
            @RequestParam(defaultValue = "")  String query,       // maps ?query=...
            @RequestParam(defaultValue = MAX_NUMBER_OF_NAMES) int limit // maps ?limit=...
    ) {
        return stopIndex.stopsMatching(query, limit);
    }
}
