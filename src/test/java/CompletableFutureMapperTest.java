import freesia.Fragment;
import freesia.Outcome;
import freesia.temp.CompletableFutureMapper;
import freesia.utils.FileScanner;
import freesia.utils.Utils;
import freesia.worker.DefaultWorkerOperation;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertEquals;

public class CompletableFutureMapperTest {

    @Test
    public void test() throws IOException, ExecutionException, InterruptedException {
        // given
        List<String> inputData = FileScanner.scan("test_data.txt");
        Fragment fragment = new Fragment(inputData);
        CompletableFutureMapper mapper = CompletableFutureMapper.create(new DefaultWorkerOperation());

        // when
        Outcome outcome = mapper.doWork(fragment, 5);

        // then
        checkStrings(inputData, outcome);
    }

    private void checkStrings(List<String> inputData, Outcome outcome) {
        assertEquals(inputData.size(), outcome.getData().size());
        for (int i = 0; i < outcome.getData().size(); i++) {
            String actual = outcome.getData().get(i);
            String expected = inputData.get(i) + " " + Utils.computeHash(inputData.get(i));
            assertEquals(expected, actual);
        }
    }


}
