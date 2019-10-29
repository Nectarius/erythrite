package freesia.worker;

import freesia.Fragment;
import freesia.Outcome;
import freesia.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class DefaultWorkerOperation implements WorkerOperation {

    @Override
    public Outcome execute(Fragment receivedData, int workerId) {

            System.out.println("worker id : " + workerId + " performing");
            List<String> data = receivedData.getData();
            List<String> outcome = new ArrayList<>();

            System.out.println("current thread : " + Thread.currentThread().getName());

            for (String datum : data) {
                outcome.add(datum + " " + Utils.computeHash(datum));
            }
            return new Outcome(outcome);
    }
}
