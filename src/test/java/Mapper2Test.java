import freesia.Fragment;
import freesia.Outcome;
import freesia.mapper.Mapper;
import freesia.temp.Mapper2;
import freesia.utils.FileScanner;
import freesia.utils.Utils;
import freesia.worker.DefaultWorkerOperation;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertEquals;

public class Mapper2Test {

    @Test
    public void test() throws IOException, ExecutionException, InterruptedException {
        List<String> inputData = FileScanner.scan("test_data.txt");
        Fragment fragment = new Fragment(inputData);
        Mapper2 mapper = Mapper2.create(new DefaultWorkerOperation());

        Outcome outcome = mapper.doWork(fragment, 5);

        checkStrings(inputData, outcome);

    }

    private void checkStrings(List<String> inputData, Outcome outcome) throws ExecutionException, InterruptedException {

        for (int i = 0; i < outcome.getData().size(); i++) {
            String actual = outcome.getData().get(i);
            String expected = inputData.get(i) + " " + Utils.computeHash(inputData.get(i));
            assertEquals(expected, actual);
        }
    }


}
