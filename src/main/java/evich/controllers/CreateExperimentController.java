package evich.controllers;

import evich.model.Experiment;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BinaryOperator;

public class CreateExperimentController implements Initializable
{
    private ButtonType result = ButtonType.CANCEL;
    public Button okButton;
    public ListView<Integer> yList;
    public ListView<Integer> zList;
    public LineChart<Number, Number> chart;
    private double[][] yRows = new double[0][];
    private double[][] zRows = new double[0][];
    private double[][] yWithSteps = new double[0][];
    private double[][] yWithTrends = new double[0][];
    private double[][] steps = null;
    private double[][] trends = null;
    private double[][] noise = null;
    
    @FXML
    private TextField nameField;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameField.textProperty().addListener(
                (observable, oldValue, newValue) -> okButton.setDisable(newValue.trim().isEmpty()));
        
        zList.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        displayData(zRows[newValue]);
                    }
                });
        
        yList.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        displayData(yRows[newValue]);
                    }
                });
    }
    
    private void displayData(double[] values) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (int i = 0; i < values.length; i++) {
            series.getData().add(new XYChart.Data<>(i, values[i]));
        }
        chart.getData().clear();
        chart.getData().add(series);
    }
    
    
    private void updateYList() {
        yList.getItems().clear();
        for (int i = 0; i < yRows.length; i++) {
            yList.getItems().add(i);
        }
    }
    
    private void updateZList() {
        zList.getItems().clear();
        for (int i = 0; i < zRows.length; i++) {
            zList.getItems().add(i);
        }
    }
    
    @FXML
    private void onLoadY() {
        double[][] dataRows = new double[0][];
        try {
            dataRows = loadDataRows();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (dataRows != null) {
            yRows = dataRows;
            yWithTrends = yRows;
            yWithSteps = yWithTrends;
            calcZ();
            updateYList();
            updateZList();
        }
    }
    
    private void calcZ() {
        zRows = new double[yWithSteps.length][];
        for (int i = 0; i < yWithSteps.length; i++) {
            zRows[i] = new double[yWithSteps[i].length];
            for (int j = 0; j < yWithSteps[i].length; j++) {
                zRows[i][j] = yWithSteps[0][j] - yWithSteps[i][j];
            }
        }
        if (noise != null) {
            zRows = calc(zRows, noise, Double::sum);
        }
    }
    
    @FXML
    private void onGenerateY() {
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/y_generation_scene.fxml"));
        Parent root;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        javafx.stage.Stage stage = new javafx.stage.Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(chart.getScene().getWindow());
        stage.setScene(new Scene(root));
        
        GenerationController controller = fxmlLoader.getController();
        stage.showAndWait();
        if (controller.getResult() == ButtonType.OK) {
            yRows = controller.getMetrics();
            yWithTrends = yRows;
            yWithSteps = yWithTrends;
            calcZ();
            updateYList();
            updateZList();
        }
    }
    
    @FXML
    private void onGenerateStaff() {
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/staff_scene.fxml"));
        Parent root;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        javafx.stage.Stage stage = new javafx.stage.Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(chart.getScene().getWindow());
        stage.setScene(new Scene(root));
        
        StaffController controller = fxmlLoader.getController();
        controller.setN(yRows.length);
        controller.setCount(yRows[0].length);
        stage.showAndWait();
        
        if (controller.getResult() == ButtonType.OK) {
            if (controller.isTrendsSelected()) {
                trends = controller.getTrends();
                yWithTrends = calc(yRows, trends, Double::sum);
            } else {
                trends = null;
            }
    
            if (controller.isStepsSelected()) {
                steps = controller.getSteps();
                yWithSteps = calc(yWithTrends, steps, Double::sum);
            } else {
                steps = null;
            }
    
            if (controller.isNoiseSelected()) {
                noise = controller.getNoise();
            } else {
                noise = null;
            }
            
            calcZ();
        }
    }
    
    private double[][] calc(double[][] m1, double[][] m2, BinaryOperator<Double> operator) {
        double[][] r = new double[m1.length][];
        for (int i = 0; i < m1.length; i++) {
            r[i] = new double[m1[i].length];
            for (int j = 0; j < m1[i].length; j++) {
                r[i][j] = operator.apply(m1[i][j], m2[i][j]);
            }
        }
        return r;
    }
    
    @FXML
    private void onLoadZ() {
        double[][] dataRows = new double[0][];
        try {
            dataRows = loadDataRows();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (dataRows != null) {
            yRows = new double[0][];
            zRows = dataRows;
            updateYList();
            updateZList();
        }
    }
    
    private double[][] loadDataRows() throws IOException {
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/data_loading_scene.fxml"));
        Parent root = fxmlLoader.load();
        javafx.stage.Stage stage = new javafx.stage.Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(chart.getScene().getWindow());
        stage.setScene(new Scene(root));
        
        DataLoadingController controller = fxmlLoader.getController();
        stage.showAndWait();
        
        return controller.getDataRows();
    }
    
    @FXML
    private void onClose() {
        ((Stage) nameField.getScene().getWindow()).close();
    }
    
    public Experiment getExperiment() {
        Experiment experiment = new Experiment(nameField.getText());
        experiment.getDataRows().put(evich.model.Stage.y0, yRows);
        experiment.getDataRows().put(evich.model.Stage.originalNoise, noise);
        experiment.getDataRows().put(evich.model.Stage.originalSteps, steps);
        experiment.getDataRows().put(evich.model.Stage.originalTrends, trends);
        experiment.getDataRows().put(evich.model.Stage.z, zRows);
        experiment.getDataRows().put(evich.model.Stage.y0withSteps, yWithSteps);
        experiment.getDataRows().put(evich.model.Stage.y0withTrends, yWithTrends);
        return experiment;
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
        ((Stage) chart.getScene().getWindow()).close();
    }
}
