package freesia.temp;

import freesia.Fragment;
import freesia.Outcome;
import freesia.worker.DataAggregator;
import freesia.worker.WorkerOperation;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CompletableFutureMapper {

    private final WorkerOperation workerOperation;
    private final DataAggregator dataAggregator;
    private AtomicInteger count = new AtomicInteger(0);

    public static CompletableFutureMapper create(WorkerOperation workerOperation) {
        return create(workerOperation, new DataAggregator() {
        });
    }

    public static CompletableFutureMapper create(WorkerOperation workerOperation, DataAggregator dataAggregator) {
        return new CompletableFutureMapper(workerOperation, dataAggregator);
    }

    private CompletableFutureMapper(WorkerOperation workerOperation, DataAggregator dataAggregator) {
        this.workerOperation = workerOperation;
        this.dataAggregator = dataAggregator;
    }

    public Outcome doWork(Fragment input, int numOfWorkers) throws ExecutionException, InterruptedException {
        List<Fragment> dividedInput = input.divide(numOfWorkers);

        ExecutorService executor = Executors.newFixedThreadPool(numOfWorkers);

        List<CompletableFuture<Outcome>> workers = dividedInput.stream()
                .map(fragment -> CompletableFuture.supplyAsync(() -> workerOperation.execute(fragment, count.getAndIncrement()), executor))
                .collect(Collectors.toList());

        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(workers.toArray(new CompletableFuture[0]));

        CompletableFuture<List<Outcome>> listCompletableFuture = voidCompletableFuture.thenApply(future -> workers.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList())
        );

        List<Outcome> outcomes = listCompletableFuture.get();

        return dataAggregator.aggregateData(outcomes);
    }
}
