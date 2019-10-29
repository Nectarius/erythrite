package freesia.worker;

import freesia.Outcome;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.toMap;

public interface DataAggregator {

    default Outcome aggregateData(Map<Integer, Outcome> allResultData) {
        List<String> collectedData = new ArrayList<>();

        LinkedHashMap<Integer, Outcome> sortedMap =
                allResultData.entrySet().stream().sorted(comparingByKey()).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

        sortedMap.forEach((integer, outcome) ->
                collectedData.addAll(outcome.getData()));

        return new Outcome(collectedData);
    }

}
