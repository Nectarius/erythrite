
package freesia.mapper;

import freesia.Fragment;
import freesia.Outcome;
import freesia.worker.DataAggregator;
import freesia.worker.Worker;
import freesia.worker.WorkerOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Mapper {
    // consider use size instead
    private AtomicReference<ArrayList<Worker>> workers = new AtomicReference<>();
    private int expectedCount;
    private Outcome finalResult;
    private WorkerOperation workerOperation;
    private DataAggregator dataAggregator;
    private AtomicInteger count = new AtomicInteger(0);

    public static Mapper create( WorkerOperation workerOperation) {
        return create(workerOperation, new DataAggregator() {
        });
    }

    public static Mapper create(WorkerOperation workerOperation, DataAggregator dataAggregator) {
        return new Mapper(workerOperation, dataAggregator);
    }

    private Mapper(WorkerOperation workerOperation, DataAggregator dataAggregator) {
        this.workerOperation = workerOperation;
        this.dataAggregator = dataAggregator;
        this.expectedCount = 0;
        this.finalResult = null;
    }

    public Outcome getFinalResult() {
        return this.finalResult;
    }

    int getExpectedCount() {
        return this.expectedCount;
    }

    AtomicReference<ArrayList<Worker>> getWorkers() {
        return this.workers;
    }

    private ArrayList<Worker> createWorkers(int num) {

        ArrayList<Worker> ws = new ArrayList<Worker>(num);
        for (int i = 0; i < num; i++) {
            ws.add(new Worker(this, workerOperation, i + 1));
            //i+1 consider as id
        }

        return ws;
    }

    public void doWork(Fragment input, int numOfWorkers) {
        List<Fragment> dividedInput = input.divide(numOfWorkers);

        if(!workers.compareAndSet(null, createWorkers(dividedInput.size())))
            throw new IllegalThreadStateException();

        divideWork(dividedInput);
    }

    private void divideWork(List<Fragment> dividedInput) {

        if (dividedInput != null) {
            this.expectedCount = dividedInput.size();
            for (int i = 0; i < this.expectedCount; i++) {
                this.workers.get().get(i).setReceivedData(this, dividedInput.get(i));
                this.workers.get().get(i).start();
                //this.workers.get(i).run();
            }
        }
    }

    public void notifyFromWorker() {
        collectResult();
    }

    private void collectResult() {
        if (count.getAndIncrement() +1   == this.expectedCount) {
            //all data obtained
            List<Outcome> collectedData = new ArrayList<>();
            for (Worker worker : workers.get()) {
                collectedData.add(worker.getOutcome());
            }
            this.finalResult = dataAggregator.aggregateData(collectedData);
            workers.set(null);
            count.set(0);
        }
    }

}
