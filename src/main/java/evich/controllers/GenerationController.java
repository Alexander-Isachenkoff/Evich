package evich.controllers;

import evich.Procession;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class GenerationController implements Initializable
{
    private final List<BooleanProperty> rowsVisibleProperties = new ArrayList<>();
    @FXML
    private Spinner<Integer> generatorsSpinner;
    @FXML
    private Spinner<Integer> measurementsSpinner;
    @FXML
    private TextField ArField;
    @FXML
    private TextField SRmField;
    @FXML
    private VBox rowsBox;
    @FXML
    private VBox chartBox;
    private double[][] metrics = new double[0][];
    private ButtonType result = ButtonType.CANCEL;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        generatorsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,
                Integer.MAX_VALUE, 4));
        measurementsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,
                Integer.MAX_VALUE, 400));
    }
    
    @FXML
    private void onGenerate() {
        int count = measurementsSpinner.getValue();
        int n = generatorsSpinner.getValue();
        
        double[] Ar = Arrays.stream(ArField.getText().split(" ")).filter(s -> !s.isEmpty()).mapToDouble(Double::parseDouble).toArray();
        double[] SRm =
                Arrays.stream(SRmField.getText().split(" ")).filter(s -> !s.isEmpty()).mapToDouble(Double::parseDouble).toArray();
        if (Ar.length != n) {
            new Alert(Alert.AlertType.WARNING,
                    "Ar должно быть " + n + " (введено " + Ar.length + ")").showAndWait();
            return;
        }
        if (SRm.length != n) {
            new Alert(Alert.AlertType.WARNING,
                    "SRm должно быть " + n + " (введено " + SRm.length + ")").showAndWait();
            return;
        }
        
        try {
            metrics = Procession.generateMetrics(Ar, SRm, count, n);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        updateRowsBox(n);
        updateChartBox();
    }
    
    private void updateRowsBox(int n) {
        rowsBox.getChildren().clear();
        rowsVisibleProperties.clear();
        for (int i = 0; i < n; i++) {
            CheckBox checkBox = new CheckBox("Генератор " + (i + 1));
            SimpleBooleanProperty property = new SimpleBooleanProperty();
            property.addListener((observable, oldValue, newValue) -> updateChartBox());
            rowsVisibleProperties.add(i, property);
            checkBox.selectedProperty().bindBidirectional(property);
            checkBox.setSelected(true);
            rowsBox.getChildren().add(checkBox);
        }
    }
    
    private void updateChartBox() {
        chartBox.getChildren().clear();
        
        for (int i = 0; i < rowsVisibleProperties.size(); i++) {
            if (rowsVisibleProperties.get(i).get()) {
                LineChart<Number, Number> lineChart = new LineChart<>(new NumberAxis(), new NumberAxis());
                lineChart.setCreateSymbols(false);
                lineChart.setAnimated(false);
                chartBox.getChildren().add(lineChart);
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName("Генератор " + (i + 1));
                for (int j = 0; j < metrics[i].length; j++) {
                    series.getData().add(new XYChart.Data<>(j, metrics[i][j]));
                }
                lineChart.getData().add(series);
            }
        }
    }
    
    @FXML
    private void onOk() {
        result = ButtonType.OK;
        close();
    }
    
    @FXML
    private void onCancel() {
        close();
    }
    
    private void close() {
        ((Stage) chartBox.getScene().getWindow()).close();
    }
    
    public double[][] getMetrics() {
        return metrics;
    }
    
    public ButtonType getResult() {
        return result;
    }
}
