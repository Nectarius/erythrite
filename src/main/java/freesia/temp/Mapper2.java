package freesia.temp;

import freesia.Fragment;
import freesia.Outcome;
import freesia.mapper.Mapper;
import freesia.worker.DataAggregator;
import freesia.worker.WorkerOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Mapper2 {

    private final WorkerOperation workerOperation;
    private final DataAggregator dataAggregator;
    private AtomicInteger count = new AtomicInteger(0);

  //  private List<CompletableFuture> workers = new ArrayList<>();

    private CompletableFuture<Outcome> finalResult;

    public static Mapper2 create(WorkerOperation workerOperation) {
        return create(workerOperation, new DataAggregator() {
        });
    }

    public static Mapper2 create(WorkerOperation workerOperation, DataAggregator dataAggregator) {
        return new Mapper2(workerOperation, dataAggregator);
    }

    private Mapper2(WorkerOperation workerOperation, DataAggregator dataAggregator) {
        this.workerOperation = workerOperation;
        this.dataAggregator = dataAggregator;
        this.finalResult = null;
    }

    public Outcome doWork(Fragment input, int numOfWorkers) throws ExecutionException, InterruptedException {
        List<Fragment> dividedInput = input.divide(numOfWorkers);

        ExecutorService executor = Executors.newFixedThreadPool(numOfWorkers);

        List<CompletableFuture<Outcome>> workers = new ArrayList<>();

        for (Fragment fragment : dividedInput) {
                    CompletableFuture.supplyAsync(() -> workerOperation.execute(fragment, count.getAndIncrement()), executor);
        }

        CompletableFuture<Outcome> result = new CompletableFuture<>();

        //for (CompletableFuture<Outcome> worker : workers) {


        CompletableFuture<Void> voidCompletableFuture = CompletableFuture
                .allOf(workers.toArray(new CompletableFuture[workers.size()]));
        // }

        CompletableFuture<List<Outcome>> listCompletableFuture = voidCompletableFuture.thenApply(future -> {
            return workers.stream().map(CompletableFuture::join).collect(Collectors.toList());
        });

        List<Outcome> outcomes
                = listCompletableFuture.get();

        return dataAggregator.aggregateData(outcomes);



        // if (!workers.compareAndSet(null, createWorkers(dividedInreput.size())))
       //     throw new IllegalThreadStateException();

      //  divideWork(dividedInput);

    }

  /*  private void divideWork(List<Fragment> dividedInput) {

        if (dividedInput != null) {
            this.countOfFragments = dividedInput.size();
            for (int i = 0; i < this.countOfFragments; i++) {
                this.workers.get().get(i).setReceivedData(this, dividedInput.get(i));
                this.workers.get().get(i).start();
                //this.workers.get(i).run();
            }
        }
    }*/
}
