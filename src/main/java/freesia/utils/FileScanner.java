package freesia.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileScanner {

    public static List<String> scan(String path) throws IOException {

        List<String> lines = new ArrayList<>();

        InputStream inputStream = null;
        Scanner sc = null;
        try {
            inputStream = FileScanner.class
                    .getClassLoader().getResourceAsStream(path);

            sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                lines.add(line);
            }
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
        }
        return lines;
    }
}
