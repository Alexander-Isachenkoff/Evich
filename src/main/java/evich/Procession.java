package evich;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Procession
{
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
    
    private static String execPython(String file, String... args) throws IOException {
        String[] command = Arrays.copyOf(new String[]{ "py", file }, 2 + args.length);
        System.arraycopy(args, 0, command, 2, args.length);
        
        ProcessBuilder pb = new ProcessBuilder(command);
        
        System.out.println("Запускаю Python...");
        Process p = pb.start();
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        
        // BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        // System.out.println("Here is the standard error of the command (if any):\n");
        // String s;
        // while ((s = stdError.readLine()) != null) {
        //     System.out.println(s);
        // }
        
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
        
        // BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        //
        // System.out.println("Here is the standard error of the command (if any):\n");
        // String s;
        // while ((s = stdError.readLine()) != null) {
        //     System.out.println(s);
        // }
        
        System.out.println("Cчитываю результаты...");
        String line1 = stdInput.readLine();
        String line2 = stdInput.readLine();
        String line3 = stdInput.readLine();
        
        System.out.println("Преобразовываю результаты в числа...");
        return new StepsFiltrationResults(toMatrix(line1), toMatrix(line2), toMatrix(line3));
    }
    
    public static TrendsFiltrationResults filterTrends(double[][] YnLin) throws IOException {
        String arg = Arrays.deepToString(YnLin);
        ProcessBuilder pb = new ProcessBuilder("py", "python/trends_script.py", arg);
        
        System.out.println("Запускаю Python...");
        Process p = pb.start();
        
        
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
        return Arrays.stream(stringMatrix.split("[\\[\\]]")).filter(s -> !s.trim().isEmpty())
                .map(s -> Arrays.stream(s.split("[,\\s]")).filter(str -> !str.trim().isEmpty())
                        .mapToDouble(Double::parseDouble).toArray()).filter(values -> values.length > 0).collect(Collectors.toList()).toArray(new double[][]{});
    }
    
    public static class StepsFiltrationResults
    {
        public double[][] found_steps;
        public double[][] Z_no_lin;
        public double[][] YnLin;
        
        public StepsFiltrationResults(double[][] found_steps, double[][] z_no_lin, double[][] ynLin) {
            this.found_steps = found_steps;
            Z_no_lin = z_no_lin;
            YnLin = ynLin;
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
