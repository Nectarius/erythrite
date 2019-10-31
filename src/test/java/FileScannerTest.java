import freesia.utils.FileScanner;
import freesia.Fragment;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class FileScannerTest {

    @Test
    public void test() throws IOException {
        // given
        List<String> data = FileScanner.scan("test_data.txt");

        // when
        Fragment fragment = new Fragment(data);
        List<Fragment> fragments = fragment.divide(5);

        // then
        assertEquals(109, data.size());
        assertEquals(5, fragments.size());

    }
}
