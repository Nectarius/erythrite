package freesia.worker;

import freesia.Outcome;

import java.util.List;
import java.util.stream.Collectors;

public interface DataAggregator {

    default Outcome aggregateData(List<Outcome> data) {

        List<String>collectedData = data.stream()
                .map(Outcome::getData)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        return new Outcome(collectedData);
    }

}
