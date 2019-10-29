
package freesia.worker;

import freesia.Fragment;
import freesia.Outcome;
import freesia.mapper.Mapper;

import java.util.function.Consumer;

public class Worker extends Thread {
  private final int workerId;
  private Fragment receivedData;
  private WorkerOperation workerOperation;
  private Outcome outcome;
  private Consumer<Integer> consumer;

  public Worker(WorkerOperation workerOperation, Consumer<Integer> consumer, int id) {
    this.workerId = id;
    this.consumer = consumer;
    this.receivedData = null;
    this.workerOperation = workerOperation;
  }

  public Outcome getOutcome() {
    return outcome;
  }

  public int getWorkerId() {
    return this.workerId;
  }

  Fragment getReceivedData() {
    return this.receivedData;
  }

  public void setReceivedData(Mapper m, Fragment i) {
    //check if ready to receive..if yes:
    this.receivedData = i;
  }

  public void run() { //from Thread class
    outcome = workerOperation.execute(receivedData, workerId);
    consumer.accept(workerId);
  }
}
