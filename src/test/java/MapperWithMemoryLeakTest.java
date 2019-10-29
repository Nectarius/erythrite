import freesia.Fragment;
import freesia.Outcome;
import freesia.mapper.Mapper;
import freesia.utils.FileScanner;
import freesia.utils.Utils;
import freesia.worker.DefaultWorkerOperation;
import freesia.worker.WorkerOperationWithMemoryLeak;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class MapperWithMemoryLeakTest {

    @Test
    public void test() throws IOException {

        List<String> inputData = FileScanner.scan("test_data.txt");

        assertEquals(109, inputData.size());

        Fragment fragment = new Fragment(inputData);

        List<Fragment> fragments = fragment.divide(5);

        assertEquals(5, fragments.size());

        Mapper mapper = Mapper.create(new WorkerOperationWithMemoryLeak());

       // long start = System.currentTimeMillis();

        boolean started = false;
        while (true) {
            if (mapper.getFinalResult() == null && !started) {
                started = true;
                mapper.doWork(fragment, 5);
            } else if(mapper.getFinalResult() != null){
                mapper = Mapper.create(new DefaultWorkerOperation());
                started = false;
            }

        }
    }

    private void doWork(Mapper mapper, Fragment fragment){
        if (mapper.getFinalResult() != null) {
            mapper = Mapper.create(new DefaultWorkerOperation());
        } else {
            mapper.doWork(fragment, 5);
        }

    }




}
