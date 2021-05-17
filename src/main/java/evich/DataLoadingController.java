package evich;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DataLoadingController implements Initializable
{
    public Button deleteButton;
    public ListView<DataFromFile> filesListView;
    public LineChart<Number, Number> previewChart;
    private ButtonType result = ButtonType.CANCEL;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filesListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    deleteButton.setDisable(newValue == null);
                    if (newValue != null) {
                        displayData(newValue.file.getName(), newValue.data);
                    }
                });
    }
    
    private void displayData(String name, double[] values) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);
        for (int i = 0; i < values.length; i++) {
            series.getData().add(new XYChart.Data<>(i, values[i]));
        }
        previewChart.getData().clear();
        previewChart.getData().add(series);
    }
    
    @FXML
    private void onAdd() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("").getAbsoluteFile());
        List<File> files = fileChooser.showOpenMultipleDialog(filesListView.getScene().getWindow());
        for (File file : files) {
            double[] data;
            try {
                data = IO.readDoubles(file);
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
                e.printStackTrace();
                continue;
            }
            boolean sizeError = false;
            for (DataFromFile item : filesListView.getItems()) {
                if (item.data.length != data.length) {
                    new Alert(Alert.AlertType.ERROR, "Ошибка размерности!").showAndWait();
                    sizeError = true;
                }
            }
            if (!sizeError) {
                filesListView.getItems().add(new DataFromFile(file, data));
            }
        }
    }
    
    @FXML
    private void onDelete() {
        DataFromFile selectedItem = filesListView.getSelectionModel().getSelectedItem();
        filesListView.getItems().remove(selectedItem);
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
        ((Stage) filesListView.getScene().getWindow()).close();
    }
    
    public double[][] getDataRows() {
        if (result == ButtonType.OK) {
            return filesListView.getItems().stream().map(dataFromFile -> dataFromFile.data).toArray(double[][]::new);
        } else {
            return null;
        }
    }
    
    private static class DataFromFile
    {
        File file;
        double[] data;
        
        DataFromFile(File file, double[] data) {
            this.file = file;
            this.data = data;
        }
        
        @Override
        public String toString() {
            return file.getName() + " (" + data.length + ")";
        }
    }
}
