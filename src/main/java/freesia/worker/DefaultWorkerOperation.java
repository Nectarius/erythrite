package freesia.worker;

import freesia.Fragment;
import freesia.Outcome;
import freesia.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultWorkerOperation implements WorkerOperation {

    @Override
    public Outcome execute(Fragment receivedData, int workerId) {

            System.out.println("worker id : " + workerId + " performing");
            List<String> data = receivedData.getData();

            System.out.println("current thread : " + Thread.currentThread().getName());

            List<String> outcomes = data.stream().map(this::computeWithHash).collect(Collectors.toList());
            return new Outcome(outcomes);
    }

    private String computeWithHash(String datum) {
        return datum + " " + Utils.computeHash(datum);
    }
}
