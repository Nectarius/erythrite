package freesia.worker;

import freesia.Fragment;
import freesia.Outcome;
import freesia.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class DefaultWorkerOperation implements WorkerOperation {

    @Override
    public Outcome execute(Fragment receivedData, int workerId) {

            // create as lambda
            System.out.println("worker id : " + workerId + " performing");
            List<String> data = receivedData.getData();
            List<String> outcome = new ArrayList<>();

            //try {
            System.out.println("current thread : " + Thread.currentThread().getName());

            // String str = data.toString(); // read lengthy string any source db,textbox/jsp etc..
            // This will place the string in memory pool from which you can't remove
            // String intern = str.intern();

            //MemoryLeakUtils.putValueIntoProperties(str);

            // } catch (InterruptedException e) {
            // skip
            //  }

            for (String datum : data) {
                outcome.add(datum + " " + Utils.computeHash(datum));
                //System.out.println(datum + " hash : " + Utils.computeHash(datum));
            }
            return new Outcome(outcome);
    }
}
