import freesia.utils.FileScanner;
import freesia.Fragment;
import freesia.Outcome;
import freesia.utils.Utils;
import freesia.mapper.Mapper;
import freesia.worker.DefaultWorkerOperation;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class MapperTest {

    @Test
    public void test() throws IOException {
        List<String> inputData = FileScanner.scan("test_data.txt");
        Fragment fragment = new Fragment(inputData);
        Mapper mapper = new Mapper(5, new DefaultWorkerOperation());

        mapper.doWork(fragment);

        while (true) {
            if (mapper.getFinalResult() != null) {
                checkStrings(inputData, mapper);
                break;
            }
        }
    }

    private void checkStrings(List<String> inputData, Mapper mapper) {
        Outcome finalResult = mapper.getFinalResult();

        for (int i = 0; i < finalResult.getData().size(); i++) {
            String actual = finalResult.getData().get(i);
            String expected = inputData.get(i) + " " + Utils.computeHash(inputData.get(i));
            assertEquals(expected, actual);
        }
    }


}
