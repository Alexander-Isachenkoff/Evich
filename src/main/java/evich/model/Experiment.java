package evich.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Experiment implements Serializable
{
    private static final long serialVersionUID = 4237212795388522471L;
    private final String name;
    private final Map<Stage, double[][]> dataRows = new HashMap<>();
    private LinearCoefficients[] linearCoefficients = new LinearCoefficients[0];
    private MeanVar[] meanVars = new MeanVar[0];
    private double[][] moments = new double[0][];
    private double[][] ampls = new double[0][];
    private double[][] absError = new double[0][];
    
    public Experiment(String name) {
        this.name = name;
    }
    
    public double[][] getAbsError() {
        return absError;
    }
    
    public void setAbsError(double[][] absError) {
        this.absError = absError;
    }
    
    public MeanVar[] getMeanVars() {
        return meanVars;
    }
    
    public void setMeanVars(MeanVar[] meanVars) {
        this.meanVars = meanVars;
    }
    
    public double[][] getMoments() {
        return moments;
    }
    
    public void setMoments(double[][] moments) {
        this.moments = moments;
    }
    
    public double[][] getAmpls() {
        return ampls;
    }
    
    public void setAmpls(double[][] ampls) {
        this.ampls = ampls;
    }
    
    public LinearCoefficients[] getLinearCoefficients() {
        return linearCoefficients;
    }
    
    public void setLinearCoefficients(LinearCoefficients[] linearCoefficients) {
        this.linearCoefficients = linearCoefficients;
    }
    
    public Map<Stage, double[][]> getDataRows() {
        return dataRows;
    }
    
    public double[][] getDataByStage(Stage stage) {
        return dataRows.get(stage);
    }
    
    public Map<Stage, double[]> getDataByGenerator(int i) {
        Map<Stage, double[]> dataByGenerator = new HashMap<>();
        for (Stage stage : dataRows.keySet()) {
            dataByGenerator.put(stage, dataRows.get(stage)[i]);
        }
        return dataByGenerator;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
}
