import freesia.utils.FileScanner;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class DivideElementsTest {

    @Test
    public void divideElements() throws IOException {
        // when
        List<String> data = FileScanner.scan("test_data.txt");

        // then
        assertEquals(109, data.size());
    }

}
