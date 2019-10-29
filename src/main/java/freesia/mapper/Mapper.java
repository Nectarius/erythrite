
package freesia.mapper;

import freesia.Fragment;
import freesia.Outcome;
import freesia.worker.DataAggregator;
import freesia.worker.Worker;
import freesia.worker.WorkerOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class Mapper {
    // consider use size instead
    private AtomicReference<ArrayList<Worker>> workers = new AtomicReference<>();
    private int expectedCount;
    private CompletableFuture<Outcome> finalResult;
    private final WorkerOperation workerOperation;
    private final DataAggregator dataAggregator;
    private AtomicInteger count = new AtomicInteger(0);

    public static Mapper create(WorkerOperation workerOperation) {
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

    public CompletableFuture<Outcome> getFinalResult() {
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
            ws.add(new Worker(workerOperation, new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) {
                    if (count.getAndIncrement() + 1 == expectedCount) {
                        //all data obtained
                        List<Outcome> collectedData = new ArrayList<>();
                        for (Worker worker : workers.get()) {
                            collectedData.add(worker.getOutcome());
                        }
                        Outcome result = dataAggregator.aggregateData(collectedData);
                        workers.set(null);
                        count.set(0);
                        finalResult.complete(result);
                    }
                }
            }, i + 1));
            //i+1 consider as id
        }

        return ws;
    }

    public CompletableFuture<Outcome> doWork(Fragment input, int numOfWorkers) {
        List<Fragment> dividedInput = input.divide(numOfWorkers);

        if (!workers.compareAndSet(null, createWorkers(dividedInput.size())))
            throw new IllegalThreadStateException();

        divideWork(dividedInput);
        this.finalResult = new CompletableFuture<>();
        return finalResult;
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


}
