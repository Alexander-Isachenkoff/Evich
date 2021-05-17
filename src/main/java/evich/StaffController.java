package evich;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StaffController implements Initializable
{
    private final Function<String, Integer> intConverter = s -> {
        if (s.trim().isEmpty()) {
            return 0;
        } else {
            return Integer.parseInt(s);
        }
    };
    
    private final Function<String, Double> doubleConverter = s -> {
        if (s.trim().isEmpty()) {
            return 0.0;
        } else {
            return Double.parseDouble(s);
        }
    };
    
    private final TextFieldGrid<Integer> momentsGrid = new TextFieldGrid<>(intConverter);
    private final TextFieldGrid<Double> amplsGrid = new TextFieldGrid<>(doubleConverter);
    private final TextFieldGrid<Double> trendsGrid = new TextFieldGrid<>(doubleConverter);
    private final List<LineChart<Number, Number>> noiseCharts = new ArrayList<>();
    private final List<LineChart<Number, Number>> stepsCharts = new ArrayList<>();
    private final List<LineChart<Number, Number>> trendsCharts = new ArrayList<>();
    private ButtonType result = ButtonType.CANCEL;
    @FXML
    private TextField scaleField;
    @FXML
    private Pagination noisePagination;
    @FXML
    private Pagination stepsPagination;
    @FXML
    private Pagination trendsPagination;
    @FXML
    private CheckBox noiseCheckBox;
    @FXML
    private CheckBox stepsCheckBox;
    @FXML
    private CheckBox trendsCheckBox;
    @FXML
    private Spinner<Integer> noiseSpinner;
    @FXML
    private ScrollPane stepsScrollPane;
    @FXML
    private ScrollPane amplsScrollPane;
    @FXML
    private ScrollPane trendsScrollPane;
    private double[][] noise;
    private double[][] steps;
    private double[][] trends;
    private int n;
    private int count;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        noiseSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,
                Integer.MAX_VALUE));
        
        stepsScrollPane.setContent(momentsGrid);
        momentsGrid.setRowsCount(1);
        
        amplsScrollPane.setContent(amplsGrid);
        amplsGrid.setRowsCount(2);
        
        trendsScrollPane.setContent(trendsGrid);
        trendsGrid.setRowsCount(3);
    }
    
    public void setN(int n) {
        momentsGrid.setColumnsCount(n);
        amplsGrid.setColumnsCount(n);
        trendsGrid.setColumnsCount(n);
        this.n = n;
        
        updatePaginations();
    }
    
    private void updatePaginations() {
        noiseCharts.clear();
        stepsCharts.clear();
        trendsCharts.clear();
        
        for (int i = 0; i < n; i++) {
            String title = "Генератор " + (i+1);
            noiseCharts.add(createLineChart(title));
            stepsCharts.add(createLineChart(title));
            trendsCharts.add(createLineChart(title));
        }
        
        noisePagination.setPageFactory(noiseCharts::get);
        stepsPagination.setPageFactory(stepsCharts::get);
        trendsPagination.setPageFactory(trendsCharts::get);
        
        noisePagination.setPageCount(n);
        stepsPagination.setPageCount(n);
        trendsPagination.setPageCount(n);
    }
    
    private LineChart<Number, Number> createLineChart(String title) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setUpperBound(count);
        xAxis.setLabel("Номер такта");
        LineChart<Number, Number> chart = new LineChart<>(xAxis, new NumberAxis());
        chart.setTitle(title);
        chart.setCreateSymbols(false);
        chart.setAnimated(false);
        chart.setLegendVisible(false);
        
        return chart;
    }
    
    public void setCount(int count) {
        this.count = count;
        updatePaginations();
    }
    
    @FXML
    private void onAddSteps() {
        momentsGrid.incRows();
        amplsGrid.setRowsCount(momentsGrid.getRowsCount() + 1);
    }
    
    @FXML
    private void onRemoveSteps() {
        momentsGrid.decRows();
        amplsGrid.setRowsCount(momentsGrid.getRowsCount() + 1);
    }
    
    @FXML
    private void onGenerateNoise() {
        int per = noiseSpinner.getValue();
        int sigma = Integer.parseInt(scaleField.getText());
        try {
            noise = Procession.generateNoise(count, n, per, sigma);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        updateCharts(noiseCharts, noise);
        noiseCheckBox.setDisable(false);
    }
    
    @FXML
    private void onGenerateSteps() {
        //int[][] moments = toInteger(momentsGrid.getInfo());
        //Integer[][] moments = momentsGrid.getInfo1();
        int[][] moments = momentsGrid.getInfoAsList().stream().map(integers -> integers.stream().mapToInt(integer -> integer).toArray()).toArray(int[][]::new);
        System.out.println(Arrays.deepToString(moments));
        
        double[][] ampls =
                amplsGrid.getInfoAsList().stream().map(doubles -> doubles.stream().mapToDouble(d -> d).toArray()).toArray(double[][]::new);
        System.out.println(Arrays.deepToString(ampls));
        try {
            steps = Procession.generateSteps(n, count, moments, ampls);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        updateCharts(stepsCharts, steps);
        stepsCheckBox.setDisable(false);
    }
    
    @FXML
    private void onGenerateTrends() {
        double[][] RegC =
                trendsGrid.getInfoAsList().stream().map(doubles -> doubles.stream().mapToDouble(d -> d).toArray()).toArray(double[][]::new);
        try {
            trends = Procession.generateTrends(n, count, RegC);
            System.out.println(Arrays.deepToString(trends));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        updateCharts(trendsCharts, trends);
        trendsCheckBox.setDisable(false);
    }
    
    private void updateCharts(List<LineChart<Number, Number>> charts, double[][] data) {
        for (int i = 0; i < n; i++) {
            LineChart<Number, Number> chart = charts.get(i);
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            for (int j = 0; j < data[i].length; j++) {
                series.getData().add(new XYChart.Data<>(j, data[i][j]));
            }
            chart.getData().clear();
            chart.getData().add(series);
        }
    }
    
    @FXML
    private void onOk() {
        result = ButtonType.OK;
        close();
    }
    
    @FXML
    private void onCancel() {
        result = ButtonType.CANCEL;
        close();
    }
    
    public ButtonType getResult() {
        return result;
    }
    
    private void close() {
        ((Stage) trendsCheckBox.getScene().getWindow()).close();
    }
    
    public boolean isNoiseSelected() {
        return noiseCheckBox.isSelected();
    }
    
    public boolean isStepsSelected() {
        return stepsCheckBox.isSelected();
    }
    
    public boolean isTrendsSelected() {
        return trendsCheckBox.isSelected();
    }
    
    public double[][] getNoise() {
        return noise;
    }
    
    public double[][] getSteps() {
        return steps;
    }
    
    public double[][] getTrends() {
        return trends;
    }
}
