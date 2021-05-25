package evich;

import java.io.*;
import java.nio.file.Files;

public class IO
{
    public static double[] readDoubles(File file) throws IOException {
        return Files.readAllLines(file.toPath()).stream().mapToDouble(Double::parseDouble).toArray();
    }
    
    public static void writeObject(Object object, File file) throws IOException {
        FileOutputStream stream = new FileOutputStream(file);
        ObjectOutput objectWriter = new ObjectOutputStream(stream);
        objectWriter.writeObject(object);
    }
    
    public static Object readObject(File file) throws IOException, ClassNotFoundException {
        FileInputStream stream = new FileInputStream(file);
        ObjectInput objectReader = new ObjectInputStream(stream);
        return objectReader.readObject();
    }
}
