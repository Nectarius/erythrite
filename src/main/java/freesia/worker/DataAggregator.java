package freesia.worker;

import freesia.Outcome;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.toMap;

public interface DataAggregator {

    default Outcome aggregateData(List<Outcome> data) {
        if(data == null){
            return null;
        }
        List<String> collectedData = new ArrayList<>();

        data.forEach(outcome -> collectedData.addAll(outcome.getData()));

        return new Outcome(collectedData);
    }

}
