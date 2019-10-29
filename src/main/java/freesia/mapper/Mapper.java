
package freesia.mapper;

import freesia.Fragment;
import freesia.Outcome;
import freesia.worker.DefaultWorkerOperation;
import freesia.worker.Worker;
import freesia.worker.WorkerOperation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.toMap;

public class Mapper {
  // consider use size instead
  private final int numOfWorkers;
  private final ArrayList<Worker> workers;
  private int expectedCountOfResults;
  private Map<Integer, Outcome> allResultData;
  private Outcome finalResult;
  private WorkerOperation workerOperation;
  
  public Mapper(int numOfWorkers, WorkerOperation workerOperation) {
    this.numOfWorkers = numOfWorkers;
    this.workerOperation = workerOperation;
    this.workers = setWorkers(numOfWorkers);
    this.expectedCountOfResults = 0;
    this.allResultData = new ConcurrentHashMap<>(numOfWorkers);
    this.finalResult = null;
  }
   
  public Outcome getFinalResult() {
    return this.finalResult;
  }

  public Map<Integer, Outcome> getAllResultData() {
    return this.allResultData;
  }
  
  int getExpectedCountOfResults() {
    return this.expectedCountOfResults;
  }
  
  ArrayList<Worker> getWorkers() {
    return this.workers;
  }
  
  private ArrayList<Worker> setWorkers(int num){
    ArrayList<Worker> ws = new ArrayList<Worker>(num);
    for (int i = 0; i < num ; i++) {
      ws.add(new Worker(this, workerOperation, i + 1));
      //i+1 will be id
    }
    return ws;
  }
  
  public void doWork(Fragment input) {
    divideWork(input);
  }
  
  private void divideWork(Fragment input) {
    List<Fragment> dividedInput = input.divide(numOfWorkers);
    if (dividedInput != null) {
      this.expectedCountOfResults = dividedInput.size();
      for (int i = 0; i < this.expectedCountOfResults; i++) {
        this.workers.get(i).setReceivedData(this, dividedInput.get(i));
        this.workers.get(i).start();
        //this.workers.get(i).run();
      }
    }
  }
  
  public void receiveData(Outcome data, Worker w) {
    //check if can receive..if yes:
    collectResult(data, w.getWorkerId());
  }
  
  private void collectResult(Outcome data, int workerId) {
    this.allResultData.put(workerId, data);
    if (this.allResultData.size() == this.expectedCountOfResults) {
      //all data received
      this.finalResult = aggregateData();
    }
  }

  public Outcome aggregateData(){

    // create as lambda

    List<String> collectedData = new ArrayList<>();

    LinkedHashMap<Integer, Outcome> sortedMap =
            allResultData.entrySet().stream().sorted(comparingByKey()).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

    sortedMap.forEach((integer, outcome) ->
            collectedData.addAll(outcome.getData()));

    return new Outcome(collectedData);
  }
}
