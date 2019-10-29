
package freesia.mapper;

import freesia.Fragment;
import freesia.Outcome;
import freesia.worker.DataAggregator;
import freesia.worker.Worker;
import freesia.worker.WorkerOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Mapper {
  // consider use size instead
  private final int numOfWorkers;
  private final ArrayList<Worker> workers;
  private int expectedCount;
  private Map<Integer, Outcome> allOutcomeData;
  private Outcome finalResult;
  private WorkerOperation workerOperation;
  private DataAggregator dataAggregator;

  public static Mapper create(int numOfWorkers, WorkerOperation workerOperation){
    return create(numOfWorkers, workerOperation, new DataAggregator() {
    });
  }

  public static Mapper create(int numOfWorkers, WorkerOperation workerOperation, DataAggregator dataAggregator){
    return new Mapper(numOfWorkers, workerOperation, dataAggregator);
  }
  
  private Mapper(int numOfWorkers, WorkerOperation workerOperation, DataAggregator dataAggregator) {
    this.numOfWorkers = numOfWorkers;
    this.workerOperation = workerOperation;
    this.dataAggregator = dataAggregator;
    this.workers = setWorkers(numOfWorkers);
    this.expectedCount = 0;
    this.allOutcomeData = new ConcurrentHashMap<>(numOfWorkers);
    this.finalResult = null;
  }

   
  public Outcome getFinalResult() {
    return this.finalResult;
  }

  public Map<Integer, Outcome> getAllOutcomeData() {
    return this.allOutcomeData;
  }
  
  int getExpectedCount() {
    return this.expectedCount;
  }
  
  ArrayList<Worker> getWorkers() {
    return this.workers;
  }
  
  private ArrayList<Worker> setWorkers(int num){
    ArrayList<Worker> ws = new ArrayList<Worker>(num);
    for (int i = 0; i < num ; i++) {
      ws.add(new Worker(this, workerOperation, i + 1));
      //i+1 consider as id
    }
    return ws;
  }
  
  public void doWork(Fragment input) {
    divideWork(input);
  }
  
  private void divideWork(Fragment input) {
    List<Fragment> dividedInput = input.divide(numOfWorkers);
    if (dividedInput != null) {
      this.expectedCount = dividedInput.size();
      for (int i = 0; i < this.expectedCount; i++) {
        this.workers.get(i).setReceivedData(this, dividedInput.get(i));
        this.workers.get(i).start();
        //this.workers.get(i).run();
      }
    }
  }
  
  public void receiveData(Outcome data, Worker w) {
    collectResult(data, w.getWorkerId());
  }

  private void collectResult(Outcome data, int workerId) {
    this.allOutcomeData.put(workerId, data);
    if (this.allOutcomeData.size() == this.expectedCount) {
      //all data obtained
      List<Outcome> collectedData = new ArrayList<>();
      for (int i = 0; i < allOutcomeData.size(); i++) {
        collectedData.add(allOutcomeData.get(i + 1));
      }
      this.finalResult = dataAggregator.aggregateData(collectedData);
    }
  }

}
