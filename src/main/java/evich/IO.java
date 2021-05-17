package evich;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class IO
{
    public static double[] readDoubles(File file) throws IOException {
        return Files.readAllLines(file.toPath()).stream().mapToDouble(Double::parseDouble).toArray();
    }
}
