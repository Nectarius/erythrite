
package freesia.worker;

import freesia.Fragment;
import freesia.Outcome;
import freesia.mapper.Mapper;

public class Worker extends Thread {
  private final Mapper mapper;
  private final int workerId;
  private Fragment receivedData;
  private WorkerOperation workerOperation;
  private Outcome outcome;

  public Worker(Mapper mapper, WorkerOperation workerOperation, int id) {
    this.mapper = mapper;
    this.workerId = id;
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
    this.mapper.notifyFromWorker();
  }
}
