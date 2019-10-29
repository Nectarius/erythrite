package freesia.worker;

import freesia.Fragment;
import freesia.Outcome;
import freesia.memoryleak.MemoryLeakUtils;
import freesia.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class WorkerOperationWithMemoryLeak implements WorkerOperation {

    @Override
    public Outcome execute(Fragment receivedData, int workerId) {

            System.out.println("worker id : " + workerId + " performing");
            List<String> data = receivedData.getData();
            List<String> outcome = new ArrayList<>();

            System.out.println("current thread : " + Thread.currentThread().getName());

             String str = data.toString();

            MemoryLeakUtils.putValueIntoProperties(str);

            for (String datum : data) {
                outcome.add(datum + " " + Utils.computeHash(datum));
            }
            return new Outcome(outcome);
    }
}
