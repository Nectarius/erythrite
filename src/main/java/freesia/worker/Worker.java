
package freesia.worker;

import freesia.Fragment;
import freesia.Outcome;
import freesia.memoryleak.MemoryLeakUtils;
import freesia.utils.Utils;
import freesia.mapper.Mapper;

import java.util.ArrayList;
import java.util.List;

public class Worker extends Thread {
  private final Mapper mapper;
  private final int workerId;
  private Fragment receivedData;
  private WorkerOperation workerOperation;

  public Worker(Mapper mapper, WorkerOperation workerOperation, int id) {
    this.mapper = mapper;
    this.workerId = id;
    this.receivedData = null;
    this.workerOperation = workerOperation;
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

  private void sendToMaster(Outcome data) {
    this.mapper.receiveData(data, this);
  } 

  public void run() { //from Thread class
    Outcome work = workerOperation.execute(receivedData, workerId);
    sendToMaster(work);
  }
}
