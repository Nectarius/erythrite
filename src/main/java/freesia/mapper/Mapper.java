
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
import java.util.stream.Collectors;

public class Mapper {

    private final WorkerOperation workerOperation;
    private final DataAggregator dataAggregator;

    private AtomicReference<ArrayList<Worker>> workers = new AtomicReference<>();
    private int countOfFragments;
    private AtomicInteger count = new AtomicInteger(0);

    private CompletableFuture<Outcome> finalResult;

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
        this.countOfFragments = 0;
        this.finalResult = null;
    }

    public CompletableFuture<Outcome> getFinalResult() {
        return this.finalResult;
    }

    private ArrayList<Worker> createWorkers(int num) {

        ArrayList<Worker> ws = new ArrayList<Worker>(num);
        for (int i = 0; i < num; i++) {
            ws.add(new Worker(workerOperation, integer -> {
                notifyFromWorker();
            }, i + 1));
            //i+1 consider as id
        }

        return ws;
    }

    private void notifyFromWorker() {
        if (count.getAndIncrement() + 1 == countOfFragments) {
            //all data obtained
            List<Outcome> collectedData = workers.get().stream().map(Worker::getOutcome).collect(Collectors.toList());
            Outcome result = dataAggregator.aggregateData(collectedData);
            workers.set(null);
            count.set(0);
            finalResult.complete(result);
        }
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
            this.countOfFragments = dividedInput.size();
            for (int i = 0; i < this.countOfFragments; i++) {
                this.workers.get().get(i).setReceivedData(this, dividedInput.get(i));
                this.workers.get().get(i).start();
            }
        }
    }


}
