package evich.model;

import java.util.HashMap;
import java.util.Map;

public class Experiment
{
    private final String name;
    private final Map<Stage, double[][]> dataRows = new HashMap<>();
    private LinearCoefficients[] linearCoefficients = new LinearCoefficients[0];
    
    public Experiment(String name) {
        this.name = name;
    }
    
    public void setLinearCoefficients(LinearCoefficients[] linearCoefficients) {
        this.linearCoefficients = linearCoefficients;
    }
    
    public LinearCoefficients[] getLinearCoefficients() {
        return linearCoefficients;
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
