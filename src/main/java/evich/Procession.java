package evich;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class Procession
{
    private final static File argsFile = new File("args.txt");
    
    public static double[][] generateMetrics(double[] Ar, double[] SRm, int count, int n) throws IOException {
        return toMatrix(execPython(
                "python/metrics_generation_script.py",
                Arrays.toString(Ar),
                Arrays.toString(SRm),
                Integer.toString(count),
                Integer.toString(n)));
    }
    
    public static double[][] generateNoise(int count, int n, int per, double sigma) throws IOException {
        return toMatrix(execPython("python/noise_generation_script.py",
                Integer.toString(count),
                Integer.toString(n),
                Integer.toString(per),
                Double.toString(sigma)));
    }
    
    public static double[][] generateSteps(int n, int count, int[][] moments, double[][] ampls) throws IOException {
        return toMatrix(execPython("python/steps_generation_script.py",
                Integer.toString(n),
                Integer.toString(count),
                Arrays.deepToString(moments),
                Arrays.deepToString(ampls)));
    }
    
    public static double[][] generateTrends(int n, int count, double[][] RegC) throws IOException {
        return toMatrix(execPython(
                "python/trends_generation_script.py",
                Integer.toString(n),
                Integer.toString(count),
                Arrays.deepToString(RegC)));
    }
    
    public static double[][] filterNoise(double[][] values) throws IOException {
        return toMatrix(execPython("python/noise_filtration.py", Arrays.deepToString(values)));
    }
    
    public static double[] vars(double[][] data) throws IOException {
        try {
            Files.write(argsFile.toPath(), Collections.singleton(Arrays.deepToString(data))).toFile();
            return toArray(execPython("python/vars.py", argsFile.getAbsolutePath()));
        } finally {
            argsFile.delete();
        }

    }
    
    public static double[] means(double[][] data) throws IOException {
        try {
            Files.write(argsFile.toPath(), Collections.singleton(Arrays.deepToString(data))).toFile();
            return toArray(execPython("python/means.py", argsFile.getAbsolutePath()));
        }
        finally {
            argsFile.delete();
        }
    }
    
    private static String execPython(String file, String... args) throws IOException {
        String[] command = Arrays.copyOf(new String[]{ "py", file }, 2 + args.length);
        System.arraycopy(args, 0, command, 2, args.length);
        
        ProcessBuilder pb = new ProcessBuilder(command);
        
        System.out.println("Запускаю Python...");
        System.out.println(pb.command());
        Process p = pb.start();
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        
        //        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        //        System.out.println("Here is the standard error of the command (if any):\n");
        //        String s;
        //        while ((s = stdError.readLine()) != null) {
        //            System.out.println(s);
        //        }
        
        System.out.println("Cчитываю результаты...");
        
        return stdInput.readLine();
    }
    
    public static StepsFiltrationResults filterSteps(double[][] noise_filtered, int Ks) throws IOException {
        String arg = Arrays.deepToString(noise_filtered);
        ProcessBuilder pb = new ProcessBuilder("py", "python/steps_script.py", arg,
                Integer.toString(Ks));
        
        System.out.println("Запускаю Python...");
        Process p = pb.start();
        
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        
//         BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//
//         System.out.println("Here is the standard error of the command (if any):\n");
//         String s;
//         while ((s = stdError.readLine()) != null) {
//             System.out.println(s);
//         }
        
        System.out.println("Cчитываю результаты...");
        String line1 = stdInput.readLine();
        String line2 = stdInput.readLine();
        String line3 = stdInput.readLine();
        String line4 = stdInput.readLine();
        String line5 = stdInput.readLine();
        
        System.out.println("Преобразовываю результаты в числа...");
        return new StepsFiltrationResults(
                toMatrix(line1),
                toMatrix(line2),
                toMatrix(line3),
                toMatrix(line4),
                toMatrix(line5));
    }
    
    public static double[][] calcAbsError(double[][] y0, double[][] y) throws IOException {
        try {
            Files.write(argsFile.toPath(),
                    Arrays.asList(
                            Arrays.deepToString(y0),
                            Arrays.deepToString(y)))
                    .toFile();
            String s = execPython("python/abs_error_script.py", argsFile.getAbsolutePath());
            System.out.println("s = " + s);
            return toMatrix(s);
        } finally {
            argsFile.delete();
        }
    }
    
    public static TrendsFiltrationResults filterTrends(double[][] YnLin) throws IOException {
        String arg = Arrays.deepToString(YnLin);
        ProcessBuilder pb = new ProcessBuilder("py", "python/trends_script.py", arg);
        
        System.out.println("Запускаю Python...");
        Process p = pb.start();
        System.out.println("Длина команды:" + String.join("", pb.command()).length());
        
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        
        //        System.out.println("Here is the standard error of the command (if any):\n");
        //        String s;
        //        while ((s = stdError.readLine()) != null) {
        //            System.out.println(s);
        //        }
        System.out.println("Cчитываю результаты...");
        String line1 = stdInput.readLine();
        String line2 = stdInput.readLine();
        String line3 = stdInput.readLine();
        String line4 = stdInput.readLine();
        
        System.out.println("Преобразовываю результаты в числа...");
        return new TrendsFiltrationResults(toMatrix(line1), toMatrix(line2), toMatrix(line3), toMatrix(line4));
    }
    
    private static double[][] toMatrix(String stringMatrix) {
        return Arrays.stream(stringMatrix.split("[\\[\\]]"))
                .filter(s -> !s.trim().isEmpty())
                .map(s -> Arrays.stream(s.split("[,\\s]"))
                        .filter(str -> !str.trim().isEmpty())
                        .mapToDouble(Double::parseDouble)
                        .toArray())
                .filter(values -> values.length > 0).
                        collect(Collectors.toList())
                .toArray(new double[][]{});
    }
    
    private static double[] toArray(String string) {
        return Arrays.stream(string.split("[\\[\\],\\s]"))
                .filter(s -> !s.trim().isEmpty())
                .mapToDouble(Double::parseDouble)
                .toArray();
    }
    
    public static class StepsFiltrationResults
    {
        public double[][] found_steps;
        public double[][] Z_no_lin;
        public double[][] YnLin;
        public double[][] moments;
        public double[][] ampls;
    
        public StepsFiltrationResults(double[][] found_steps, double[][] z_no_lin, double[][] ynLin, double[][] moments, double[][] ampls) {
            this.found_steps = found_steps;
            this.Z_no_lin = z_no_lin;
            this.YnLin = ynLin;
            this.moments = moments;
            this.ampls = ampls;
        }
    }
    
    public static class TrendsFiltrationResults
    {
        public double[][] y1lin;
        public double[][] y2lin;
        public double[][] y_a_b;
        public double[][] Yres_lin;
        
        public TrendsFiltrationResults(double[][] y1lin, double[][] y2lin, double[][] y_a_b, double[][] yres_lin) {
            this.y1lin = y1lin;
            this.y2lin = y2lin;
            this.y_a_b = y_a_b;
            Yres_lin = yres_lin;
        }
    }
}
