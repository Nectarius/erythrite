package freesia.worker;

import freesia.Fragment;
import freesia.Outcome;

public interface WorkerOperation {

    Outcome execute(Fragment receivedData, int workerId);

}
