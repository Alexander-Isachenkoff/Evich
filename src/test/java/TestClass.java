import evich.IO;
import evich.Procession;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

class TestClass
{
    private double[][] readZ() throws IOException {
        System.out.println("Начинаю считывать данные...");
        double[] z1 = IO.readDoubles(new File("z/z_1.txt"));
        double[] z2 = IO.readDoubles(new File("z/z_2.txt"));
        double[] z3 = IO.readDoubles(new File("z/z_3.txt"));
        double[] z4 = IO.readDoubles(new File("z/z_4.txt"));
        double[][] Z = new double[][]{ z1, z2, z3, z4 };
        System.out.println("Данные считаны");
        return Z;
    }
    
    @Test
    void testFiltration() throws IOException {
        double[][] Z = readZ();
        
        System.out.println("Фильтрую выбросы...");
        double[][] filteredNoise = Procession.filterNoise(Z);
        
        System.out.println("Выбросы отфильтрованы:");
        System.out.println(Arrays.deepToString(filteredNoise));
        
        System.out.println("Фильтрую скачки...");
        Procession.StepsFiltrationResults stepsFiltrationResults = Procession.filterSteps(filteredNoise, 3);
        System.out.println("Скачки отфильтрованы:");
        System.out.println(Arrays.deepToString(stepsFiltrationResults.found_steps));
        System.out.println(Arrays.deepToString(stepsFiltrationResults.YnLin));
        System.out.println(Arrays.deepToString(stepsFiltrationResults.Z_no_lin));
        
        System.out.println("Фильтрую тренды...");
        Procession.TrendsFiltrationResults trendsFiltrationResults = Procession.filterTrends(stepsFiltrationResults.YnLin);
        System.out.println("Тренды отфильтрованы:");
        System.out.println(Arrays.deepToString(trendsFiltrationResults.y1lin));
        System.out.println(Arrays.deepToString(trendsFiltrationResults.y2lin));
        System.out.println(Arrays.deepToString(trendsFiltrationResults.y_a_b));
        System.out.println(Arrays.deepToString(trendsFiltrationResults.Yres_lin));
    }
    
    @Test
    void testMetricsGeneration() {
        double[] Ar = new double[]{ 0.49919369, 0.9404525, 0.4547725086, 0.841137265 };
        double[] SRm = new double[]{ Math.sqrt(0.275164), Math.sqrt(0.32006153),
                Math.sqrt(0.561166371), Math.sqrt(0.25584871) };
        // 0.52456 0.565685 0.74911 0.505814
        int n = 4;
        int count = 400;
        
        try {
            double[][] metrics = Procession.generateMetrics(Ar, SRm, count, n);
            System.out.println(Arrays.deepToString(metrics));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    void testNoiseGeneration() {
        int n = 4;
        int count = 300;
        int scale = 10000;
        int per = 5;
        double[] SRm = new double[]{ Math.sqrt(0.275164), Math.sqrt(0.32006153),
                Math.sqrt(0.561166371), Math.sqrt(0.25584871) };
        
        try {
            double[][] noise = Procession.generateNoise(count, n, per, SRm[2] * scale);
            System.out.println(Arrays.deepToString(noise));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    void testStepsGeneration() {
        int n = 4;
        int count = 300;
        int[][] M_S = {
                { 290, 49, 159, 27 },
                { 0, 100, 0, 0 } };
        
        double[][] A_S = {
                { 0, -34.42789, -17.68, -29.2521991 },
                { 15, -19.42789, -0.68, -10.2521991 },
                { 0, -1.42789, 0, 0 } };
        
        try {
            double[][] steps = Procession.generateSteps(n, count, M_S, A_S);
            System.out.println(Arrays.deepToString(steps));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    void testTrendsGeneration() {
        int n = 4;
        int count = 300;
        double[][] RegC = {
                { -0.003740645, 0, -0.249283006, -0.0212369 },
                { 0.044859474, 0, -0.098291637, 0.061969925 },
                { 0, 0, 0, 0 } };
        
        try {
            double[][] trends = Procession.generateTrends(n, count, RegC);
            System.out.println(Arrays.deepToString(trends));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}