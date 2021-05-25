package evich;

import evich.model.Experiment;
import evich.model.LinearCoefficients;
import evich.model.Stage;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class MainFormController implements Initializable
{
    private final ObservableList<evich.model.Experiment> experiments = FXCollections.observableArrayList();
    private final List<BooleanProperty> rowsVisibleProperties = new ArrayList<>();
    private final Map<BooleanProperty, Stage> stepsVisibleProperties = new HashMap<>();
    public ListView<evich.model.Experiment> experimentsList;
    public Button editButton;
    public TableView<LinearCoefficients> coeffsTable;
    public TableColumn<LinearCoefficients, String> generatorColumn;
    public TableColumn<LinearCoefficients, Number> aColumn;
    public TableColumn<LinearCoefficients, Number> bColumn;
    public Button saveButton;
    public Button loadButton;
    
    // region FXML
    @FXML
    private TextField ksTextField;
    @FXML
    private Button processButton;
    @FXML
    private VBox rowsBox;
    @FXML
    private VBox chartBox;
    @FXML
    private VBox stepsBox;
    @FXML
    private Button deleteButton;
    // endregion
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        experimentsList.setItems(experiments);
        experimentsList.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, experiment) -> {
                    deleteButton.setDisable(experiment == null);
                    editButton.setDisable(experiment == null);
                    saveButton.setDisable(experiment == null);
                    processButton.setDisable(experiment == null);
                    
                    updateStepsBox(experiment);
                    updateRowsBox(experiment);
                    updateCoefficientsTable(experiment);
                    updateChartBox();
                });
        
        generatorColumn.setCellValueFactory(data -> {
            int index = coeffsTable.getItems().indexOf(data.getValue());
            return new SimpleStringProperty("Генератор " + (index + 1));
        });
        
        aColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().a));
        bColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().b));
    }
    
    private void updateCoefficientsTable(Experiment experiment) {
        coeffsTable.getItems().clear();
        if (experiment == null) {
            return;
        }
        coeffsTable.setItems(FXCollections.observableArrayList(experiment.getLinearCoefficients()));
        //        if (experiment.getLinearCoefficients() != null) {
        //            for (LinearCoefficients c : experiment.getLinearCoefficients()) {
        //                coeffsTable.getItems().add(c);
        //            }
        //        }
    }
    
    @FXML
    private void onAddExperiment() {
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("../create_experiment_scene.fxml"));
        Parent root;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        javafx.stage.Stage stage = new javafx.stage.Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(experimentsList.getScene().getWindow());
        stage.setScene(new Scene(root));
        stage.showAndWait();
        
        CreateExperimentController controller = fxmlLoader.getController();
        if (controller.getResult() == ButtonType.OK) {
            Experiment experiment = controller.getExperiment();
            experiments.add(experiment);
        }
    }
    
    @FXML
    private void onDeleteExperiment() {
        Experiment selectedExperiment = getSelectedExperiment();
        if (selectedExperiment != null) {
            experiments.remove(selectedExperiment);
        }
    }
    
    @FXML
    private void onProcess() {
        Experiment experiment = getSelectedExperiment();
        
        double[][] filtered_noise;
        Procession.StepsFiltrationResults stepsFiltrationResults;
        Procession.TrendsFiltrationResults trendsFiltrationResults;
        try {
            int Ks = Integer.parseInt(ksTextField.getText());
            filtered_noise = Procession.filterNoise(experiment.getDataByStage(Stage.z));
            stepsFiltrationResults = Procession.filterSteps(filtered_noise, Ks);
            trendsFiltrationResults = Procession.filterTrends(stepsFiltrationResults.YnLin);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (NumberFormatException nfex) {
            new Alert(Alert.AlertType.WARNING, nfex.getMessage()).showAndWait();
            return;
        }
        
        experiment.getDataRows().put(Stage.noise_filtered, filtered_noise);
        experiment.getDataRows().put(Stage.found_steps, stepsFiltrationResults.found_steps);
        experiment.getDataRows().put(Stage.Z_no_lin, stepsFiltrationResults.Z_no_lin);
        experiment.getDataRows().put(Stage.YnLin, stepsFiltrationResults.YnLin);
        
        experiment.getDataRows().put(Stage.y1lin, trendsFiltrationResults.y1lin);
        experiment.getDataRows().put(Stage.y2lin, trendsFiltrationResults.y2lin);
        experiment.getDataRows().put(Stage.Yres_lin, trendsFiltrationResults.Yres_lin);
        
        LinearCoefficients[] linCoefficients =
                new LinearCoefficients[trendsFiltrationResults.y_a_b.length];
        for (int i = 0; i < trendsFiltrationResults.y_a_b.length; i++) {
            linCoefficients[i] = new LinearCoefficients(
                    trendsFiltrationResults.y_a_b[i][0],
                    trendsFiltrationResults.y_a_b[i][1]);
        }
        experiment.setLinearCoefficients(linCoefficients);
        
        updateStepsBox(experiment);
        updateRowsBox(experiment);
        updateCoefficientsTable(experiment);
    }
    
    private void updateStepsBox(Experiment experiment) {
        stepsBox.getChildren().clear();
        stepsVisibleProperties.clear();
        if (experiment == null) {
            return;
        }
        for (Stage stage : Stage.values()) {
            if (experiment.getDataRows().containsKey(stage)) {
                addNewStepProperty(stage);
            }
        }
    }
    
    private void addNewStepProperty(Stage stage) {
        BooleanProperty property = new SimpleBooleanProperty(null, stage.getName(), false);
        property.addListener((observable, oldValue, newValue) -> updateChartBox());
        CheckBox checkBox = new CheckBox(property.getName());
        checkBox.selectedProperty().bindBidirectional(property);
        stepsBox.getChildren().add(checkBox);
        stepsVisibleProperties.put(property, stage);
    }
    
    private void updateRowsBox(evich.model.Experiment experiment) {
        rowsBox.getChildren().clear();
        rowsVisibleProperties.clear();
        if (experiment == null) {
            return;
        }
        for (int i = 0; i < experiment.getDataByStage(Stage.z).length; i++) {
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
        Experiment experiment = getSelectedExperiment();
        if (experiment == null) {
            chartBox.getChildren().clear();
            return;
        }
        
        for (int i = 0; i < rowsVisibleProperties.size(); i++) {
            if (rowsVisibleProperties.get(i).get()) {
                NumberAxis xAxis = new NumberAxis();
                xAxis.setLabel("Номер такта");
                LineChart<Number, Number> lineChart = new LineChart<>(xAxis, new NumberAxis());
                VBox.setVgrow(lineChart, Priority.ALWAYS);
                
                lineChart.setTitle("Генератор " + (i + 1));
                lineChart.setCreateSymbols(false);
                lineChart.setAnimated(false);
                chartBox.getChildren().add(lineChart);
                
                int k = 0;
                for (BooleanProperty property : stepsVisibleProperties.keySet()) {
                    if (property.get()) {
                        Stage stage = stepsVisibleProperties.get(property);
                        
                        double[] data = experiment.getDataRows().get(stage)[i];
                        ObservableList<XYChart.Data<Number, Number>> dataList = FXCollections.observableArrayList();
                        for (int j = 0; j < data.length; j++) {
                            dataList.add(new XYChart.Data<>(j, data[j]));
                        }
                        
                        XYChart.Series<Number, Number> series = new XYChart.Series<>(stage.getName(), dataList);
                        lineChart.getData().add(series);
                        
                        String rgb = "rgb(" +
                                String.join(", ",
                                        String.valueOf(Math.round(stage.getColor().getRed() * 255)),
                                        String.valueOf(Math.round(stage.getColor().getGreen() * 255)),
                                        String.valueOf(Math.round(stage.getColor().getBlue()) * 255))
                                + ")";
                        
                        changeColor(lineChart, k++, rgb);
                    }
                }
            }
        }
    }
    
    private void changeColor(LineChart<?, ?> lineChart, int position, String color) {
        Node nl = lineChart.lookup(".default-color" + position + ".chart-series-line");
        //Node ns = lineChart.lookup(".default-color" + position + ".chart-line-symbol");
        Node nsl = lineChart.lookup(".default-color" + position + ".chart-legend-item-symbol");
        
        nl.setStyle("-fx-stroke: " + color + ";");
        //ns.setStyle("-fx-background-color: " + color + ", white;");
        if (nsl == null) {
            System.out.println(position);
            return;
        }
        nsl.setStyle("-fx-background-color: " + color + ", white;");
    }
    
    private Experiment getSelectedExperiment() {
        return experimentsList.getSelectionModel().getSelectedItem();
    }
    
    @FXML
    private void onSave() {
        File file = new FileChooser().showSaveDialog(experimentsList.getScene().getWindow());
        saveAsPng(chartBox, file.getAbsolutePath());
        try {
            Runtime.getRuntime().exec("cmd /c start " + file.getAbsolutePath() + ".png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void onSaveExperiment() {
        Experiment experiment = getSelectedExperiment();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("").getAbsoluteFile());
        File file = fileChooser.showSaveDialog(getWindow());
        if (file != null) {
            try {
                IO.writeObject(experiment, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void onLoadExperiment() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("").getAbsoluteFile());
        File file = fileChooser.showOpenDialog(getWindow());
        try {
            Experiment experiment = (Experiment) IO.readObject(file);
            experiments.add(experiment);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private Window getWindow() {
        return chartBox.getScene().getWindow();
    }
    
    public void saveAsPng(Node node, String fileName) {
        saveAsPng(node, fileName, new SnapshotParameters());
    }
    
    public void saveAsPng(Node node, String fileName, SnapshotParameters ssp) {
        WritableImage image = node.snapshot(ssp, null);
        File file = new File(fileName + ".png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void onExport() {
        Experiment experiment = getSelectedExperiment();
        
        int cols =
                (int) rowsVisibleProperties.stream().map(p -> p.get()).count() *
                        (int) stepsVisibleProperties.keySet().stream().map(p -> p.get()).count();
        int rows = experiment.getDataByStage(Stage.z)[0].length;
        
        List<List<String>> reportTable = new ArrayList<>(rows);
        for (int i = 0; i < rows; i++) {
            List<String> row = new ArrayList<>(cols);
            for (int j = 0; j < cols; j++) {
                row.add("");
            }
            reportTable.add(row);
        }
        
        ArrayList<Stage> stages = new ArrayList<>(experiment.getDataRows().keySet());
        stages.sort(Comparator.comparingInt(Enum::ordinal));
        
        String head =
                repeat(stages.stream().map(Stage::getName).collect(Collectors.joining(";")) + ";"
                        , (int) rowsVisibleProperties.stream().map(p -> p.get()).count());
        
        for (int s = 0; s < stages.size(); s++) {
            double[][] data = experiment.getDataByStage(stages.get(s));
            for (int i = 0; i < data.length; i++) {
                double[] row = data[i];
                for (int k = 0; k < row.length; k++) {
                    reportTable.get(k).set(experiment.getDataRows().keySet().size() * i + s,
                            Double.toString(row[k]));
                }
            }
        }
        
        List<String> report = new ArrayList<>();
        report.add(head);
        for (List<String> line : reportTable) {
            report.add(String.join(";", line));
        }
        
        try (PrintWriter writer = new PrintWriter("test.csv", "Cp1251")) {
            writer.print(String.join(System.lineSeparator(), report));
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
    private String repeat(String string, int n) {
        StringBuilder newString = new StringBuilder(string.length() * n);
        for (int i = 0; i < n; i++) {
            newString.append(string);
        }
        return newString.toString();
    }
}
